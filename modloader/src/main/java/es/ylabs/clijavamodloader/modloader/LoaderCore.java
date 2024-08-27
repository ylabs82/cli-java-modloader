/*
 * MIT License
 *
 * Copyright 2024 Yago Mouriño Mendaña <ylabs82@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package es.ylabs.clijavamodloader.modloader;

import es.ylabs.clijavamodloader.annotations.CliCommand;
import es.ylabs.clijavamodloader.annotations.CliCommandGroup;
import es.ylabs.clijavamodloader.commands.management.CommandCollection;
import es.ylabs.clijavamodloader.helpers.ANSIHelpers;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.yaml.snakeyaml.Yaml;

public enum LoaderCore {
    INSTANCE;

    private final static String ANSI_RESET = "\u001B[0m";
    private final static String ANSI_BOLD = "\u001B[1m";
    private final static String ANSI_RED = "\u001B[31m";

    private final CommandCollection commandCollection;
    private final Map<String, ArrayList<String>> loadedModulesWithCommands;
    private final String pluginsHomePath;

    LoaderCore() {
        String DEFAULT_PLUGINS_HOME = "/tmp";
        String YAML_CONFIG_FILE = "application.yml";

        commandCollection = CommandCollection.INSTANCE.getInstance();
        loadedModulesWithCommands = new HashMap<>();

        // Get the path of the current JAR/Class file.
        CodeSource codeSource = LoaderCore.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().getPath());
        String jarDir = jarFile.getParentFile().getPath();

        // The configuration file is expected to be in the same directory as the
        // JAR file.
        File configFile = new File(jarDir, YAML_CONFIG_FILE);

        // TODO: this code may be optimized
        if (configFile.exists()) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = null;

            try (InputStream inputStream = configFile.toURI().toURL().openStream()) {
                yamlMap = yaml.load(inputStream);
            } catch (Exception ignored) {
            } finally {
                if (yamlMap != null) {
                    pluginsHomePath = (String) yamlMap.get("pluginshome");
                } else {
                    pluginsHomePath = DEFAULT_PLUGINS_HOME;
                }
            }
        } else {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlMap = null;

            try (InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(YAML_CONFIG_FILE)) {
                yamlMap = yaml.load(inputStream);
            } catch (Exception ignored) {
            } finally {
                if (yamlMap != null) {
                    pluginsHomePath = (String) yamlMap.get("pluginshome");
                } else {
                    pluginsHomePath = DEFAULT_PLUGINS_HOME;
                }
            }
        }

        File directory = new File(pluginsHomePath);

        if (!directory.exists()) {
            throw new RuntimeException("Plugins home directory does not exist");
        }
    }

    public LoaderCore getInstance() {
        return INSTANCE;
    }

    /**
     * Takes the loaded modules and creates an ordered list.
     *
     * @return An ordered list of the loaded modules.
     */
    public List<String> getLoadedModules() {
        return loadedModulesWithCommands.keySet().stream().sorted().toList();
    }

    /**
     * Loads a module.
     *
     * @param module The module to load (a JAR file).
     *
     * @return True if the module was loaded, false if the module was not found.
     * @throws Exception If the module is already loaded or an error occurs while
     *      loading the module.
     */
    public boolean loadModule(String module) throws Exception {
        if (module.contains("/")) {
            String base = module.substring(0, module.lastIndexOf('/'));
            String file = module.substring(module.lastIndexOf('/') + 1);
            return loadModule(base, file);
        } else {
            return loadModule(pluginsHomePath, module);
        }
    }

    /**
     * Loads a module.
     *
     * @param pluginPath The path to the directory where the module is located.
     * @param module The module to load (a JAR file).
     *
     * @return True if the module was loaded, false if the module was not found.
     * @throws Exception If the module is already loaded or an error occurs while
     *      loading the module.
     */
    public boolean loadModule(String pluginPath, String module) throws Exception {
        if (mapContainsKey(loadedModulesWithCommands, module)) {
            throw new Exception("Module already loaded");
        }

        File file = new File(pluginPath, module);

        if (file.exists()) {
            try (JarFile jar = new JarFile(file)) {
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();

                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName()
                                .replace("/", ".")
                                .replace(".class", "");

                        try (URLClassLoader classLoader = new URLClassLoader(
                                new URL[]{ file.toURI().toURL() },
                                this.getClass().getClassLoader())) {
                            Class<?> groupClass = classLoader.loadClass(className);

                            if (groupClass.isAnnotationPresent(CliCommandGroup.class)) {
                                Object groupInstance = groupClass.getDeclaredConstructor().newInstance();
                                Map<String, Consumer<String[]>> newCommands =
                                        getCommandsFromGroup(groupClass, groupInstance);

                                try {
                                    commandCollection.addCommands(newCommands);

                                    if (!mapContainsKey(loadedModulesWithCommands, module)) {
                                        loadedModulesWithCommands.put(module,
                                                new ArrayList<>(newCommands.keySet()));
                                    } else {
                                        loadedModulesWithCommands.get(module)
                                                .addAll(newCommands.keySet());
                                    }
                                } catch (Exception e) {
                                    if (mapContainsKey(loadedModulesWithCommands, module)) {
                                        unloadModule(module);
                                    }

                                    throw new Exception(e.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            throw new Exception(e.getMessage());
                        }
                    }
                }

                if (mapContainsKey(loadedModulesWithCommands, module)) {
                    return true;
                } else {
                    throw new Exception("Unknown error");
                }
            } catch (Exception e) {
                throw new Exception("Error loading module " + module
                        + System.lineSeparator()
                        + e.getMessage());
            }
        } else {
            return false;
        }
    }

    /**
     * Unloads a previously loaded module.
     *
     * @param module The module to unload.
     *
     * @return True if the module was unloaded, false if the module was not found.
     */
    public boolean unloadModule(String module) {
        if (mapContainsKey(loadedModulesWithCommands, module)) {
            ArrayList<String> commandsToRemove = loadedModulesWithCommands.get(module);
            commandCollection.removeCommands(commandsToRemove);
            loadedModulesWithCommands.remove(module);

            return true;
        } else {
            return false;
        }
    }

    private boolean mapIsNullOrEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    private boolean mapContainsKey(final Map<?, ?> map, String key) {
        return !mapIsNullOrEmpty(map) && map.containsKey(key);
    }

    private static Map<String, Consumer<String[]>> getCommandsFromGroup(Class<?> groupClass,
                                                                        Object groupInstance) {
        Map<String, Consumer<String[]>> toret = new HashMap<>();

        for (Method method : groupClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(CliCommand.class)) {
                CliCommand cliCommand = method.getAnnotation(CliCommand.class);

                toret.put(cliCommand.command(), consumer -> {
                    try {
                        method.invoke(groupInstance, (Object) consumer);
                    } catch (Exception e) {
                        ANSIHelpers.printRedAndBold("Error executing command " + cliCommand.command());
                        System.out.println();
                    }
                });
            }
        }

        return toret;
    }
}
