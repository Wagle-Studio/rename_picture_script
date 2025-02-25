package com.filemanager.services.renaming;

import com.filemanager.services.renaming.enums.StrategyType;
import com.filemanager.services.renaming.strategies.RenameByDate;
import com.filemanager.services.renaming.strategies.RenameByFormatPattern;
import com.filemanager.services.renaming.strategies.RenameRandomly;

public class RenameStrategyFactory {

    public static RenameStrategy getStrategy(StrategyType type) {
        return switch (type) {
            case RANDOM ->
                new RenameRandomly();
            case BY_DATE ->
                new RenameByDate();
            case BY_FORMAT_PATTERN ->
                new RenameByFormatPattern();
            default ->
                throw new IllegalArgumentException("Unknown rename strategy : " + type);
        };
    }
}
