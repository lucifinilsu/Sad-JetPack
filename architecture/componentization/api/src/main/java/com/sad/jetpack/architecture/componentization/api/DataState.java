package com.sad.jetpack.architecture.componentization.api;

import java.io.Serializable;

public enum DataState implements Serializable {

    DONE,EXCEPTION,FAILURE,RUNNING,CANCELED,UNWORKED,INTERCEPTED;

    private static final long serialVersionUID=51556513651315L;
}
