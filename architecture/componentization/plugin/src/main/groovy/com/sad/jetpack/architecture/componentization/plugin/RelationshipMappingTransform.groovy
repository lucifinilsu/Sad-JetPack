package com.sad.jetpack.architecture.componentization.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.utils.FileUtils
import com.google.common.collect.Sets
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonWriter
import com.sad.jetpack.architecture.componentization.annotation.ExposedService
import com.sad.jetpack.lib.classscanner.ClassScanner
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import javassist.Modifier
import jdk.nashorn.internal.ir.debug.JSONWriter
import org.gradle.api.Project

class RelationshipMappingTransform extends Transform implements ClassScanner.OnFileScannedCallback, ClassScanner.ITarget{
    private Project project;
    private List<String> applicationIds=new ArrayList<>();
    private List<String> assetsDirs=new ArrayList<>();
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

   /*@Override
    Set<? super QualifiedContent.Scope> getReferencedScopes() {
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
    }*/

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
        /*return Collections.emptySet()*/
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


        /*for(String a:assetsDirs){

        }*/


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



    @Override
    void onScannedCompleted(ClassPool classPool) {
        /*if (handleErmUtils){
            CtMethod method = null;
            try{
                method=classErmUtils.getDeclaredMethod("ermPaths",classPool.get("java.lang.String"));
                for (String a:assetsDirs){

                }
                method.insertAt(32,"android.net.Uri uri=android.net.Uri.parse(\$\$);\n" +
                        "        String path=uri.getPath();\n" +
                        "        StringBuilder ermPath=new StringBuilder();\n" +
                        "        ermPath\n" +
                        "                .append(\"erm\"+ File.separator)\n" +
                        "                .append(InternalContextHolder.get().getContext().getPackageName())\n" +
                        "                .append(path.replace(\"/\",File.separator));\n" +
                        "        ")
            }catch(Exception e){
                project.logger.error(">> "+methodName+" method is not found")
            }
        }*/
    }

    private final static String ERM_DIR="erm"
    private File ermUtilsFile
    private File ermUtilsFileDest
    private boolean handleErmUtils=false
    private CtClass classErmUtils
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
        /*CtClass ermPathUtils = classPool.get("com.sad.jetpack.architecture.componentization.api.Utils")
        if (ermPathUtils!=null && scannedClass.subclassOf(ermPathUtils)){
            ermUtilsFile=scannedFile;
            ermUtilsFileDest=dest;
            handleErmUtils=true;
            classErmUtils=scannedClass
            return true
        }*/
        /*if (!avaliableClass(scannedClass)){
            return false;
        }*/
        ExposedService exposedAnnotation=scannedClass.getAnnotation(ExposedService.class);
        if (exposedAnnotation!=null){
            String url=exposedAnnotation.url()
            if (!ObjectUtils.isEmpty(url) /*&& ObjectUtils.isURL(url)*/){
                project.logger.error(">>> 目标url："+url)
                for (int i = 0; i < applicationIds.size(); i++) {
                    String[] assetsDs=exposedAnnotation.assetsDir()
                    if (!ObjectUtils.isEmpty(assetsDs)){
                        for (String a:assetsDs){
                            createMap(applicationIds.get(i),scannedClass,exposedAnnotation.url(),a,exposedAnnotation.description())
                        }
                    }
                }
                //scannedClass.writeFile(dest.getAbsolutePath())
                return false
            }
        }

        return false
    }

    private void createMap(String packageName,CtClass scannedClass,String u,String assetsDir,String description){

        if (ObjectUtils.isEmpty(u)){
            return
        }
        if (!ObjectUtils.isEmpty(assetsDir) && assetsDirs.indexOf(assetsDir)==-1){
            project.logger.error(">>> 清空目录："+assetsDir)
            StringBuilder sbERM=new StringBuilder()
            sbERM.append(project.buildDir.getParent()+File.separator)
                    .append(assetsDir)
                    .append(ERM_DIR)
            File f=new File(sbERM.toString());
            if (f.exists()){
                FileUtils.deleteDirectoryContents(f)
            }
            assetsDirs.add(assetsDir)
        }
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
            StringBuilder ermPath=new StringBuilder()
            ermPath
                .append(ERM_DIR+File.separator)
                .append(packageName)
                .append(path.replace("/",File.separator))
            sbApath
                .append(project.buildDir.getParent()+File.separator)
                .append(assetsDir)
                .append(ermPath.toString())


            String aPath=sbApath.toString()
            project.logger.error(">>> 目标映射路径："+aPath)
            File fa=new File(aPath)
            File dir=fa.getParentFile()
            if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()){
                String content=createJson(ermPath.toString(),scannedClass,u,description)
                project.logger.error(">>> 目标内容："+content)
                if(!fa.exists()){
                    FileUtils.createFile(fa,content)
                }
                else {
                    FileUtils.writeToFile(fa,content)
                }
            }
        }
    }

    private String createJson(String path,CtClass scannedClass,String u,String description){

        String j=""
        try {
            GsonBuilder gsonBuilder=new GsonBuilder()
            gsonBuilder.setPrettyPrinting()
            gsonBuilder.disableHtmlEscaping()
            Gson gson= gsonBuilder.create()
            JsonObject jsonObject=new JsonObject()
            jsonObject.addProperty("path",path)
            JsonObject jo_e=new JsonObject();
            jo_e.addProperty("url",u)
            jo_e.addProperty("class",scannedClass.getName())
            jo_e.addProperty("description",description)
            jsonObject.add("element",jo_e)
            j=gson.toJson(jsonObject)
        }catch(Exception e){
            e.printStackTrace();
        }
        return j
    }

}