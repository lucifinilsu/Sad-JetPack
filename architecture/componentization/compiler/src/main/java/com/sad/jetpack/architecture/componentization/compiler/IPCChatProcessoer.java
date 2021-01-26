package com.sad.jetpack.architecture.componentization.compiler;

import com.google.auto.service.AutoService;
import com.sad.jetpack.architecture.componentization.annotation.Data;
import com.sad.jetpack.architecture.componentization.annotation.Description;
import com.sad.jetpack.architecture.componentization.annotation.EncryptUtil;
import com.sad.jetpack.architecture.componentization.annotation.ExtraObject;
import com.sad.jetpack.architecture.componentization.annotation.IDataConverter;
import com.sad.jetpack.architecture.componentization.annotation.IPCChat;
import com.sad.jetpack.architecture.componentization.annotation.NameUtils;
import com.sad.jetpack.architecture.componentization.annotation.ValidUtils;
import com.sad.jetpack.architecture.componentization.annotation.What;
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
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
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
import javax.lang.model.type.MirroredTypeException;
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
        int[] priority=annotation_eventResponse.priority();
        if (priority.length!=annotation_eventResponse.url().length){
            log.error(String.format("%s方法所注解的url数量（"+annotation_eventResponse.url().length+"）与priority配置数量（"+priority.length+"）不一致。",executable_e_method.getSimpleName().toString()));
            return;
        }
        List<String> urls=Arrays.asList(annotation_eventResponse.url());
        for (String e_name:urls
             ) {
            String u_name= EncryptUtil.getInstance().XORencode(e_name,"abc123");//ValidUtils.encryptMD5ToString(e_name);
            String dynamicComponentClsName= NameUtils.getParasiticComponentClassSimpleName(e_class.getQualifiedName().toString()+"."+executable_e_method.getSimpleName(),u_name,"$$");
            String pkgName=elementUtils.getPackageOf(e_class).getQualifiedName().toString();
            List<? extends VariableElement> listParams=executable_e_method.getParameters();
            //取出被注解的方法的所有参数类型
            CodeBlock.Builder codeInvokeHostMethodBuilder=CodeBlock.builder();
            if (listParams.size()==0){
                //无参数
                codeInvokeHostMethodBuilder.addStatement("getHost().$L()",executable_e_method.getSimpleName());
            }
            else {
                Element elementIAC_Request=elementUtils.getTypeElement(Constant.PACKAGE_API+".IRequest");
                Element elementIAC_ResponseSession=elementUtils.getTypeElement(Constant.PACKAGE_API+".IResponseSession");
                //检测参数中是否含有Request和session之外的类型
                boolean hasUnknownTypeParameters=false;
                for (VariableElement ve:listParams
                     ) {
                    if (!typeUtils.isSubtype(typeUtils.erasure(ve.asType()),typeUtils.erasure(elementIAC_Request.asType()))
                        && !typeUtils.isSubtype(typeUtils.erasure(ve.asType()),typeUtils.erasure(elementIAC_ResponseSession.asType()))
                    ){
                        //log.error(String.format("错误的方法参数类型，@%s方法第一个参数必须是IPCMessenger类型",executable_e_method.getSimpleName().toString()));
                        hasUnknownTypeParameters=true;
                        break;
                    }
                }
                //预置请求参数备用
                String randomString= RandomStringUtils.randomAlphabetic(5);
                String vn_extraObject="_extraObject_"+randomString;
                String vn_what="_what_"+randomString;
                String vn_description="_description_"+randomString;
                String vn_body="_requestBody_"+randomString;

                if (hasUnknownTypeParameters){

                    codeInvokeHostMethodBuilder.addStatement("$T $L = request.body()",ClassName.bestGuess(Constant.PACKAGE_API+".IBody"),vn_body);
                    codeInvokeHostMethodBuilder.addStatement("$T $L = $L.what()",int.class,vn_what,vn_body);
                    codeInvokeHostMethodBuilder.addStatement("$T $L = $L.description()",String.class,vn_description,vn_body);
                    codeInvokeHostMethodBuilder.addStatement("$T $L = $L.extraObject()",Object.class,vn_extraObject,vn_body);
                    //若含有
                    codeInvokeHostMethodBuilder.addStatement("$T dataContainer=$L.dataContainer()",ClassName.bestGuess(Constant.PACKAGE_API+".IDataContainer"),vn_body);

                    //遍历未知类型参数
//                    for (VariableElement ve:listParams
//                         ) {
//                        if (!typeUtils.isSubtype(typeUtils.erasure(ve.asType()),typeUtils.erasure(elementIAC_Request.asType()))
//                                && !typeUtils.isSubtype(typeUtils.erasure(ve.asType()),typeUtils.erasure(elementIAC_ResponseSession.asType()))
//                        ) {
//                            String p_name=ve.getSimpleName().toString();
//                            Data data=ve.getAnnotation(Data.class);
//                            //TypeMirror dm=null;
//                            Class<? extends IDataConverter> converterClass=null;
//                            String vn_dataName=ve.getSimpleName().toString();
//                            if (data!=null){
//                                p_name=data.name();
//                                vn_dataName="_"+ve.getSimpleName().toString()+"_"+randomString;
//                                //converterClass=data.converter();
//                                //dm=getConverterClassTypeMirror(data);
//                            }
//                            codeInvokeHostMethodBuilder.addStatement("$T $L = dataContainer.get($S)",ve.asType(),vn_dataName,p_name);
//
//                            if (dm!=null){
//                                codeInvokeHostMethodBuilder.addStatement("Object _$L = dataContainer.get($S)",ve.getSimpleName().toString(),p_name);
//                                codeInvokeHostMethodBuilder.addStatement("$T dataConverter_$L = new $T()",IDataConverter.class,ve.getSimpleName().toString(),ClassName.bestGuess(dm.toString()));
//                                codeInvokeHostMethodBuilder.addStatement("$T $L = dataConverter_$L.convert(_$L)",ve.asType(),ve.getSimpleName().toString(),ve.getSimpleName().toString(),ve.getSimpleName().toString());
//                            }
//                            else {
//                                codeInvokeHostMethodBuilder.addStatement("$T $L = dataContainer.get($S)",ve.asType(),ve.getSimpleName().toString(),p_name);
//                            }
//
//                        }
//                    }
                    //log.error(String.format("错误的方法参数类型，@%s方法不能含有IRequest、IResponseSession之外的类型",executable_e_method.getSimpleName().toString()));

                }

                String ps="";
                //生成参数列表
                for (VariableElement ve:listParams
                     ) {
                    String sp=(listParams.indexOf(ve)==listParams.size()-1?"":",");
                    String vn_dataName=ve.getSimpleName().toString();
                    if (typeUtils.isSubtype(typeUtils.erasure(ve.asType()),typeUtils.erasure(elementIAC_Request.asType()))){

                        ps+="request"+sp;
                    }
                    else if (typeUtils.isSubtype(typeUtils.erasure(ve.asType()),typeUtils.erasure(elementIAC_ResponseSession.asType()))){
                        ps+="session"+sp;
                    }
                    else {
                        Data data=ve.getAnnotation(Data.class);
                        Description description=ve.getAnnotation(Description.class);
                        What what=ve.getAnnotation(What.class);
                        ExtraObject extraObject=ve.getAnnotation(ExtraObject.class);
                        //这里注解有效性的优先级是：ExtraObject>Data>What>Description
                        if (extraObject!=null){
                            ps+="("+ve.asType()+")"+vn_extraObject+sp;
                            continue;
                        }
                        if (data!=null){
                            String p_name=data.name();
                            vn_dataName="_"+ve.getSimpleName().toString()+"_"+randomString;
                            codeInvokeHostMethodBuilder.addStatement("$T $L = dataContainer.get($S)",ve.asType(),vn_dataName,p_name);
                            ps+=vn_dataName+sp;
                            continue;
                        }
                        if (what!=null){
                            ps+=vn_what+sp;
                            continue;
                        }
                        if (description!=null){
                            ps+=vn_description+sp;
                        }
                        codeInvokeHostMethodBuilder.addStatement("$T $L = dataContainer.get($S)",ve.asType(),vn_dataName,vn_dataName);
                        ps+=vn_dataName+sp;

                    }
                }
                codeInvokeHostMethodBuilder.addStatement("getHost().$L("+ps+")",executable_e_method.getSimpleName());
            }
            MethodSpec ms_onComponentResponse=MethodSpec.methodBuilder("onCall")
                    .addAnnotation(Override.class)
                    .returns(TypeName.VOID)
                    .addModifiers(Modifier.PUBLIC)
                    .addException(Exception.class)
                    .addParameter(ParameterSpec.builder(ClassName.bestGuess(Constant.PACKAGE_API+".IRequest"),"request").build())
                    .addParameter(ParameterSpec.builder(ClassName.bestGuess(Constant.PACKAGE_API+".IResponseSession"),"session").build())
                    .addCode(codeInvokeHostMethodBuilder.build())
                    .build();
            MethodSpec ms_priority=MethodSpec.methodBuilder("priority")
                    .addAnnotation(Override.class)
                    .returns(TypeName.INT)
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("return "+annotation_eventResponse.priority()[urls.indexOf(e_name)])
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
                    .superclass(ParameterizedTypeName.get(ClassName.bestGuess(Constant.PACKAGE_API+".ParasiticComponent"),
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

    /*private static TypeMirror getConverterClassTypeMirror(Data annotation) {
        try
        {
            annotation.converter(); // this should throw
        }
        catch( MirroredTypeException mte )
        {
            return mte.getTypeMirror();
        }
        return null; // can this ever happen ??
    }*/
}
