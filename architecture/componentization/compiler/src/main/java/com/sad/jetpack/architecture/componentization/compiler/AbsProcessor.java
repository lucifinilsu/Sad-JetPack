package com.sad.jetpack.architecture.componentization.compiler;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract class AbsProcessor extends AbstractProcessor {
    //在init里初始化这4个工具性质的类
    protected Types typeUtils;
    protected Elements elementUtils;//Element代表程序的元素，例如包、类或者方法。每个Element代表一个静态的、语言级别的构件。它只是结构化的文本，他不是可运行的.可以理解为一个签名 (public class Main 或者 private void fun(int a) {})，大家可以联系到XML的解析，或者抽象语法树 （AST）
    protected Filer filer;
    protected Messager messager;//因为在注解处理器里面不可以抛出Exception！为什么了？因为在编译阶段抛出错误后，注解处理器就不会运行完，也就没什么用了。所以Message就是为了输出错误信息。
    protected ProcessorLog log;
    protected boolean isLog=true;
    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        elementUtils = env.getElementUtils();
        processingEnv=env;
        filer = env.getFiler();
        typeUtils = env.getTypeUtils();
        messager=env.getMessager();
    }

}
