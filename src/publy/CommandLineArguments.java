/*
 * Copyright 2013 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package publy;

import com.beust.jcommander.Parameter;

/**
 *
 * @author Sander Verdonschot <sander.verdonschot at gmail.com>
 */
public class CommandLineArguments {

    @Parameter(names = {"-c", "--config"}, description = "Configuration file to use (XML)", arity = 1)
    private String config;
    @Parameter(names = {"-d", "--debug"}, description = "Enable debug output")
    private boolean debug = false;
    @Parameter(names = {"-g", "--gui"}, description = "Open the configuration GUI")
    private boolean gui = false;
    @Parameter(names = {"-h", "--help"}, description = "Display this usage information", help = true)
    private boolean help = false;
    @Parameter(names = {"-f", "--input"}, description = "Publication list to use (BibTeX)", arity = 1)
    private String input;
    @Parameter(names = {"-o", "--output"}, description = "Output file to use (HTML)", arity = 1)
    private String output;
    @Parameter(names = {"-q", "-s", "--quiet", "--silent"}, description = "Hide regular log output")
    private boolean silent = false;
    @Parameter(names = {"-v", "-V", "--version"}, description = "Display version information")
    private boolean version = false;
    @Parameter(names = {"-w", "--no-warn"}, description = "Hide warnings")
    private boolean hidewarnings = false;

    public String getConfig() {
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

    public String getInput() {
        return input;
    }

    public String getOutput() {
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
}
