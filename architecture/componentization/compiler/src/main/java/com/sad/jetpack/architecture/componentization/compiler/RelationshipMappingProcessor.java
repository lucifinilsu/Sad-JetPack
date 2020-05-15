package com.sad.jetpack.architecture.componentization.compiler;

import com.google.auto.service.AutoService;
import com.sad.jetpack.architecture.componentization.annotation.ExposedService;
import com.squareup.javapoet.CodeBlock;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
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

/**
 * Created by Administrator on 2019/4/8 0008.
 */
@AutoService(Processor.class)
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@SupportedOptions({"log"})
@SupportedAnnotationTypes({
        Constant.PACKAGE__ANNOTATION +".ExposedService"
})
public class RelationshipMappingProcessor extends AbsProcessor{

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        isLog=Boolean.valueOf(env.getOptions().getOrDefault("log","false"));
        log=new ProcessorLog(messager,isLog);
        log.config_err("log");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotationedElements, RoundEnvironment roundEnv) {
        if (CollectionUtils.isNotEmpty(annotationedElements)){
            //第一步，解析ExposedService注解的类
            Set<? extends Element> listServiceElements=roundEnv.getElementsAnnotatedWith(ExposedService.class);
            if (!CollectionUtils.isNotEmpty(listServiceElements)){
                log.error(">>>未发现注解ExposedService");
                return true;
            }
            for (Element element:listServiceElements
                 ) {
                if (element.getKind() != ElementKind.CLASS) {
                    log.error(">>>错误的注解类型，只有【类】才能够被该ExposedService注解处理");
                    continue;
                }
                Set<Modifier> mod=element.getModifiers();
                if (mod.contains(Modifier.ABSTRACT)){
                   log.error(">>>"+element.getSimpleName().toString()+"是抽象类，故无法进行注册:");
                   continue;
                }

                try {
                    appendToDoc(element);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        if (roundEnv.processingOver()){
            //结束解析，生成注册类

        }
        return false;
    }

    private final static String dir="";
    private void appendToDoc(Element element) throws Exception{
        ExposedService annotation=element.getAnnotation(ExposedService.class);
        String u=annotation.url();
        String des=annotation.description();
        URL url=new URL(u);
        String protocol=url.getProtocol();
        String host=url.getHost();
        String path=url.getPath();
        String name=path.substring(path.lastIndexOf('/')+1);
        String query=url.getQuery();


    }


}
