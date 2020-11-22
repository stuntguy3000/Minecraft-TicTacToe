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

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Functions and Strings of Language elements used by the plugin
 */
public class Lang {
    public static final String PLUGIN_PREFIX = "§3TicTacToe §f» §7";

    public static final String COMMAND_VERSION = "§7TicTacToe version §f%s§7 by §astuntguy3000§7.";
    public static final String COMMAND_HELP_TITLE = "§bCommand Help:";
    public static final String COMMAND_HELP_ENTRY = "§7/%s §e%s §8- §f%s";

    public static final String COMMAND_BOARD_CREATE_CANCEL = "§cBoard creation cancelled.";
    public static final String COMMAND_BOARD_CREATE_START_L1 = "§eBoard creation begun. Right click on the middle of a 3x3 grid of item frames.";
    public static final String COMMAND_BOARD_CREATE_START_L2 = "To cancel, run §b/%s board cancel§7.";
    public static final String COMMAND_BOARD_LIST_TITLE = "§eBoard List (%d):";
    public static final String COMMAND_BOARD_LIST_VALUE = " §8- §b%d, %d, %d, §7(§e§7%s)";

    public static final String EVENT_GAME_JOIN = "§a%s§r§a has joined this game.";
    public static final String EVENT_GAME_LEAVE = "§c%s§r§c has left this game.";
    public static final String EVENT_GAME_START = "§b§lThe game has begun!";
    public static final String EVENT_GAME_WAITING = "§eWaiting for a second player to join...";
    public static final String EVENT_GAME_WINNER = "§a§lGame Over!§r §7Winner: §e%s";

    public static final String ACTIONBAR_GAME_STATUS = "§3TicTacToe §8| §7%s";

    public static final String ERROR_PREFIX = "§cError: ";
    public static final String ERROR_PERMISSION_DENIED = ERROR_PREFIX + "You do not have permission to perform this action.";
    public static final String ERROR_NOT_PLAYER = ERROR_PREFIX + "You must be a player to perform this action.";
    public static final String ERROR_BOARD_CREATE = ERROR_PREFIX + "Unable to create board, is this the middle of a 3x3 grid of empty item frames?";
    public static final String ERROR_BLOCK_BREAK_EVENT_DENY = ERROR_PREFIX + "You are unable to break this block as it will destroy the board.";
    public static final String ERROR_BLOCK_PLACE_EVENT_DENY = ERROR_PREFIX + "You are place to this block as it will destroy the board.";
    public static final String ERROR_BOARD_REMOVE = ERROR_PREFIX + "No nearby boards found!";
    public static final String ERROR_NOT_IN_GAME = ERROR_PREFIX + "You are not in an active game.";
    public static final String ERROR_GAME_JOIN_FAIL = ERROR_PREFIX + "You are unable to join this game!";
    public static final String ERROR_COLOUR_SELECT_IN_USE = ERROR_PREFIX + "This colour is in use by the other player!";
    public static final String ERROR_NOT_YOUR_TURN = ERROR_PREFIX + "It is not your turn!";

    public static final String SUCCESS_PREFIX = "§aSuccess: ";
    public static final String SUCCESS_BOARD_CREATE = SUCCESS_PREFIX + "Board created, and ready to be used!";
    public static final String SUCCESS_BOARD_REMOVE_SUCCESS = SUCCESS_PREFIX + "Nearest board removed.";
    public static final String SUCCESS_COLOUR_SELECTED = SUCCESS_PREFIX + "Your colour has been chosen.";

    public static final String GAMESTATE_NONE = "None";
    public static final String GAMESTATE_WAITING = "Waiting";
    public static final String GAMESTATE_INGAME = "Ingame";
    public static final String GAMESTATE_END = "End";

    public static final String GAMESTATE_WAITING_DESCRIPTION = "Waiting for players...";
    public static final String GAMESTATE_INGAME_DESCRIPTION = "Current Turn: §e%s";
    public static final String GAMESTATE_END_DESCRIPTION = "Winner: §e%s";

    public static final String MENU_COLOURSELECTION_TITLE = "Choose your colour!";


    /**
     * Send a message to a CommandSender.
     * If sender is a Player, the message will be sent with the plugin's message prefix.
     *
     * @param sender CommandSender the entity to send the message to.
     * @param message String the message to send.
     */
    public static void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(PLUGIN_PREFIX + message);
        } else {
            sender.sendMessage(ChatColor.stripColor(message));
        }
    }
    /**
     * Send a formatted message to a CommandSender.
     * If sender is a Player, the message will be sent with the plugin's message prefix.
     *
     * @param sender CommandSender the entity to send the message to.
     * @param message String the message to send.
     * @param format Object[] format objects
     */
    public static void sendMessage(CommandSender sender, String message, Object... format) {
        sendMessage(sender, String.format(message, format));
    }
}
