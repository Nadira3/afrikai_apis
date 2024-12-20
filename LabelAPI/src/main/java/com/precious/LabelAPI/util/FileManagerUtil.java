package com.precious.LabelAPI.util;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for file path operations
 */
public class FileManagerUtil {
    public static File getCurrentDirectoryFile(String fileName) {
        // Get the current working directory
        String currentDirectory = System.getProperty("user.dir");
        
        // Construct the file path in the current directory
        File file = new File(currentDirectory, fileName);
        
        return file;
    }

    /**
     * Determines file type from filename
     */
    public FileType determineFileType(String filename) {
        String extension = FilenameUtils.getExtension(filename).toLowerCase();
        return switch (extension) {
            case "csv" -> FileType.CSV;
            case "json" -> FileType.JSON;
            case "jsonl" -> FileType.JSONL;
            case "xlsx", "xls" -> FileType.EXCEL;
            default -> throw new UnsupportedFileTypeException("Unsupported file type: " + extension);
        };
    }
}
