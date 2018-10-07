package com.jinhyuk.summer.core.components;

public interface Creatable<T> {
    boolean isCreatable();
    T create();
}
