package org.tourplanner.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

public final class NumberTextFormatters {

    private NumberTextFormatters() {}   // utility class - no instantiating

    public static TextFormatter<Number> forInteger(IntegerProperty target) {
        TextFormatter<Number> fmt = new TextFormatter<>(
                new NumberStringConverter("#"),
                target.get(),
                change -> change.getControlNewText().matches("\\d*") ? change : null
        );
        target.bindBidirectional(fmt.valueProperty());
        return fmt;
    }

    public static TextFormatter<Number> forDouble(DoubleProperty target) {
        TextFormatter<Number> fmt = new TextFormatter<>(
                new NumberStringConverter(),
                target.get(),
                change -> change.getControlNewText().matches("\\d*(\\.\\d*)?") ? change : null
        );
        target.bindBidirectional(fmt.valueProperty());
        return fmt;
    }
}