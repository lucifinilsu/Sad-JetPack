package com.sad.jetpack.architecture.componentization.compiler;


import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class ProcessorLog {

    private Messager messager;
    private boolean isLog=false;
    public ProcessorLog(Messager messager,boolean isLog){
        this.messager=messager;
        this.isLog=isLog;
    }
    //打印错误信息
    public void error(String err) {
        if (isLog){
            messager.printMessage(Diagnostic.Kind.ERROR, err);
        }
    }

    public void info(String info){
        if (isLog){
            messager.printMessage(Diagnostic.Kind.NOTE, info);
        }
    }

}
