package com.sad.jetpack.architecture.componentization.compiler;

import com.google.auto.service.AutoService;
import com.sad.jetpack.architecture.componentization.annotation.EncryptUtil;
import com.sad.jetpack.architecture.componentization.annotation.IPCChat;
import com.sad.jetpack.architecture.componentization.annotation.NameUtils;
import com.sad.jetpack.architecture.componentization.annotation.ValidUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.List;
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
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@SupportedOptions({"log"})
@SupportedAnnotationTypes({
        Constant.PACKAGE_ANNOTATION +".IPCChat"
})
public class IPCChatProcessoer extends AbsProcessor {
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        isLog=Boolean.valueOf(env.getOptions().getOrDefault("log","false"));
        log=new ProcessorLog(messager,isLog);
        log.config_err("log");
    }
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        if (!CollectionUtils.isEmpty(set)){
            Set<? extends Element> allElements=roundEnv.getElementsAnnotatedWith(IPCChat.class);
            for (Element e_method:allElements
            ){
                if (e_method.getKind() != ElementKind.METHOD) {
                    log.error(String.format("错误的注解类型，只有【方法】才能够被该 @%s 注解处理", IPCChat.class.getSimpleName()));
                    return true;
                }
                Set<Modifier> modifiers=e_method.getModifiers();
                if (!modifiers.contains(Modifier.PUBLIC)){
                    log.error(String.format(e_method.getSimpleName().toString()+"方法使用了不恰当的作用域，只有Public方法才能够被该 @%s 注解处理", IPCChat.class.getSimpleName()));
                    return true;
                }
                ExecutableElement executable_e_method= (ExecutableElement) e_method;

                IPCChat annotation_eventResponse=e_method.getAnnotation(IPCChat.class);
                TypeElement e_class= (TypeElement) e_method.getEnclosingElement();
                generateNewAbstractParasiticComponent(annotation_eventResponse,executable_e_method,e_class);
            }
        }
        return false;
    }

    private void generateNewAbstractParasiticComponent(IPCChat annotation_eventResponse,ExecutableElement executable_e_method,TypeElement e_class){
        for (String e_name:annotation_eventResponse.url()
             ) {
            String u_name= EncryptUtil.getInstance().XORencode(e_name,"abc123");//ValidUtils.encryptMD5ToString(e_name);
            String dynamicComponentClsName= NameUtils.getParasiticComponentClassSimpleName(e_class.getQualifiedName().toString()+"."+executable_e_method.getSimpleName(),u_name,"$$");
            String pkgName=elementUtils.getPackageOf(e_class).getQualifiedName().toString();
            List<? extends VariableElement> listParams=executable_e_method.getParameters();
            boolean isHasReturnData=false;
            //检查被注解的方法参数
            if (listParams.size()!=1){
                log.error(String.format("错误的方法参数数目，@%s方法只能有且仅有一个参数",executable_e_method.getSimpleName().toString()));
                return;
            }
            Element elementIAC_0=elementUtils.getTypeElement(Constant.PACKAGE_API+".IPCMessenger");
            if (!typeUtils.isSubtype(typeUtils.erasure(listParams.get(0).asType()),typeUtils.erasure(elementIAC_0.asType()))){
                log.error(String.format("错误的方法参数类型，@%s方法第一个参数必须是IPCMessenger类型",executable_e_method.getSimpleName().toString()));
                return;
            }
            //检查返回类型
            Element elementIAC_return=elementUtils.getTypeElement(Void.class.getCanonicalName());
            TypeMirror re =executable_e_method.getReturnType();
            isHasReturnData=!"void".equals(typeUtils.erasure(re).toString());
            CodeBlock codeInvokeHostMethod=null;
            if (isHasReturnData){
                codeInvokeHostMethod=CodeBlock.builder()
                        .addStatement("$T returnData=getHost().$L(messenger)",
                                re,
                                executable_e_method.getSimpleName()
                        )
                        .addStatement("return returnData")
                        .build();
            }
            else {
                codeInvokeHostMethod=CodeBlock.builder()
                        .addStatement("getHost().$L(messenger)",
                                executable_e_method.getSimpleName()
                        )
                        .build();
            }

            MethodSpec ms_onComponentResponse=MethodSpec.methodBuilder("action")
                    .addAnnotation(Override.class)
                    .returns(isHasReturnData?TypeVariableName.get(re): TypeName.OBJECT)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(ClassName.bestGuess(Constant.PACKAGE_API+".IPCMessenger"),"messenger").build())
                    .beginControlFlow("try")
                    .addCode(codeInvokeHostMethod)
                    .endControlFlow("catch($T e){e.printStackTrace();}", Exception.class)
                    .addStatement("return null")
                    .build();
            MethodSpec ms_priority=MethodSpec.methodBuilder("priority")
                    .addAnnotation(Override.class)
                    .returns(TypeName.INT)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return "+annotation_eventResponse.priority())
                    .build();

            MethodSpec ms_c=MethodSpec.constructorBuilder()
                    .addParameter(ParameterSpec.builder(TypeVariableName.get(e_class.asType()),"host").build())
                    .addParameter(ParameterSpec.builder(IPCChat.class,"chat").build())
                    .addStatement("super(host,chat)")
                    .build()
                    ;

            TypeSpec.Builder tb=TypeSpec.classBuilder(dynamicComponentClsName)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(ms_c)
                    .addMethod(ms_onComponentResponse)
                    .addMethod(ms_priority)
                    .superclass(ParameterizedTypeName.get(ClassName.bestGuess(Constant.PACKAGE_API+".impl.IPCChatProxy"),
                            TypeVariableName.get(e_class.asType())
                    ))

                    ;


            JavaFile.Builder jb= JavaFile.builder(pkgName,tb.build())
                    //.addStaticImport(ClassName.bestGuess(Constant.PACKAGE_SAD_ARCHITECTURE_API_COMPONENTIZATION+".ComponentState"))
                    ;
            try {
                jb.build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
