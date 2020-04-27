package com.sad.jetpack.architecture.appgo.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project;
public class AppGoAction implements Plugin<Project> {

    @Override
    void apply(Project project) {
        /*if (project.plugins.hasPlugin("com.android.application")
                || project.plugins.hasPlugin("com.android.library")
                || project.plugins.hasPlugin("java-library")) {


        }

        if (project.plugins.hasPlugin("com.android.application")) {
            project.android.registerTransform(new AppGoActionTransformOld(project))
        }*/


        project.dependencies {
            api "com.sad.jetpack.architecture.appgo:api:1.0.9"//rootProject.ext.dependencies["appgo_api"]
        }
        project.logger.error(">> appgo plugin is running")
        project.android.registerTransform(new AppGoActionTransform(project))
    }
}