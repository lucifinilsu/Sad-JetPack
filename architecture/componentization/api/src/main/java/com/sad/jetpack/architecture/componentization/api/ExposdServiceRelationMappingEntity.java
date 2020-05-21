package com.sad.jetpack.architecture.componentization.api;

public class ExposdServiceRelationMappingEntity {
    private String path="";
    private ExposdServiceRelationMappingElement element;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ExposdServiceRelationMappingElement getElement() {
        return element;
    }

    public void setElement(ExposdServiceRelationMappingElement element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return "ExposdServiceRelationMappingEntity{" +
                "path='" + path + '\'' +
                ", element=" + element +
                '}';
    }
}
