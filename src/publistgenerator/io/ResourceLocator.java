/*
 */
package publistgenerator.io;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import publistgenerator.Console;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class ResourceLocator {

    private static final Path baseDirectory;

    static {
        Path workingDir;

        try {
            // Location of the publistgenerator package. Can be of one of two forms:
            // - path/build/classes - when run from within NetBeans
            // - path/PubListGenerator.jar - when run from a jar archive
            workingDir = Paths.get(ResourceLocator.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            
            if (workingDir.endsWith(Paths.get("build", "classes"))) {
                // Running in NetBeans, remove "build/classes"
                workingDir = workingDir.getParent().getParent();
            } else if (workingDir.endsWith("PubListGenerator.jar")) {
                // Running from a jar
                workingDir = workingDir.getParent();
            }
        } catch (NullPointerException // From the long chain of initializers
                | SecurityException // Can be thrown from getProtectionDomain(), if a SecurityManager is enabled
                | URISyntaxException ex) {
            Console.except(ex, "Exception while initializing base directory:");
            workingDir = (new File(System.getProperty("user.dir"))).toPath();
            Console.log("Reverted to working directory \"%s\".", workingDir.toString());
        }

        baseDirectory = workingDir;
    }

    /**
     * The directory that contains the JAR.
     *
     * @return
     */
    public static Path getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Returns a path to the given file that is relative to the base directory.
     * When the file is
     * <code>null</code>, an empty path is returned instead.
     *
     * @param file
     * @return
     */
    public static String getRelativePath(File file) {
        if (file == null) {
            return "";
        } else {
            return baseDirectory.relativize(file.toPath()).toString();
        }
    }

    /**
     * Resolves the given path against the base directory and returns the
     * corresponding File. When the path is
     * <code>null</code> or empty,
     * <code>null</code> is returned instead.
     *
     * @param relativePath
     * @return
     */
    public static File getFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        } else {
             return baseDirectory.resolve(relativePath).toFile();
        }
    }

    public static void main(String[] args) {
        getFile("a");
    }
}
