package law.leya;

import java.io.*;

public class FileUtils {
    public static String getResourcePath(String resourceName) {
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        return classLoader.getResource(resourceName).getPath();
    }

    public static String getAbsolutePath(String relativePath) {
        return new File(relativePath).getAbsolutePath();
    }
}
