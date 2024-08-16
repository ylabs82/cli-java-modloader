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

package es.ylabs.clijavamodloader.commands.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public enum CommandCollection {
    INSTANCE;

    private final Map<String, Consumer<String[]>> commands;

    CommandCollection() {
        commands = new HashMap<>();
    }

    public CommandCollection getInstance() {
        return INSTANCE;
    }

    /**
     * Add new commands to the collection.
     *
     * @param newCommands A map containing the new commands to add.
     *
     * @throws Exception If there are duplicate commands, they won't be added
     *                  and an exception will be thrown.
     */
    public void addCommands(Map<String, Consumer<String[]>> newCommands) throws Exception {
        Map<String, Consumer<String[]>> result = new HashMap<>(newCommands);
        result.keySet().retainAll(commands.keySet());

        if (!result.isEmpty()) {
            throw new Exception("Duplicate commands found");
        } else {
            commands.putAll(newCommands);
        }
    }

    /**
     * Execute a command.
     *
     * @param command A string containing the command to execute and its arguments.
     *
     * @throws Exception If the command is not found, an exception will be thrown.
     */
    public void executeCommand(String command) throws Exception {
        String[] commandArray = command.split(" ");

        if (commands.containsKey(commandArray[0])) {
            commands.get(commandArray[0]).accept(commandArray);
        } else {
            throw new Exception("Command not found");
        }
    }

    /**
     * Remove commands from the collection.
     *
     * @param commandsToRemove A list of commands to remove.
     */
    public void removeCommands(ArrayList<String> commandsToRemove) {
        commandsToRemove.forEach(commands::remove);
    }
}
