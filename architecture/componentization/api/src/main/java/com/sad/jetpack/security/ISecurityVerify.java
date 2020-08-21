package com.sad.jetpack.security;

public interface ISecurityVerify {

    <T> T onSecurityVerifySuccess(T t);

    boolean onSecurityVerifySuccess(Exception e);

}
