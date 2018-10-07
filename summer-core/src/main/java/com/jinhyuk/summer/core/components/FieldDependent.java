package com.jinhyuk.summer.core.components;

import java.util.Collection;

public interface FieldDependent extends Dependent {
    Collection<Injectable<?>> getFieldDependencies();
}
