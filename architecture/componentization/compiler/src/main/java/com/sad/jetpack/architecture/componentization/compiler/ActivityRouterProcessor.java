package com.sad.jetpack.architecture.componentization.compiler;

import com.google.auto.service.AutoService;
import com.sad.jetpack.architecture.componentization.annotation.ActivityRouter;
import com.sad.jetpack.architecture.componentization.annotation.Component;
import com.sad.jetpack.architecture.componentization.annotation.EncryptUtil;
import com.sad.jetpack.architecture.componentization.annotation.NameUtils;
import com.sad.jetpack.architecture.componentization.annotation.ValidUtils;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
@AutoService(Processor.class)
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@SupportedOptions({"log"})
@SupportedAnnotationTypes({
        Constant.PACKAGE_ANNOTATION +".ActivityRouter"
})
public class ActivityRouterProcessor extends AbsProcessor{
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        isLog=Boolean.valueOf(env.getOptions().getOrDefault("log","false"));
        log=new ProcessorLog(messager,isLog);
        log.config_err("log");
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotationedElements, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(annotationedElements)){
            Set<? extends Element> targetElements=roundEnvironment.getElementsAnnotatedWith(ActivityRouter.class);
            if (!CollectionUtils.isNotEmpty(targetElements)){
                log.error(">>>未发现注解ActivityRouter");
                return true;
            }
            for (Element element:targetElements
                 ) {
                if (element.getKind() != ElementKind.CLASS) {
                    log.error(">>>错误的注解类型，只有【类】才能够被该ActivityRouter注解处理");
                    continue;
                }
                Set<Modifier> mod=element.getModifiers();
                if (mod.contains(Modifier.ABSTRACT)){
                    log.error(">>>"+element.getSimpleName().toString()+"是抽象类，故无法进行注册:");
                    continue;
                }
                Element elementIAC_Activity=elementUtils.getTypeElement("android.app.Activity");
                if (!typeUtils.isSubtype(typeUtils.erasure(element.asType()),typeUtils.erasure(elementIAC_Activity.asType()))){
                    log.error(">>>错误的注解类型，"+element.getSimpleName().toString()+"不是android.app.Activity的子类，故无法进行注册:");
                    continue;
                }
                generateActivityClassSupplier((TypeElement) element);
            }
        }
        return false;
    }

    private void generateActivityClassSupplier(TypeElement element){
        ActivityRouter activityRouter=element.getAnnotation(ActivityRouter.class);
        String[] urls=activityRouter.url();
        int[] ps=activityRouter.priority();
        if (urls.length!=ps.length){
            log.error(">>>错误的注解参数值数组长度，"+element.getSimpleName().toString()+"的注解参数值数组长度分别是:url.size="+urls.length+",priority.size="+ps.length);
            return;
        }
        for (String url:urls
             ) {
            int p= ps[Arrays.asList(urls).indexOf(url)];
            String name= NameUtils.getActivityRouteProxyComponentClassSimpleName(element.getQualifiedName().toString(),EncryptUtil.getInstance().XORencode(url,"abc123"),"$$");
            TypeSpec.Builder tb=TypeSpec.classBuilder(name)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.bestGuess(Constant.PACKAGE_API+".extension.router.IActivityClassSupplier"))
                    .addAnnotation(AnnotationSpec.builder(Component.class)
                            .addMember("url","$S",url)
                            .build())
                    ;
            MethodSpec mb_targetClass=MethodSpec.methodBuilder("activityClass")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(ParameterizedTypeName.get(
                            ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(ClassName.bestGuess("android.app.Activity")))
                    )
                    .addStatement("return $T.class",element.asType())
                    .build()
                    ;
            MethodSpec mb_priority=MethodSpec.methodBuilder("intentPriority")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(int.class)
                    .addStatement("return $L",p)
                    .build()
                    ;
            tb.addMethod(mb_targetClass);
            tb.addMethod(mb_priority);
            String pkg_name=elementUtils.getPackageOf(element).getQualifiedName().toString();
            JavaFile.Builder jb= JavaFile.builder(pkg_name,tb.build())
                    ;
            try {
                jb.build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
