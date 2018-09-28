package com.jinhuyk.summer_core.components;

import java.util.Collection;

public interface FieldDependent extends Dependent {
    Collection<Injectable<?>> getFieldDependencies();
}
