package com.abstractdocument.domain;

import com.abstractdocument.Document;
import com.abstractdocument.domain.enums.Property;
import java.util.stream.Stream;

public interface HasParts extends Document {
    
    default Stream<Part> getParts() {
        return children(Property.PARTS.toString(), Part::new);
    }
}
