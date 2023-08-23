/*
 * MIT License
 *
 * Copyright (c) 2020 Luke Anderson (stuntguy3000)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.stuntguy3000.minecraft.tictactoe.core.plugin;

import org.bukkit.command.CommandSender;

/**
 * Functions and Strings of Permission elements used by the plugin
 */
public class Perm {
    public static final String COMMAND_ADMIN = "tictactoe.admin";

    /**
     * Test if CommandSender has a specific permission, if not, send an error.
     *
     * @param commandSender CommandSender the entity to test against
     * @param perm          String the permission string to test
     * @return true if commandSender has permission
     */
    public static boolean tryPerm(CommandSender commandSender, String perm) {
        if (commandSender.hasPermission(perm)) {
            return true;
        }

        Lang.sendMessage(commandSender, Lang.ERROR_PERMISSION_DENIED);
        return false;
    }
}
