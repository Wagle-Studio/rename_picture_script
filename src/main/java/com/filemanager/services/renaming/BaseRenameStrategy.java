package com.filemanager.services.renaming;

import java.util.List;

import com.filemanager.models.ProcessingFile;

public abstract class BaseRenameStrategy implements RenameStrategy {

    @Override
    public abstract String getDisplayName();

    @Override
    public abstract Boolean validateFileRequirements(ProcessingFile file);

    @Override
    public abstract List<ProcessingFile> execute(List<ProcessingFile> files, boolean prePreprocess);
}
