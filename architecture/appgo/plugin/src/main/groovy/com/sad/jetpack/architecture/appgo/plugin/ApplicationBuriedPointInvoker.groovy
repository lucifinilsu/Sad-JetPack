package com.sad.jetpack.architecture.appgo.plugin

import com.sad.jetpack.architecture.appgo.annotation.ApplicationLifeCycleActionConfig
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import javassist.CtNewMethod
import javassist.Modifier
import org.gradle.api.Project

class ApplicationBuriedPointInvoker {


    CtClass applicationClass
    ClassPool classPool
    Project mProject
    ArrayList<ApplicationLifeCycleActionConfig> lifeCycleActionConfigList
    String contentMethod = "INIT_CONTENT"

    ApplicationBuriedPointInvoker(Project project, CtClass ctClass, ArrayList<ApplicationLifeCycleActionConfig> lifeCycleActionConfigList, ClassPool classPool) {
        this.applicationClass = ctClass
        this.classPool = classPool
        this.mProject = project
        this.lifeCycleActionConfigList = lifeCycleActionConfigList
    }

    void handleActivitySaveState() {

        //mProject.logger.error("ApplicationTransform ${appCtClass.name} ")

        CtMethod createCtMethod = applicationClass.declaredMethods.find {
            it.name == "onCreate" && it.parameterTypes == [] as CtClass[]
        }
      //  mProject.logger.error("ApplicationTransform ${createCtMethod} ")
        String content = ""

        try {
            content = JSON.toJSON(lifeCycleActionConfigList)
        } catch (Exception e) {
            e.printStackTrace()
        }
        generateEnabledField(applicationClass, content)

        if (createCtMethod == null) {//application 没有 onCreate 方法
            createCtMethod = CtNewMethod.make(generateActivityRestoreMethod(), applicationClass)
            applicationClass.addMethod(createCtMethod)
        }
        createCtMethod.insertBefore("com.renny.mylibrary.InitManager.addPath(${contentMethod});\n" +
                "com.renny.mylibrary.InitManager.doInit(this);\n")
    }

    void generateEnabledField(CtClass ctClass, String path) {
        CtField pathCtField = ctClass.declaredFields.find {
            it.name == contentMethod && it.getType().name == "java.lang.String"
        }
        if (pathCtField != null) {
            ctClass.removeField(pathCtField)
        }
        pathCtField = new CtField(classPool.get("java.lang.String"), contentMethod, ctClass)
        pathCtField.setModifiers(Modifier.PRIVATE | Modifier.STATIC)
        ctClass.addField(pathCtField, CtField.Initializer.constant(path))
    }


    static String generateActivityRestoreMethod() {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append("public void onCreate() {\n ")
        stringBuilder.append("super.onCreate();\n")
        stringBuilder.append("}")
        return stringBuilder.toString()
    }
}
