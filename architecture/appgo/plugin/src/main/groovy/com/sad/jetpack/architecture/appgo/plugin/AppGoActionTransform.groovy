package com.sad.jetpack.architecture.appgo.plugin


import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.google.common.collect.Sets
import com.sad.jetpack.architecture.appgo.annotation.ApplicationAccess
import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleAction
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

import java.lang.reflect.Method

class AppGoActionTransform extends Transform implements ClassScanner.OnFileScannedCallback{
    private Project project

    AppGoActionTransform(Project project){
        this.project=project
    }
    @Override
    String getName() {
        return "AppGoActionTransform"
    }

    //需要处理的数据类型，有两种枚举类型
    //CLASSES和RESOURCES，CLASSES代表处理的java的class文件，RESOURCES代表要处理java的资源
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        Collections.singleton(QualifiedContent.DefaultContentType.CLASSES)
    }

    //    EXTERNAL_LIBRARIES        只有外部库
    //    PROJECT                       只有项目内容
    //    PROJECT_LOCAL_DEPS            只有项目的本地依赖(本地jar)
    //    PROVIDED_ONLY                 只提供本地或远程依赖项
    //    SUB_PROJECTS              只有子项目。
    //    SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
    //    TESTED_CODE                   由当前变量(包括依赖项)测试的代码
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        Sets.immutableEnumSet(
                QualifiedContent.Scope.PROJECT,
                QualifiedContent.Scope.SUB_PROJECTS,
                QualifiedContent.Scope.EXTERNAL_LIBRARIES
                //QualifiedContent.Scope.PROVIDED_ONLY
        )
    }

    //指明当前Transform是否支持增量编译
    @Override
    boolean isIncremental() {
        return false
    }
    /*private CtClass applicationParentClass
    private CtClass lifecyclesObserverInterface*/
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        ClassPool classPool = ClassPool.getDefault()
        //project.logger.error("==>project.android.bootClasspath="+project.android.bootClasspath)
        classPool.appendClassPath(project.android.bootClasspath[0].toString())
        ClassScanner.scan(project,classPool,transformInvocation,this);
    }




    @Override
    boolean onScanned(ClassPool classPool, File scannedFile, File dest,ClassScanResult scanResult) {

        CtClass applicationParentClass = classPool.get("android.app.Application")
        CtClass lifecyclesObserverInterface = classPool.get("com.sad.jetpack.architecture.appgo.api.IApplicationLifecyclesObserver")
        classPool.importPackage("android.os")
        classPool.importPackage("android.util")

        if (!scannedFile.name.endsWith("class")) {
            return false
        }
        //获取扫描到的class
        CtClass scannedClass=null
        try {
            scannedClass = classPool.makeClass(new FileInputStream(scannedFile))
        } catch (Throwable throwable) {
            project.logger.error("Parsing class file ${scannedFile.getAbsolutePath()} fail.", throwable)
            return false
        }
        boolean handled = false
        //如果扫描到的class是Application的子类，则准备处理
        if (applicationParentClass != null && scannedClass.subclassOf(applicationParentClass)){
            Object accessAnnotation=scannedClass.getAnnotation(ApplicationAccess.class);
            if(accessAnnotation!=null){
                //Application的子类被注解过了，说明是可埋点的宿主，记录并等待处理
                project.logger.error("------------->Host Application is "+scannedClass.name)
                scanResult.setApplicationClass(scannedClass);
                scanResult.setDest(dest)
                scanResult.setHandled(true)
                handled=true;
            }
        }

        //如果扫描到的类是Application行为监听实现类
        if (lifecyclesObserverInterface != null && scannedClass.getInterfaces().contains(lifecyclesObserverInterface)){
            boolean isHandleThisObserverClass=false
            project.logger.error("------------->find observer interface"+scannedClass.name)
            //剔除没有任何显式注解埋点方法的的监听
            CtMethod[] methods = scannedClass.getDeclaredMethods()
            for (CtMethod method:methods){
                Object annotation = method.getAnnotation(ApplicationLifeCycleAction.class)
                if (annotation!=null){
                    project.logger.error("------------->find anchord method:"+method.name)
                    isHandleThisObserverClass=true
                    break
                }
            }
            if (isHandleThisObserverClass){
                scanResult.getLifecyclesObserverClassList().add(scannedClass)
            }
        }
        return handled
    }

    @Override
    void onScannedCompleted(ClassPool classPool,ClassScanResult scanResult) {
        if (scanResult.getHandled()){
            setAnchorOnApplicationCreated(classPool,scanResult)
            //将埋点后的流写入class文件
            scanResult.getApplicationClass().writeFile(scanResult.getDest().absolutePath)
            scanResult.getApplicationClass().detach()
        }
    }


    private void setAnchorOnApplicationCreated(ClassPool classPool,ClassScanResult scanResult){
        String method="onCreate"
        String defaultSuperMethodCode=
                "public void onCreate(){\n" +
                "        super.onCreate();\n" +
                "}"
        boolean afterOrg=true;
        ArrayList<CtClass> org=scanResult.getLifecyclesObserverClassList();
        ArrayList<CtClass> applicationLifeCycleObserverList =sort(org,"onApplicationCreated",classPool.get("android.app.Application"))
        String anchorCode="";
        StringBuilder ps = new StringBuilder()

        for (CtClass observerClass:applicationLifeCycleObserverList){
            ApplicationLifeCycleAction action=observerClass.getDeclaredMethod("onApplicationCreated",classPool.get("android.app.Application")).getAnnotation(ApplicationLifeCycleAction.class)
            if (action!=null){
                String newObj="new "+observerClass.name+"()"
                String processNamesArray=""

                //ps.append("com.sad.jetpack.architecture.appgo.api.ApplicationLifecycleObserverMaster.doOnCreatedAnchor(this, "+newObj+",")
                String[] processes=action.processName()
                boolean hasIncludeProcess=(processes!=null && (processes.length>0));
                project.logger.error("------------->include processNames? "+hasIncludeProcess+" : "+processes)
                //ps.append("new java.lang.String[]{")
                if (hasIncludeProcess){
                    ps.append("java.lang.String[] pNames=new java.lang.String["+processes.length+"];")
                    for (int i = 0; i < processes.length; i++) {
                        /*String sp=i==processes.length-1?"":","
                        String temp='"'+processes[i]+'"'+sp;
                        ps.append(temp)*/
                        ps.append("pNames["+i+"]="+"\""+processes[i]+"\";\n")
                    }
                    processNamesArray="pNames"
                }
                else {
                    processNamesArray="null"
                }
                ps.append("com.sad.jetpack.architecture.appgo.api.ApplicationLifecycleObserverMaster.doOnCreatedAnchor(this,"+newObj+","+processNamesArray+");")
                //ps.append("});")
            }
        }
        anchorCode=
                //"java.lang.String[] ss2=new java.lang.String[]{\"hello\"};\n"
                //"java.lang.String[] s=new java.lang.String[]{});"
                ps.toString()
        setAnchor(scanResult,method,defaultSuperMethodCode,anchorCode,afterOrg,null)
    }

    private void setAnchor(
            ClassScanResult scanResult,
            String mn,
            String defaultSuperMethodCode,
            String anchorCode,
            boolean afterOrg,
            CtClass... params){
        CtClass applicationClass=scanResult.getApplicationClass();
        CtMethod method = applicationClass.getDeclaredMethod(mn,params);
        if (method==null){
            method = CtNewMethod.make(defaultSuperMethodCode,applicationClass)
            applicationClass.addMethod(method)
        }
        project.logger.error("------------->"+mn+"'s anchord code is=\n"+anchorCode)
        if (afterOrg){
            method.insertAfter(anchorCode)
        }
        else {
            method.insertBefore(anchorCode)
        }

        /*method.insertBefore("com.renny.mylibrary.InitManager.addPath(${contentMethod});\n" +
                "com.renny.mylibrary.InitManager.doInit(this);\n")*/

                /*applicationClass.declaredMethods.find {
            it.name == "onCreate" && it.parameterTypes == [] as CtClass[]
        }*/
    }

    private ArrayList<CtClass> sort(ArrayList<CtClass> classList,String methodName,CtClass... params){
        ArrayList<CtClass> target=new ArrayList<>(classList)
        Comparator comparator=new Comparator<CtClass>() {


            @Override
            public int compare(CtClass o1, CtClass o2) {
                try {
                    Method method2=o2.getDeclaredMethod(methodName,params)
                    Method method1=o1.getDeclaredMethod(methodName,params);
                    if (method2==null || method2.getAnnotation(ApplicationLifeCycleAction.class)==null){
                        return -1
                    }
                    if (method1==null || method1.getAnnotation(ApplicationLifeCycleAction.class)==null){
                        return 1
                    }
                    ApplicationLifeCycleAction priority1=method1.getAnnotation(ApplicationLifeCycleAction.class)
                    ApplicationLifeCycleAction priority2=method2.getAnnotation(ApplicationLifeCycleAction.class)
                    return (int) (priority2.priority()-priority1.priority())

                } catch (Exception e) {
                    e.printStackTrace()
                }
                return 0
            }
        };
        Collections.sort(target,comparator)
        return target
    }

    /*static String generateNewOnCreateMethod(String superMethodCode) {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append(superMethodCode)
        *//*stringBuilder.append("public void onCreate() {\n ")
        stringBuilder.append("super.onCreate();\n")
        stringBuilder.append("}")*//*
        return stringBuilder.toString()
    }*/
    /*private void generateEnabledField(CtClass ctClass, String path) {
        CtField pathCtField = ctClass.declaredFields.find {
            it.name == contentMethod && it.getType().name == "java.lang.String"
        }
        if (pathCtField != null) {
            ctClass.removeField(pathCtField)
        }
        pathCtField = new CtField(classPool.get("java.lang.String"), contentMethod, ctClass)
        pathCtField.setModifiers(Modifier.PRIVATE | Modifier.STATIC)
        ctClass.addField(pathCtField, CtField.Initializer.constant(path))
    }*/
}