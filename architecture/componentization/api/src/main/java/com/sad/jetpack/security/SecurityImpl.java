package com.sad.jetpack.security;

import java.security.Security;

public class SecurityImpl implements ISecurity{

    private SecurityImpl(){}
    private SecurityImpl(String token){
        this.token=token;
    }
    public static SecurityImpl newInstance(String token){
        return new SecurityImpl(token);
    }

    private String token="";
    private ISecurityVerify securityVerify;

    public SecurityImpl token(String token){
        this.token=token;
        return this;
    }
    public SecurityImpl securityVerify(ISecurityVerify securityVerify){
        this.securityVerify=securityVerify;
        return this;
    }

    @Override
    public String token() {
        return token;
    }

    @Override
    public ISecurityVerify securityVerify() {
        return securityVerify;
    }
}
