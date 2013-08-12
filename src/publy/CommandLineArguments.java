/*
 */
package publy;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.Parameter;
import java.nio.file.Path;
import publy.io.ResourceLocator;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class CommandLineArguments {

    @Parameter(names = {"-c", "--config"}, description = "Configuration file to use (XML)", arity = 1, converter = PathConverter.class)
    private Path config;
    @Parameter(names = {"-d", "--debug"}, description = "Enable debug output")
    private boolean debug = false;
    @Parameter(names = {"-g", "--gui"}, description = "Open the configuration GUI")
    private boolean gui = false;
    @Parameter(names = {"-h", "--help"}, description = "Display this usage information", help = true)
    private boolean help = false;
    @Parameter(names = {"-f", "--input"}, description = "Publication list to use (BibTeX)", arity = 1, converter = PathConverter.class)
    private Path input;
    @Parameter(names = {"-o", "--output"}, description = "Output file to use (HTML)", arity = 1, converter = PathConverter.class)
    private Path output;
    @Parameter(names = {"-q", "-s", "--quiet", "--silent"}, description = "Hide regular log output")
    private boolean silent = false;
    @Parameter(names = {"-v", "-V", "--version"}, description = "Display version information")
    private boolean version = false;
    @Parameter(names = {"-w", "--no-warn"}, description = "Hide warnings")
    private boolean hidewarnings = false;

    public Path getConfig() {
        return config;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isGui() {
        return gui;
    }

    public boolean isHelp() {
        return help;
    }

    public Path getInput() {
        return input;
    }

    public Path getOutput() {
        return output;
    }

    public boolean isSilent() {
        return silent;
    }

    public boolean isVersion() {
        return version;
    }

    public boolean isHidewarnings() {
        return hidewarnings;
    }

    private class PathConverter implements IStringConverter<Path> {

        @Override
        public Path convert(String value) {
            return ResourceLocator.getFullPath(value);
        }
    }
}
