package com.abstractdocument;

import com.abstractdocument.domain.Car;
import com.abstractdocument.domain.enums.Property;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    // private static final Logger LOGGER = ...  ← この行を削除

    public static void main(String[] args) {
        LOGGER.info("Constructing parts and car");

        var wheelProperties = Map.of(
            Property.TYPE.toString(), "wheel",
            Property.MODEL.toString(), "15C",
            Property.PRICE.toString(), 100L
        );

        var doorProperties = Map.of(
            Property.TYPE.toString(), "door",
            Property.MODEL.toString(), "Lambo",
            Property.PRICE.toString(), 300L
        );

        var carProperties = Map.of(
            Property.MODEL.toString(),
            "300SL",
            Property.PRICE.toString(),
            10000L,
            Property.PARTS.toString(),
            List.of(wheelProperties, doorProperties)
        );

        var car = new Car(carProperties);

        LOGGER.info("Here is our car:");
        LOGGER.info("-> model: {}", car.getModel().orElseThrow());
        LOGGER.info("-> price: {}", car.getPrice().orElseThrow());
        LOGGER.info("-> parts: ");
        car.getParts()
            .forEach(
                p ->
                    LOGGER.info(
                        "\t{}/{}/{}",
                        p.getType().orElse(null),
                        p.getModel().orElse(null),
                        p.getPrice().orElse(null)));
    }
}