package org.mga44.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileWriter {
    private static final Logger logger = LoggerFactory.getLogger(FileWriter.class);

    public static void writeToOut(Class<?> clazz, String text) {
        final Path filePath = Paths.get("out", clazz.getSimpleName() + "_output.txt");
        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, text);
            logger.debug("Output written to out file: {}", filePath);
        } catch (IOException e) {
            logger.error("Unexpected error when writing to {}, stacktrace: {}", filePath, e.getStackTrace());
        }
    }

    public static void writeToResult(Class<?> clazz, String text) {
        final Path filePath = Paths.get("result", clazz.getSimpleName() + ".out");
        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, text);
            logger.debug("Result written to out file: {}", filePath);
        } catch (IOException e) {
            logger.error("Unexpected error when writing to {}, stacktrace: {}", filePath, e.getStackTrace());
        }
    }
}
