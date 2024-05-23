package org.example.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utility {
    public static String fileToString(String filePathString) {
        Path filePath = Path.of(filePathString);

        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read file " + filePath);
        }
    }
}