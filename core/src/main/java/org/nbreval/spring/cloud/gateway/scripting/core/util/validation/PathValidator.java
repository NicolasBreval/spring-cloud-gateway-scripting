package org.nbreval.spring.cloud.gateway.scripting.core.util.validation;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/** Class used to validate some types of paths */
public class PathValidator {

  /**
   * Validates if an input string is a valid absolute or relative path. Also checks if path's file
   * exists.
   *
   * @param possiblePath String to check if is a valid path.
   * @return True if argument string is a valid path and file exists, else false.
   */
  public static boolean isValidRegularPath(String possiblePath) {
    try {
      var path = Paths.get(possiblePath);
      return Files.exists(path);
    } catch (InvalidPathException e) {
      return false;
    }
  }

  /**
   * Validates if an input string is a valid classpath's path. Also checks if resource file exists.
   *
   * @param possiblePath String to check if is a valid classpath's path.
   * @return True if argument string is a valid classpath's path and resource exists, else false.
   */
  public static boolean isValidClasspathPath(String possiblePath) {
    if (possiblePath.startsWith("classpath:")) {
      var resourcePath = possiblePath.replaceFirst("classPath:", "");
      return Thread.currentThread().getContextClassLoader().getResource(resourcePath) != null;
    }

    return false;
  }
}
