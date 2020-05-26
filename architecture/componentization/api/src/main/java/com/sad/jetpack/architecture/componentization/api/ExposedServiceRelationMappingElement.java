package com.sad.jetpack.architecture.componentization.api;

public class ExposedServiceRelationMappingElement {
    private String url="";
    private String decription="";
    private String className="";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "ExposdServiceRelationMappingElement{" +
                "url='" + url + '\'' +
                ", decription='" + decription + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
