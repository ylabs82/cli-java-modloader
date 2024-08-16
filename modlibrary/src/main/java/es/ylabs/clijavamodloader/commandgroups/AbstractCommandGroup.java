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

package es.ylabs.clijavamodloader.commandgroups;

import es.ylabs.clijavamodloader.annotations.Command;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractCommandGroup {

    private final static String ANSI_RESET = "\u001B[0m";
    private final static String ANSI_BOLD = "\u001B[1m";
    private final static String ANSI_RED = "\u001B[31m";

    private final Map<String, Consumer<String[]>> commands;

    public AbstractCommandGroup() {
        commands = new HashMap<>();
        Class<? extends AbstractCommandGroup> currentClass = this.getClass();

        for (Method method : currentClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);

                commands.put(command.name(), consumer -> {
                    try {
                        method.invoke(this, (Object) consumer);
                    } catch (Exception e) {
                        printRedAndBold("Error executing command " + command.name());
                        System.out.println();
                    }
                });
            }
        }
    }

    public Map<String, Consumer<String[]>> getCommands() {
        return commands;
    }

    private static void printRedAndBold(String message) {
        System.out.println(ANSI_BOLD + ANSI_RED + message + ANSI_RESET);
    }
}
