package com.sad.jetpack.architecture.componentization.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.utils.FileUtils
import com.google.common.collect.Sets
import com.sad.jetpack.lib.classscanner.ClassScanner
import javassist.ClassPool
import javassist.CtClass
import javassist.Modifier
import org.gradle.api.Project

class ComponentRegisterTransform extends Transform{
    private Project project;

    ComponentRegisterTransform(Project project){
        this.project=project;
    }

    @Override
    String getName() {
        return "ComponentRegisterTransform"
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
        project.logger.error(">>> ComponentRegisterTransform'method transform is called ")
        ClassPool classPool = ClassPool.getDefault()
        classPool.appendClassPath(project.android.bootClasspath[0].toString())
        ClassScanner.newInstance(project)
                .classPool(classPool)
                .transformInvocation(transformInvocation)
                .scannedCallback(new ClassScanner.OnFileScannedCallback(){

                    @Override
                    boolean onScanned(ClassPool pool, File scannedFile, File dest) {

                        return false
                    }
                })
                .into(null)

    }


}