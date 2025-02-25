package com.filemanager.services.renaming;

import com.filemanager.services.renaming.enums.FormatPattern;

public abstract class BaseRenameStrategyWithParams extends BaseRenameStrategy {

    private FormatPattern formatPattern;

    public abstract Boolean validateStrategyParams();

    public FormatPattern getFormatPattern() {
        return this.formatPattern;
    }

    public void setFormatPattern(FormatPattern formatPattern) {
        this.formatPattern = formatPattern;
    }
}
