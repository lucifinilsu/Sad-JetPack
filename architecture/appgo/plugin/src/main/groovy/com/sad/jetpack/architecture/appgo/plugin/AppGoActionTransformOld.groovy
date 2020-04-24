package com.sad.jetpack.architecture.appgo.plugin
import com.android.build.api.transform.*
import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleAction
import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleActionConfig
import com.google.common.collect.Sets
import groovy.io.FileType
import javassist.ClassPath
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.lang.reflect.Constructor

public class AppGoActionTransformOld extends Transform{

    Project mProject
    CtClass applicationClass
    File appDest
    def applicationLifeCycleActionConfigsList = new ArrayList<ApplicationLifeCycleActionConfig>()


    AppGoActionTransformOld(Project project) {
        mProject = project
    }

    @Override
    String getName() {
        return "AppGoAction_ApplicationOnCreatedPreTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        if (mProject.plugins.hasPlugin("com.android.application")) {
            return Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT,
                    QualifiedContent.Scope.SUB_PROJECTS,
                    QualifiedContent.Scope.EXTERNAL_LIBRARIES,
                    QualifiedContent.Scope.PROVIDED_ONLY
            )
        } else if (mProject.plugins.hasPlugin("com.android.library") || mProject.plugins.hasPlugin("java-library")) {
            return Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT,
                    //2020.04.23增加
                    QualifiedContent.Scope.SUB_PROJECTS,
                    QualifiedContent.Scope.EXTERNAL_LIBRARIES,
                    QualifiedContent.Scope.PROVIDED_ONLY
            )
        } else {
            return Collections.emptySet()
        }
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        ClassPool classPool = ClassPool.getDefault()

        def classPath = []

        classPool.appendClassPath(mProject.android.bootClasspath[0].toString())

        try {
            Class jarClassPathClazz = Class.forName("javassist.JarClassPath")
            Constructor constructor = jarClassPathClazz.getDeclaredConstructor(String.class)
            constructor.setAccessible(true)

            transformInvocation.inputs.each { input ->


                def subProjectInputs = []

                input.jarInputs.each { jarInput ->
                    // mProject.logger.error("jar input=   " + jarInput.file.getAbsolutePath())
                    ClassPath clazzPath = (ClassPath) constructor.newInstance(jarInput.file.absolutePath)
                    classPath.add(clazzPath)
                    classPool.appendClassPath(clazzPath)

                    def jarName = jarInput.name
                    if (jarName.endsWith(".jar")) {
                        jarName = jarName.substring(0, jarName.length() - 4)
                    }
                    //mProject.logger.error("jar name   " + jarName)
                    if (jarName.startsWith(":")) {
                        // mProject.logger.error("jar name startsWith冒号   " + jarName)
                        //handle it later, after classpath set
                        subProjectInputs.add(jarInput)
                    } else {
                        def dest = transformInvocation.outputProvider.getContentLocation(jarName,
                                jarInput.contentTypes, jarInput.scopes, Format.JAR)
                        // mProject.logger.error("jar output path:" + dest.getAbsolutePath())
                        FileUtils.copyFile(jarInput.file, dest)
                    }
                }

                // Handle library project jar here
                subProjectInputs.each { jarInput ->

                    def jarName = jarInput.name
                    if (jarName.endsWith(".jar")) {
                        jarName = jarName.substring(0, jarName.length() - 4)
                    }

                    if (jarName.startsWith(":")) {
                        // sub project
                        File unzipDir = new File(
                                jarInput.file.getParent(),
                                jarName.replace(":", "") + "_unzip")
                        if (unzipDir.exists()) {
                            unzipDir.delete()
                        }
                        unzipDir.mkdirs()
                        Decompression.uncompress(jarInput.file, unzipDir)

                        File repackageFolder = new File(
                                jarInput.file.getParent(),
                                jarName.replace(":", "") + "_repackage"
                        )

                        FileUtils.copyDirectory(unzipDir, repackageFolder)

                        unzipDir.eachFileRecurse(FileType.FILES) { File it ->
                            checkAndTransformClass(classPool, it, repackageFolder)
                        }

                        // re-package the folder to jar
                        def dest = transformInvocation.outputProvider.getContentLocation(
                                jarName, jarInput.contentTypes, jarInput.scopes, Format.JAR)

                        Compressor zc = new Compressor(dest.getAbsolutePath())
                        zc.compress(repackageFolder.getAbsolutePath())
                    }
                }

                input.directoryInputs.each { dirInput ->
                    def outDir = transformInvocation.outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                    classPool.appendClassPath(dirInput.file.absolutePath)
                    // dirInput.file is like "build/intermediates/classes/debug"
                    int pathBitLen = dirInput.file.toString().length()

                    def callback = { File it ->
                        def path = "${it.toString().substring(pathBitLen)}"
                        if (it.isDirectory()) {
                            new File(outDir, path).mkdirs()
                        } else {
                            boolean handled = checkAndTransformClass(classPool, it, outDir)
                            if (!handled) {
                                // copy the file to output location
                                new File(outDir, path).bytes = it.bytes
                            }
                        }
                    }
                    if (dirInput.changedFiles == null || dirInput.changedFiles.isEmpty()) {
                        dirInput.file.traverse(callback)
                    } else {
                        dirInput.changedFiles.keySet().each(callback)
                    }
                }
            }

            if (applicationClass != null) {
                mProject.logger.error("appCtClass==  ${applicationClass.name}    size" + applicationLifeCycleActionConfigsList.size())
                if (applicationLifeCycleActionConfigsList.size() > 0) {
                    ApplicationBuriedPointInvoker applicationTransform = new ApplicationBuriedPointInvoker(mProject,
                            applicationClass, applicationLifeCycleActionConfigsList, classPool)
                    applicationTransform.handleActivitySaveState()
                }
                applicationClass.writeFile(appDest.absolutePath)
                applicationClass.detach()
            }
        } finally {
            classPath.each { it ->
                classPool.removeClassPath(it)
            }
        }

    }


    boolean checkAndTransformClass(ClassPool classPool, File file, File dest) {


        CtClass applicationParentClass = classPool.get("android.app.Application")
        CtClass lifecyclesObserverInterface = classPool.get("com.sad.jetpack.architecture.appgo.api.IApplicationLifecyclesObserver")

        mProject.logger.error("applicationParentClass  ${applicationParentClass.name}")
        mProject.logger.error("lifecyclesObserverInterface  ${lifecyclesObserverInterface.name}")


        classPool.importPackage("android.os")
        classPool.importPackage("android.util")

        if (!file.name.endsWith("class")) {
            return false
        }

        CtClass scanedClass
        try {
            scanedClass = classPool.makeClass(new FileInputStream(file))
        } catch (Throwable throwable) {
            mProject.logger.error("Parsing class file ${file.getAbsolutePath()} fail.", throwable)
            return false
        }

        mProject.logger.error("checkAndTransformClass  ${scanedClass.name}")
        boolean handled = false
        //扫描到Application子类
        if (applicationParentClass != null && scanedClass.subclassOf(applicationParentClass)) {
            mProject.logger.error("Find Application：${scanedClass.getAnnotation(ApplicationAccess.class)} ")
            Object accessAnnotation=scanedClass.getAnnotation(ApplicationAccess.class);
            if (accessAnnotation!=null){
                applicationClass = scanedClass
                appDest = dest
                handled = true
            }
            else {
                return false;
            }
        }
        //扫描到IApplicationLifecyclesObserver的实现类
        if (lifecyclesObserverInterface != null && scanedClass.getInterfaces().contains(lifecyclesObserverInterface)) {
            boolean isHandleThisObserverClass=false
            //剔除没有任何注解埋点方法的的监听
            CtMethod[] methods = scanedClass.getDeclaredMethods()
            for (CtMethod method:methods){
                Object annotation = method.getAnnotation(ApplicationLifeCycleAction.class)
                if (annotation!=null){
                    isHandleThisObserverClass=true
                    break
                }
            }
            if (isHandleThisObserverClass){
                def applicationLifeCycleActionConfig=new ApplicationLifeCycleActionConfig()
                applicationLifeCycleActionConfig.setPath(scanedClass.name)
            }






            /*def applicationLifeCycleActionConfig=new ApplicationLifeCycleActionConfig()
            applicationLifeCycleActionConfig.setPath(scanedClass.name)
            Object annotation =  scanedClass.getAnnotation(ApplicationLifeCycleAction.class)
            mProject.logger.error("Find IApplicationLifecyclesObserver：${scanedClass.getAnnotation(ApplicationLifeCycleAction.class)} ")
            if (annotation != null) {
                ApplicationLifeCycleAction actionAnn = (ApplicationLifeCycleAction)annotation
                applicationLifeCycleActionConfig.setProcessName(actionAnn.processName())
                applicationLifeCycleActionConfig.setPriority(actionAnn.priority())
            }
            this.applicationLifeCycleActionConfigsList.add(applicationLifeCycleActionConfig)
            mProject.logger.error("FindAppInit  ${scanedClass.name} ")*/
        }

        return handled
    }

}