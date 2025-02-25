package com.filemanager.services.renaming.strategies;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.filemanager.models.ProcessingFile;
import com.filemanager.services.renaming.BaseRenameStrategyWithParams;
import com.filemanager.services.renaming.enums.FileExtension;
import com.filemanager.utils.FileUtils;

public class RenameByFormatPattern extends BaseRenameStrategyWithParams {

    private final String displayName = "Rename by format pattern";

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public Boolean validateStrategyParams() {
        return this.getFormatPattern() != null;
    }

    @Override
    public Boolean validateFileRequirements(ProcessingFile file) {
        File parentDir = file.getParentDir();

        if (parentDir == null) {
            return false;
        }

        Optional<FileExtension> extension = FileUtils.getFileExtension(file.getFile());

        return extension.isPresent();
    }

    @Override
    public List<ProcessingFile> execute(List<ProcessingFile> files, boolean prePreprocess) {
        System.out.println("Winner");
        return files;
    }
}
