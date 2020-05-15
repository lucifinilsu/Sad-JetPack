package com.sad.jetpack.architecture.componentization.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.utils.FileUtils
import com.google.common.collect.Sets
import com.sad.jetpack.architecture.componentization.annotation.ExposedService
import com.sad.jetpack.lib.classscanner.ClassScanner
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import javassist.Modifier
import org.gradle.api.Project

class RelationshipMappingTransform extends Transform implements ClassScanner.OnFileScannedCallback, ClassScanner.ITarget{
    private Project project;
    private List<String> applicationIds=new ArrayList<>();
    RelationshipMappingTransform(Project project){
        this.project=project;
    }

    @Override
    String getName() {
        return "RelationshipMappingTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        if (project.plugins.hasPlugin("com.android.application")) {
            return Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT,
                    QualifiedContent.Scope.SUB_PROJECTS,
                    QualifiedContent.Scope.EXTERNAL_LIBRARIES)
        } else if (project.plugins.hasPlugin("com.android.library") ||project.plugins.hasPlugin("java-library")) {
            return Sets.immutableEnumSet(
                    QualifiedContent.Scope.PROJECT)
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
        super.transform(transformInvocation)
        project.android.applicationVariants.all { variant ->
            def applicationId= [variant.mergedFlavor.applicationId, variant.buildType.applicationIdSuffix].findAll().join()
            if (!applicationIds.contains(applicationId)){
                applicationIds.add(applicationId)
            }
        }
        project.logger.error(">>>applicationIds is "+applicationIds)
        ClassPool classPool = ClassPool.getDefault()
        classPool.appendClassPath(project.android.bootClasspath[0].toString())
        ClassScanner.newInstance(project)
                .classPool(classPool)
                .transformInvocation(transformInvocation)
                .scannedCallback(this)
                .into(this)
    }

    private boolean avaliableClass(CtClass ctClass){
        return !Modifier.isAbstract(ctClass.getModifiers()) && !Modifier.isInterface(ctClass.getModifiers())
    }

    void testGotoAssetDir(){
        File b=project.buildDir
        String ap=b.getParent()+File.separator+"src"+File.separator+"main"+File.separator+"assets";
        File apF=new File(ap);
        if ((apF.exists() && apF.isDirectory()) || apF.mkdirs()){
            String t=ap+File.separator+"aaa.json"
            File tf=new File(t);
            if (!tf.exists()){
                tf.createNewFile();
            }
            FileUtils.writeToFile(tf,"{'s':6}")
        }
    }

    @Override
    void onScannedCompleted(ClassPool classPool) {

    }

    private final static String ERM_DIR="erm"
    @Override
    boolean onScanned(ClassPool classPool, File scannedFile, File dest) {
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
        if (!avaliableClass(scannedClass)){
            return false;
        }
        ExposedService exposedAnnotation=scannedClass.getAnnotation(ExposedService.class);
        if (exposedAnnotation!=null){
            String url=exposedAnnotation.url()
            if (!ObjectUtils.isEmpty(url) && ObjectUtils.isURL(url)){
                project.logger.error(">>> 目标url："+url)
                for (int i = 0; i < applicationIds.size(); i++) {
                    createMap(applicationIds.get(i),url)
                }
            }
        }
        return false
    }

    private void createMap(String packageName,String u){
        URI uri=new URI(u);
        String protocol=uri.getScheme()
        String host=uri.getHost()
        String path=uri.getPath()
        //project.logger.error(">>> 目标路径："+new URL(u).getPath())
        String name=path.substring(path.lastIndexOf('/')+1)
        project.logger.error(">>> 目标名称："+name)
        String query=uri.getQuery()
        if (!ObjectUtils.isEmpty(name)){
            project.logger.error(">>> 模块路径："+project.buildDir.getParent())
            StringBuilder sbApath=new StringBuilder()
            sbApath.append(project.buildDir.getParent()+File.separator)
                .append("src"+File.separator)
                .append("main"+File.separator)
                .append("assets"+File.separator)
                .append(ERM_DIR+File.separator)
                .append(packageName)
                .append(path.replace("/",File.separator))

            String aPath=sbApath.toString()
            project.logger.error(">>> 目标映射路径："+aPath)
            File fa=new File(aPath);
            File dir=fa.getParentFile();
            if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()){
                if(!fa.exists()){
                    FileUtils.createFile(fa,"xxx")
                }
                else {
                    FileUtils.writeToFile(fa,"yyy")
                }
            }
        }
    }

}