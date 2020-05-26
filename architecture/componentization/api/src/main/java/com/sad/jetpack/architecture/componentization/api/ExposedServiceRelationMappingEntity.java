package com.sad.jetpack.architecture.componentization.api;

public class ExposedServiceRelationMappingEntity {
    private String path="";
    private ExposedServiceRelationMappingElement element;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ExposedServiceRelationMappingElement getElement() {
        return element;
    }

    public void setElement(ExposedServiceRelationMappingElement element) {
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
