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

package es.ylabs.clijavamodloader;

import es.ylabs.clijavamodloader.commands.management.CommandCollection;
import es.ylabs.clijavamodloader.commands.coregroups.HelpersGroup;
import es.ylabs.clijavamodloader.helpers.ANSIHelpers;
import es.ylabs.clijavamodloader.helpers.ReadString;
import es.ylabs.clijavamodloader.modloader.LoaderCommands;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class App {
    public static void main(String[] args) {
        CommandCollection commandCollection = CommandCollection.INSTANCE.getInstance();
        ReadString readString = new ReadString("$ ");

        try {
            HelpersGroup helpersGroup = new HelpersGroup();
            LoaderCommands loaderGroup = new LoaderCommands();

            Map<String, Consumer<String[]>> newCommands = new HashMap<>();

            newCommands.put("clear", helpersGroup::clear);
            newCommands.put("exit", helpersGroup::exit);
            newCommands.put("lsmod", loaderGroup::lsmod);
            newCommands.put("modprobe", loaderGroup::modprobe);
            newCommands.put("rmmod", loaderGroup::rmmod);

            commandCollection.addCommands(newCommands);
        } catch (Exception e) {
            ANSIHelpers.printRedAndBold(e.getMessage());
            System.exit(-1);
        }

        while (true) {
            String command = readString.readString();

            if (!command.isBlank()) {
                try {
                    commandCollection.executeCommand(command);
                } catch (Exception e) {
                    ANSIHelpers.printRedAndBold(e.getMessage());
                }
            }
        }
    }
}
