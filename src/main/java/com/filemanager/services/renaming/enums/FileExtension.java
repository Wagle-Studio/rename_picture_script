package com.filemanager.services.renaming.enums;

import java.util.Arrays;
import java.util.Optional;

public enum FileExtension {
    TXT, PDF, DOC, DOCX, XLS, XLSX, CSV, JPG, JPEG, PNG, GIF, BMP, MP3, MOV, MP4, AVI, MKV, ZIP, RAR, TAR, GZ, XML, JSON;

    public static Optional<FileExtension> fromString(String extension) {
        return Arrays.stream(values())
                .filter(ext -> ext.name().equalsIgnoreCase(extension))
                .findFirst();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
