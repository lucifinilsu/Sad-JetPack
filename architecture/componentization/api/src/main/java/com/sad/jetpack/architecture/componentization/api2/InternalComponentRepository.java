package com.sad.jetpack.architecture.componentization.api2;

import java.util.LinkedHashMap;

public class InternalComponentRepository implements IComponentRepository {
    private LinkedHashMap<Object, String> objectInstances =new LinkedHashMap<>();
    private LinkedHashMap<IComponent, String> componentInstances =new LinkedHashMap<>();

    public InternalComponentRepository(LinkedHashMap<Object, String> objectInstances, LinkedHashMap<IComponent, String> componentInstances) {
        this.objectInstances = objectInstances;
        this.componentInstances = componentInstances;
    }

    @Override
    public LinkedHashMap<Object, String> objectInstances() {
        return this.objectInstances;
    }

    @Override
    public LinkedHashMap<IComponent, String> componentInstances() {
        return this.componentInstances;
    }
}
