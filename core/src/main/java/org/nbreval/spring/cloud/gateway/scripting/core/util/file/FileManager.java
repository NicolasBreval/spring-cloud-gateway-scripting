package org.nbreval.spring.cloud.gateway.scripting.core.util.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Util class used to make operations with files */
public class FileManager {

  /**
   * Gets the content of a file as a String object.
   *
   * @param regularPath Paht of file to download.
   * @return The content of file to download.
   * @throws IOException If any I/O error occurs during file downloading.
   */
  public static String getRegularFileContentAsText(String regularPath) throws IOException {
    return Files.readString(Paths.get(regularPath));
  }

  /**
   * Gets the content of a file inside the project's classpath.
   *
   * @param classpathPath Path of the resource file to download.
   * @return The content of the resource to download.
   * @throws IOException If any I/O exception occurs during resource downloading.
   */
  public static String getResourceFileContentAsText(String classpathPath) throws IOException {
    return new String(
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream(classpathPath)
            .readAllBytes(),
        StandardCharsets.UTF_8);
  }
}
