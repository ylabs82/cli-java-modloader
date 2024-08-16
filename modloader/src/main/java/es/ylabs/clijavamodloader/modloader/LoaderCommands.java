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

import es.ylabs.clijavamodloader.annotations.Command;
import es.ylabs.clijavamodloader.annotations.CommandGroup;
import es.ylabs.clijavamodloader.commandgroups.AbstractCommandGroup;
import es.ylabs.clijavamodloader.helpers.ANSIHelpers;

import java.util.List;

@CommandGroup
public class LoaderCommands extends AbstractCommandGroup {

    private final String COMMAND_LISTMODS = "lsmod";
    private final String COMMAND_LOADMOD = "modprobe";
    private final String COMMAND_UNLOADMOD = "rmmod";

    private final LoaderCore loaderCore;

    public LoaderCommands() {
        loaderCore = LoaderCore.INSTANCE.getInstance();
    }

    @Command(name = COMMAND_LISTMODS)
    public void listMods(String[] args) {
        if (args.length == 1) {
            List<String> loadedModules = loaderCore.getLoadedModules();

            if (loadedModules.isEmpty()) {
                ANSIHelpers.printRedAndBold("No modules loaded");
            } else {
                loadedModules.forEach(ANSIHelpers::printGreenAndBold);
            }
        } else {
            ANSIHelpers.printRedAndBold("Invalid arguments");
        }
    }

    @Command(name = COMMAND_LOADMOD)
    public void load(String[] args) {
        if (args.length == 2) {
            try {
                if (loaderCore.loadModule(args[1])) {
                    ANSIHelpers.printGreenAndBold("Module " + args[1] + " loaded");
                } else {
                    ANSIHelpers.printRedAndBold("Module not found");
                }
            } catch (Exception e) {
                ANSIHelpers.printRedAndBold(e.getMessage());
            }
        } else {
            ANSIHelpers.printRedAndBold("Invalid arguments");
        }
    }

    @Command(name = COMMAND_UNLOADMOD)
    public void unload(String[] args) {
        if (args.length == 2) {
            if (loaderCore.unloadModule(args[1])) {
                ANSIHelpers.printYellowAndBold("Module " + args[1] + " unloaded");
            } else {
                ANSIHelpers.printRedAndBold("Module not found");
            }
        } else {
            ANSIHelpers.printRedAndBold("Invalid arguments");
        }
    }
}
