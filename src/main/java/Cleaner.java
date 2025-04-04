package main.java;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Cleaner {
    private static final Logger LOGGER = Logger.getLogger(Cleaner.class.getName());

    public static void main(String[] args) {
        Path dir = Paths.get("./");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.log")) {
            for (Path file : stream) {
                try {
                    Files.delete(file);
                    LOGGER.info("Deleted: " + file);
                }catch (IOException e){
                    LOGGER.severe("Failed to delete " + file + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            LOGGER.severe("Error accessing directory: " + e.getMessage());
        }
    }
}
