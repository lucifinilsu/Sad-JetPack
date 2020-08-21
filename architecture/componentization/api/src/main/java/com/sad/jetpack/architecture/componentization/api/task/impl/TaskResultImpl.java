package com.sad.jetpack.architecture.componentization.api.task.impl;

import androidx.annotation.NonNull;

import com.sad.jetpack.architecture.componentization.api.task.ITaskResult;
import com.sad.jetpack.architecture.componentization.api.task.TaskState;
import com.sad.jetpack.security.ISecurity;
import com.sad.jetpack.security.ISecurityVerify;

public class TaskResultImpl implements ITaskResult{

    private TaskResultImpl(){}
    private TaskResultImpl(String token){
        this.token=token;
    }
    private Object data=null;
    private TaskState state=TaskState.UNWORKED;
    private String token="";
    public static ITaskResult newInstance(String token){
        return new TaskResultImpl(token);
    }
    @Override
    public <T> T data() {
        return (T) data;
    }

    @Override
    public TaskState state() {
        return this.state;
    }

    @Override
    public Creator creator(@NonNull ISecurity security) throws Exception{
        if (security==null){
            throw new Exception("ur ISecurity can not be null !!!");
        }
        String t=security.token();
        ISecurityVerify securityVerify=security.securityVerify();
        if (token.equals(t)){
            Creator creator= new Creator() {
                @Override
                public Creator data(Object o) {
                    TaskResultImpl.this.data=o;
                    return this;
                }

                @Override
                public Creator state(TaskState state) {
                    TaskResultImpl.this.state=state;
                    return this;
                }

                @Override
                public ITaskResult create() {
                    return TaskResultImpl.this;
                }
            };
            if (securityVerify!=null){
                return securityVerify.onSecurityVerifySuccess(creator);
            }
            else {
                return creator;
            }

        }
        else {
            Exception e=new Exception(",u can not modify this object.");
            if (securityVerify!=null){
                securityVerify.onSecurityVerifySuccess(e);
            }
            throw e;
        }

    }

}
