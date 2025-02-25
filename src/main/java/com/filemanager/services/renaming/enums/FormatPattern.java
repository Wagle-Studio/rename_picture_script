package com.filemanager.services.renaming.enums;

import java.util.Arrays;
import java.util.Optional;

public enum FormatPattern {
    SNAKE_CASE("Snake case  : uni_economy_27_01_2025");
    // KEBAB_CASE("Kebab case  : uni-economy-27-01-2025"),
    // CAMEL_CASE("Camel case  : uniEconomy27012025"),
    // PASCAL_CASE("Pascal case : UniEconomy27012025");

    private final String displayValue;

    FormatPattern(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }

    public static Optional<FormatPattern> fromString(String extension) {
        return Arrays.stream(values())
                .filter(ext -> ext.name().equalsIgnoreCase(extension))
                .findFirst();
    }

    @Override
    public String toString() {
        return this.getDisplayValue();
    }
}
