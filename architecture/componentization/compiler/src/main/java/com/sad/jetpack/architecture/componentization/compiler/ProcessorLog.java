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
    public boolean config_err(String opt){
        if (opt==null ||"".equals(opt)){
            error("未在Gradle脚本里配置Log开关,后续日志将关闭,若要启动日志请按照以下格式配置：\n" +
                    "android{\n" +
                    "   ...\n"+
                    "   defaultConfig{\n" +
                    "       ...\n"+
                    "       javaCompileOptions {\n" +
                    "            annotationProcessorOptions {\n" +
                    "                arguments = [\n" +
                    "                        log:true,\n"+
                    "                ]\n" +
                    "            }\n" +
                    "        }\n" +
                    "       ...\n"+
                    "   }\n" +
                    "   ...\n"+
                    "}\n"
            );
            return true;
        }
        return false;
    }
}
