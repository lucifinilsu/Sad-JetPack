package com.sad.jetpack.architecture.componentization.api;

import java.io.Serializable;
import java.util.Map;

public interface IDataContainer extends Serializable {

    Map getMap();

    <T> T get(Object key);

    IDataContainer put(Object key,Object value);

    IDataContainer putAll(Map map);

    IDataContainer putAll(IDataContainer dataContainer);
}
