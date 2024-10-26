package org.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileWriter {
    private static final Logger logger = LoggerFactory.getLogger(FileWriter.class);

    static void writeContentsToFile(Class<?> clazz, String text) {
        Path filePath = Paths.get( clazz.getSimpleName() + "_output.txt");
        try {
            Files.writeString(filePath, text);
            logger.info("Output written to out file: {}", filePath);
        } catch (IOException e) {
            logger.error("Unexpected error when writing to {}, stacktrace: {}", filePath, e.getStackTrace());
        }
    }
}
