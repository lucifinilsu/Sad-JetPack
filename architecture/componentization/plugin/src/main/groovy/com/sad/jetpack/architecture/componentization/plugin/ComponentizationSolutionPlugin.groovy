package com.sad.jetpack.architecture.componentization.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project;
class ComponentizationSolutionPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        if (project.plugins.hasPlugin("com.android.application")) {
            //project.logger.error(">>>appid is "+project.android.defaultConfig.applicationId)
            project.logger.error(">> componentization plugin is running in ["+project.getName()+"]-["+project.getRootProject()+"]")
            project.android.registerTransform(new RelationshipMappingTransform(project))
            project.android.registerTransform(new ComponentRegisterTransform(project))
            /*project.afterEvaluate{
            project.android.applicationVariants.all { variant ->
                def applicationId = [variant.mergedFlavor.applicationId, variant.buildType.applicationIdSuffix].findAll().join()
                project.logger.error(">>>appid is "+applicationId)
            }
            }*/
        }
    }
}