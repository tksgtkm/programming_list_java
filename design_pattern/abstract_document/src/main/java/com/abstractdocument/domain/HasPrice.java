package com.abstractdocument.domain;

import com.abstractdocument.Document;
import com.abstractdocument.domain.enums.Property;
import java.util.Optional;

public interface HasPrice extends Document {
    
    default Optional<Number> getPrice() {
        return Optional.ofNullable((Number) get(Property.PRICE.toString()));
    }
}
