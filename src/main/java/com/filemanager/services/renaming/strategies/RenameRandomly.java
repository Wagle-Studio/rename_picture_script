package com.filemanager.services.renaming.strategies;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.drew.metadata.Metadata;
import com.filemanager.models.ProcessingFile;
import com.filemanager.models.enums.FileStatus;
import com.filemanager.services.renaming.RenameStrategy;
import com.filemanager.utils.FileUtils;

public class RenameRandomly implements RenameStrategy {

    private final String displayName = "Rename randomly";

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public Boolean validateFileMetadata(Metadata metadata) {
        if (metadata == null) {
            return false;
        }

        Optional<String> extension = FileUtils.getFileExtension(metadata);

        return !extension.isEmpty();
    }

    @Override
    public List<ProcessingFile> execute(List<ProcessingFile> files, boolean prePreprocess) {
        files.forEach(file -> {
            Optional<String> rawExtension = FileUtils.getFileExtension(file.getMetadata());

            if (rawExtension.isEmpty()) {
                String fileStatus = "Metadata doesn't match requirements for strategy : " + this.getDisplayName().toLowerCase();
                file.setStatus(FileStatus.UNPROCESSABLE, fileStatus);
                return;
            }

            File parentDir = file.getParentDir();

            if (parentDir == null) {
                file.setStatus(FileStatus.UNPROCESSABLE, "Parent directory is invalid");
                return;
            }

            String extension = rawExtension.get().toLowerCase();

            String newFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
            File newFile = new File(parentDir, newFileName);

            while (newFile.exists()) {
                newFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
                newFile = new File(parentDir, newFileName);
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
