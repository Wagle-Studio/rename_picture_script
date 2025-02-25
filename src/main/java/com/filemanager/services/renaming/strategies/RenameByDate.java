package com.filemanager.services.renaming.strategies;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.drew.metadata.Metadata;
import com.filemanager.models.ProcessingFile;
import com.filemanager.models.enums.FileStatus;
import com.filemanager.services.renaming.BaseRenameStrategy;
import com.filemanager.services.renaming.RenameStrategy;
import com.filemanager.services.renaming.enums.FileExtension;
import com.filemanager.utils.FileUtils;

public class RenameByDate extends BaseRenameStrategy {

    private final String displayName = "Rename by date";

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public List<RenameStrategy> getStrategyPreprocess() {
        return List.of(new RenameRandomly());
    }

    @Override
    public Boolean validateFileRequirements(ProcessingFile file) {
        File parentDir = file.getParentDir();

        if (parentDir == null) {
            return false;
        }

        Optional<Metadata> metadata = FileUtils.getFileMetadata(file.getFile());

        if (metadata.isEmpty()) {
            return false;
        }

        Optional<FileExtension> extension = FileUtils.getFileExtension(file.getFile());
        Optional<String> originalDate = FileUtils.getFileOriginalDateByMetaData(metadata.get());

        return extension.isPresent() && originalDate.isPresent();
    }

    @Override
    public List<ProcessingFile> execute(List<ProcessingFile> files, boolean prePreprocess) {
        Map<String, Integer> nameCounter = new HashMap<>();

        files.forEach(file -> {
            Optional<Metadata> metadata = FileUtils.getFileMetadata(file.getFile());

            if (metadata.isEmpty()) {
                String fileStatus = "File doesn't match requirements for strategy : " + this.getDisplayName().toLowerCase();
                file.setStatus(FileStatus.UNPROCESSABLE, fileStatus);
                return;
            }

            Optional<String> originalDate = metadata.flatMap(FileUtils::getFileOriginalDateByMetaData);

            if (originalDate.isEmpty()) {
                String fileStatus = "File doesn't match requirements for strategy : " + this.getDisplayName().toLowerCase();
                file.setStatus(FileStatus.UNPROCESSABLE, fileStatus);
                return;
            }

            Optional<FileExtension> extension = FileUtils.getFileExtension(file.getFile());

            if (extension.isEmpty()) {
                String fileStatus = "File doesn't match requirements for strategy : " + this.getDisplayName().toLowerCase();
                file.setStatus(FileStatus.UNPROCESSABLE, fileStatus);
                return;
            }

            String formattedOriginalDate = originalDate.get().toLowerCase();
            String formattedExtension = extension.get().name().toLowerCase();
            int suffix = nameCounter.getOrDefault(formattedOriginalDate, 0);

            String newFileName = formattedOriginalDate + "_" + suffix + "." + formattedExtension;
            File newFile = new File(file.getParentDir(), newFileName);

            while (newFile.exists()) {
                suffix++;
                newFileName = formattedOriginalDate + "_" + suffix + "." + formattedExtension;
                newFile = new File(file.getParentDir(), newFileName);
            }

            if (file.getFile().renameTo(newFile)) {
                nameCounter.put(formattedOriginalDate, suffix);
                file.setFile(newFile);
                file.setStatus(prePreprocess ? FileStatus.PROCESSING : FileStatus.PROCESSED, "Renamed by date");
            } else {
                file.setStatus(FileStatus.UNPROCESSABLE, "Failed to rename file");
            }
        });

        return files;
    }
}
