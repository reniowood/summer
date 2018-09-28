package com.jinhuyk.summer_core.components;

public interface Creatable<T> {
    boolean isCreatable();
    T create();
}
