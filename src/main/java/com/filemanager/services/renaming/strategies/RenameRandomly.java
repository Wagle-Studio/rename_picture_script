package com.filemanager.services.renaming.strategies;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.filemanager.models.ProcessingFile;
import com.filemanager.models.enums.FileStatus;
import com.filemanager.services.renaming.BaseRenameStrategy;
import com.filemanager.services.renaming.enums.FileExtension;
import com.filemanager.utils.FileUtils;

public class RenameRandomly extends BaseRenameStrategy {

    private final String displayName = "Rename randomly";

    @Override
    public String getDisplayName() {
        return this.displayName;
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
        files.forEach(file -> {
            Optional<FileExtension> extension = FileUtils.getFileExtension(file.getFile());

            if (extension.isEmpty()) {
                String fileStatus = "File doesn't match requirements for strategy : " + this.getDisplayName().toLowerCase();
                file.setStatus(FileStatus.UNPROCESSABLE, fileStatus);
                return;
            }

            String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension.get().name().toLowerCase();
            File newFile = new File(file.getParentDir(), newFileName);

            while (newFile.exists()) {
                newFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension.get().name().toLowerCase();
                newFile = new File(file.getParentDir(), newFileName);
            }

            if (file.getFile().renameTo(newFile)) {
                file.setFile(newFile);
                file.setStatus(prePreprocess ? FileStatus.PROCESSING : FileStatus.PROCESSED, "Renamed randomly");
            } else {
                file.setStatus(FileStatus.UNPROCESSABLE, "Failed to rename file");
            }
        });

        return files;
    }
}
