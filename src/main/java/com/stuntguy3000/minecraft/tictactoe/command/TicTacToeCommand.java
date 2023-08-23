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

package com.stuntguy3000.minecraft.tictactoe.command;

import com.stuntguy3000.minecraft.tictactoe.PluginMain;
import com.stuntguy3000.minecraft.tictactoe.core.objects.Board;
import com.stuntguy3000.minecraft.tictactoe.core.objects.Game;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.Lang;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.Perm;
import com.stuntguy3000.minecraft.tictactoe.handler.BoardHandler;
import com.stuntguy3000.minecraft.tictactoe.handler.GameHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Handles the processing of the /tictactoe command
 *
 * @author stuntguy3000
 */
@Data
@AllArgsConstructor
public class TicTacToeCommand implements CommandExecutor, TabExecutor {
    private PluginMain pluginMain;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BoardHandler boardHandler = pluginMain.getBoardHandler();
        GameHandler gameHandler = pluginMain.getGameHandler();

        switch (args.length) {
            case 1: {
                if (args[0].equalsIgnoreCase("version")) {
                    Lang.sendMessage(sender, Lang.COMMAND_VERSION, PluginMain.getInstance().getDescription().getVersion());
                    return true;
                } else if (args[0].equalsIgnoreCase("board") && Perm.tryPerm(sender, Perm.COMMAND_ADMIN)) {
                    Lang.sendMessage(sender, Lang.COMMAND_HELP_TITLE);
                    Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "board list", "View a list of known boards");
                    Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "board remove [id]", "Removes a board (either at location or by specifying an id)");
                    Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "board create", "Creates a board");
                    Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "board cancel", "Cancel board creation");
                    return true;
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (isPlayer(sender)) {
                        Player player = (Player) sender;
                        Game game = gameHandler.getGameForPlayer(player);

                        if (game == null) {
                            Lang.sendMessage(sender, Lang.ERROR_NOT_IN_GAME);
                        } else {
                            gameHandler.removeFromGame(player);
                        }
                    }
                    return true;
                }
                break;
            }
            case 2: {
                // Board Command
                if (args[0].equalsIgnoreCase("board") && Perm.tryPerm(sender, Perm.COMMAND_ADMIN)) {
                    // Create Subcommand
                    if (args[1].equalsIgnoreCase("create")) {
                        if (isPlayer(sender)) {
                            Player player = (Player) sender;
                            UUID id = player.getUniqueId();

                            // Is the player not in a game?
                            if (pluginMain.getGameHandler().getGameForPlayer(player) != null) {
                                return true;
                            }

                            // Is the player not a Board Creator?
                            if (!boardHandler.isBoardCreator(id)) {
                                // Start process
                                boardHandler.addBoardCreator(id);

                                player.sendMessage("");
                                Lang.sendMessage(sender, Lang.COMMAND_BOARD_CREATE_START_L1);
                                Lang.sendMessage(sender, Lang.COMMAND_BOARD_CREATE_START_L2, label);
                                player.sendMessage("");
                            }
                        }

                        return true;
                    } else if (args[1].equalsIgnoreCase("cancel")) {
                        if (isPlayer(sender)) {
                            Player player = (Player) sender;
                            UUID id = player.getUniqueId();

                            if (boardHandler.isBoardCreator(id)) {
                                // Cancel process
                                boardHandler.removeBoardCreator(id);

                                player.sendMessage("");
                                Lang.sendMessage(sender, Lang.COMMAND_BOARD_CREATE_CANCEL);
                                player.sendMessage("");
                            }
                        }

                        return true;
                    } else if (args[1].equalsIgnoreCase("list")) {
                        // Process a board list command
                        Lang.sendMessage(sender, Lang.COMMAND_BOARD_LIST_TITLE, boardHandler.getBoards().values().size());

                        for (Board board : boardHandler.getBoards().values()) {
                            Location location = board.getCenterVector().getBlockLocation();
                            Lang.sendMessage(sender, Lang.COMMAND_BOARD_LIST_VALUE, location.getBlockX(), location.getBlockY(), location.getBlockZ(), board.getId());
                        }

                        return true;
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        // Process a board remove command
                        // This variant has does not have a third argument, so we will try to find the nearest one
                        if (isPlayer(sender)) {
                            Player player = (Player) sender;
                            Board board = boardHandler.getBoardClosestToLocation(player.getLocation(), 5);

                            if (board == null) {
                                Lang.sendMessage(sender, Lang.ERROR_BOARD_REMOVE);
                            } else {
                                boardHandler.destroyBoard(board);
                                Lang.sendMessage(sender, Lang.SUCCESS_BOARD_REMOVE_SUCCESS);
                            }
                        }
                        return true;
                    }
                }
                break;
            }
            case 3: {
                // Board Command
                if (args[0].equalsIgnoreCase("board") && Perm.tryPerm(sender, Perm.COMMAND_ADMIN)) {
                    // Remove Command with Arguments
                    if (args[1].equalsIgnoreCase("remove")) {
                        String boardIdInput = args[2];
                        UUID boardId = null;

                        try {
                            boardId = UUID.fromString(boardIdInput);
                        } catch (IllegalArgumentException ignored) {

                        }

                        Board board = boardHandler.getBoardById(boardId);

                        if (board == null) {
                            Lang.sendMessage(sender, Lang.ERROR_BOARD_REMOVE);
                        } else {
                            boardHandler.destroyBoard(board);
                            Lang.sendMessage(sender, Lang.SUCCESS_BOARD_REMOVE_SUCCESS);
                        }
                        return true;
                    }
                }
                break;
            }
        }

        // Help Menu
        Lang.sendMessage(sender, Lang.COMMAND_HELP_TITLE);

        if (Perm.tryPerm(sender, Perm.COMMAND_ADMIN)) {
            Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "board", "Access board specific commands");
        }

        Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "leave", "Leave the current game");
        Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "version", "View plugin information");
        Lang.sendMessage(sender, Lang.COMMAND_HELP_ENTRY, label, "help", "View plugin commands");

        return true;
    }

    private boolean isPlayer(CommandSender sender) {
        boolean player = (sender instanceof Player);

        if (!player) {
            Lang.sendMessage(sender, Lang.ERROR_NOT_PLAYER);
        }

        return player;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        System.out.println(Arrays.toString(args));
        System.out.println(args.length);

        if (sender instanceof Player) {
            switch (args.length) {
                case 1: {
                    List<String> commands = new ArrayList<>();

                    commands.add("leave");
                    commands.add("version");
                    commands.add("help");

                    if (Perm.tryPerm(sender, Perm.COMMAND_ADMIN)) {
                        commands.add("board");
                    }

                    return commands;
                }
                case 2: {
                    String subcommand = args[0];

                    if (subcommand.equalsIgnoreCase("board")) {
                        return Arrays.asList("create", "cancel", "list", "remove");
                    }

                    break;
                }
            }
        }

        return null;
    }
}
