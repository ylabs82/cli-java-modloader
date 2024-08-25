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

package es.ylabs.clijavamodloader.commands.coregroups;

import es.ylabs.clijavamodloader.annotations.CliCommand;
import es.ylabs.clijavamodloader.annotations.CliCommandGroup;
import es.ylabs.clijavamodloader.helpers.ANSIHelpers;

@CliCommandGroup
public class HelpersGroup {

    private final String COMMAND_CLEAR = "clear";
    private final String COMMAND_EXIT = "exit";

    @CliCommand(command = COMMAND_CLEAR)
    public void clear(String[] args) {
        System.out.print(ANSIHelpers.ANSI_CLEAR);
    }

    @CliCommand(command = COMMAND_EXIT)
    public void exit(String[] args) {
        ANSIHelpers.printGreenAndBold("Exiting...");
        System.exit(0);
    }
}
