package com.abstractdocument.domain;

import com.abstractdocument.Document;
import com.abstractdocument.domain.enums.Property;
import java.util.Optional;

public interface HasType extends Document {
    default Optional<String> getType() {
        return Optional.ofNullable((String) get(Property.TYPE.toString()));
    }
}
