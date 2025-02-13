package com.suffolk.library_management.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.suffolk.library_management.utils.Constant.*;


public class FileUtils {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    File getFileDirectory(int check) {
        String fileName = "";
        if (check == 1) {
            fileName = DIRECTORY_BOOK_COVER;
        } else if (check == 2) {
            fileName = DIRECTORY_BOOK_FILE;
        }
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            try {
                var file = Files.createDirectories(path.toAbsolutePath());
                return file.toFile();
            } catch (IOException e) {
                logger.error("Error in getFileDirectory : " + e.getMessage());
            }
        }
        return new File(fileName);
    }

    public static String GetFileName(String name) {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + "_" + name;
    }

    public static String GetFilePath(String name, int check) {
        if (check == 1) { // For Logo
            return FOLDER_PATH_BOOK_COVER + "/" + name;
        } else if (check == 2) {
            return FOLDER_PATH_BOOK_FILE + "/" + name;
        }
        return null;
    }

}
