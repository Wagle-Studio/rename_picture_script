package com.filemanager.services;

import java.io.File;
import java.util.List;

import com.filemanager.models.ProcessingFile;
import com.filemanager.models.ProcessingTask;
import com.filemanager.models.enums.TaskStatus;
import com.filemanager.services.renaming.BaseRenameStrategyWithParams;
import com.filemanager.services.renaming.RenameStrategy;
import com.filemanager.utils.FileUtils;

public class DefaultFileProcessor implements FileProcessor {

    @Override
    public void analyse(ProcessingTask task) {
        task.setStatus(TaskStatus.SETTING, "Setting task");

        if (task.getStrategy() instanceof BaseRenameStrategyWithParams strategy && !strategy.validateStrategyParams()) {
            task.setStatus(TaskStatus.ERROR, "Missing parameters for the selected strategy");
            return;
        }

        task.setStatus(TaskStatus.ANALYZING, "Analyzing folder");

        List<File> folderContent = FileUtils.getFolderContent(task.getFolderPath());

        if (folderContent.isEmpty()) {
            task.setStatus(TaskStatus.ERROR, "The folder is empty");
            return;
        }

        List<ProcessingFile> processedFiles = FileUtils.processingFiles(folderContent);

        if (processedFiles.isEmpty()) {
            task.setStatus(TaskStatus.ERROR, "No files to process");
            return;
        }

        List<ProcessingFile> filteredFiles = FileUtils.applyFilterByExtension(processedFiles, task.getExtensions());

        if (filteredFiles.isEmpty()) {
            task.setStatus(TaskStatus.ERROR, "No files matching selected extensions");
            return;
        }

        List<ProcessingFile> processingFiles = FileUtils.applyStrategyFileValidation(filteredFiles, task.getStrategy());

        for (RenameStrategy preStrategy : task.getStrategy().getStrategyPreprocess()) {
            processingFiles = FileUtils.applyStrategyFileValidation(processingFiles, preStrategy);
        }

        if (processingFiles.isEmpty()) {
            task.setStatus(TaskStatus.ERROR, "No processable files");
            return;
        }

        task.setProcessableFiles(FileUtils.getProcessableFiles(processingFiles));
        task.setUnprocessableFiles(FileUtils.getUnprocessableFiles(processingFiles));

        task.setStatus(TaskStatus.ANALYSED, "Ready to process files");
    }

    @Override
    public void run(ProcessingTask task) {
        if (!task.getStrategy().getStrategyPreprocess().isEmpty()) {
            for (RenameStrategy preStrategy : task.getStrategy().getStrategyPreprocess()) {
                List<ProcessingFile> processingFiles = preStrategy.execute(task.getProcessableFiles(), true);
                task.setProcessableFiles(FileUtils.getProcessableFiles(processingFiles));
            }
        }

        List<ProcessingFile> finalFiles = task.getStrategy().execute(task.getProcessableFiles(), false);
        task.setProcessableFiles(FileUtils.getProcessableFiles(finalFiles));
        task.setStatus(TaskStatus.PROCESSED, "Processed with success");
    }
}
