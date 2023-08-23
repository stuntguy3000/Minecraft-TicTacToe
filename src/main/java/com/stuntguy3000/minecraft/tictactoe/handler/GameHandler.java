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

package com.stuntguy3000.minecraft.tictactoe.handler;

import com.stuntguy3000.minecraft.tictactoe.PluginMain;
import com.stuntguy3000.minecraft.tictactoe.core.objects.Board;
import com.stuntguy3000.minecraft.tictactoe.core.objects.BoardPosition;
import com.stuntguy3000.minecraft.tictactoe.core.objects.Game;
import com.stuntguy3000.minecraft.tictactoe.core.objects.Gamestate;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.Lang;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.config.MainConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Handles all game-related operations
 */
public class GameHandler {
    private final PluginMain plugin;

    @Getter
    private final List<Game> games = new ArrayList<>();

    public GameHandler() {
        this.plugin = PluginMain.getInstance();
    }

    /**
     * Generates a Game object for each loaded @see {Board}.
     */
    public void generateGames() {
        BoardHandler boardHandler = plugin.getBoardHandler();

        for (UUID boardId : boardHandler.getBoards().keySet()) {
            Board board = boardHandler.getBoardById(boardId);

            if (board == null) {
                Bukkit.getLogger().log(Level.SEVERE, "[TicTacToe] Unable to create game for board " + boardId.toString() + "!");
            } else {
                generateGame(board);
            }
        }
    }

    /**
     * Generates a game for a specific @{see Board}
     *
     * @param board Board the board to create a game for.
     */
    public void generateGame(Board board) {
        // Generate the game
        Game game = new Game(UUID.randomUUID(), board.getId());
        games.add(game);

        // Setup the game
        game.changeGamestate(Gamestate.WAITING);
    }

    /**
     * Returns the Game a player is in.
     *
     * @param player Player the player to lookup.
     * @return Game the current game the player is in, null if not found.
     */
    public Game getGameForPlayer(Player player) {
        for (Game game : games) {
            if (game.getPlayer1Id() == player.getUniqueId() || game.getPlayer2Id() == player.getUniqueId()) {
                return game;
            }
        }

        return null;
    }

    /**
     * Returns the Game associated with a specific Board
     *
     * @param board Board the board to lookup with
     * @return Game the game associated with the Board, null if not found/
     */
    public Game getGameForBoard(Board board) {
        for (Game game : games) {
            if (game.getBoard() != null && game.getBoard().equals(board)) {
                return game;
            }
        }

        return null;
    }

    /**
     * Try to add a player into a game
     *
     * @param player Player the targeted player
     * @param game   Game the game to add the player into
     * @return boolean true if successful
     */
    public boolean tryAddToGame(Player player, Game game) {
        if (player == null) {
            return false;
        }

        if (game.getGamestate() != Gamestate.WAITING) {
            return false;
        }

        boolean successful = false;

        if (game.getPlayer1Id() == null) {
            game.setPlayer1Id(player.getUniqueId());
            successful = true;
        } else if (game.getPlayer2Id() == null) {
            game.setPlayer2Id(player.getUniqueId());
            successful = true;
        }

        if (successful) {
            game.sendPlayersMessage(Lang.EVENT_GAME_JOIN, player.getDisplayName());
            checkGame(game);

            plugin.getMenuHandler().createColourSelectionMenu(player);
        }

        return successful;
    }

    /**
     * Remove a Player from their current Game
     *
     * @param player Player the targeted player
     */
    public void removeFromGame(Player player) {
        Game game = getGameForPlayer(player);

        if (game != null) {
            game.sendPlayersMessage(Lang.EVENT_GAME_LEAVE, player.getDisplayName());

            if (game.getPlayer1Id() == player.getUniqueId()) {
                game.setPlayer1Id(null);
            } else if (game.getPlayer2Id() == player.getUniqueId()) {
                game.setPlayer2Id(null);
            }

            plugin.getActionBarUtil().clearActionBarMessage(player);

            checkGame(game);
        }
    }

    /**
     * Checks a game for various trigger states (e.g. calculation of win/loss conditions or reset game when all players leave).
     * Designed to be called at any point during execution, this function can expect to be called after ANY game event, significant or not.
     *
     * @param game Game the game to be checked
     */
    public void checkGame(Game game) {
        // Reset game if in invalid state or a player quits
        if ((game.getGamestate() == Gamestate.INGAME || game.getGamestate() == Gamestate.END) && (game.getPlayer1Id() == null || game.getPlayer2Id() == null)) {
            game.changeGamestate(Gamestate.WAITING);
            return;
        }

        switch (game.getGamestate()) {
            case NONE: {
                break;
            }
            case WAITING: {
                if (game.getPlayer1Id() != null && game.getPlayer2Id() != null) {
                    game.changeGamestate(Gamestate.INGAME);
                    return;
                } else {
                    game.sendPlayersActionBar(Lang.ACTIONBAR_GAME_STATUS, Lang.GAMESTATE_WAITING_DESCRIPTION);
                    game.sendPlayersMessage(Lang.EVENT_GAME_WAITING);
                }
                break;
            }
            case INGAME: {
                UUID currentTurnPlayerId = null;

                if (game.getCurrentTurn() == 1) {
                    currentTurnPlayerId = game.getPlayer1Id();
                } else if (game.getCurrentTurn() == 2) {
                    currentTurnPlayerId = game.getPlayer2Id();
                }

                // Check Win
                List<BoardPosition> boardPositions = game.findThreeInARow();

                if (boardPositions != null) {
                    // We have a winner!
                    game.setWinnerId(game.getPlayerTurn(boardPositions.get(0))); // Pretty crude but it works
                    game.changeGamestate(Gamestate.END);
                    return;
                }

                // Check tie
                if (game.isGameATie()) {
                    // We have a tie!
                    game.setWinnerId(null);
                    game.changeGamestate(Gamestate.END);
                    return;
                }

                // Update Title Bar
                Player player = Bukkit.getPlayer(currentTurnPlayerId);
                game.sendPlayersActionBar(Lang.ACTIONBAR_GAME_STATUS, String.format(Lang.GAMESTATE_INGAME_DESCRIPTION, player.getDisplayName()));
                break;
            }
            case END: {
                String winnerName;
                UUID winnerId = game.getWinnerId();

                if (winnerId == null) {
                    winnerName = "Tie!";
                } else {
                    Player winnerPlayer = Bukkit.getPlayer(game.getWinnerId());

                    if (winnerPlayer == null) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(game.getWinnerId());
                        winnerName = offlinePlayer.getName();
                    } else {
                        winnerName = winnerPlayer.getDisplayName();
                    }
                }

                game.sendPlayersMessage(Lang.EVENT_GAME_WINNER, winnerName);

                // Update Title Bar
                game.sendPlayersActionBar(Lang.ACTIONBAR_GAME_STATUS, String.format(Lang.GAMESTATE_END_DESCRIPTION, winnerName));

                // Schedule a re-match
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (game.getGamestate() == Gamestate.END) {
                        game.changeGamestate(Gamestate.WAITING);
                    }
                }, 20 * MainConfig.getConfig().getEndOfRoundSeconds());

                // End of round animation to play until end of game
                BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    // Cache various things

                    // Validation check
                    if (game.getGamestate() == Gamestate.END && game.getBoard() != null) {
                        // Try to use the already defined three-in-a-row
                        List<BoardPosition> boardPositions = game.findThreeInARow();

                        // Determine which board positions to strobe
                        if ((boardPositions == null || boardPositions.size() != 3) && game.isGameATie()) {
                            // Tie - load in all board positions as this is a tie
                            boardPositions = new ArrayList<>(Arrays.asList(BoardPosition.values()));
                        }

                        // Strobe the relevant board positions
                        if (boardPositions != null && !boardPositions.isEmpty()) {
                            // We have board positions to strobe
                            for (BoardPosition boardPosition : boardPositions) {
                                ItemFrame itemFrame = game.getBoard().getBoardItem(boardPosition).getItemFrame();

                                if (itemFrame.getItem().getType() == Material.AIR) {
                                    // Set back to the player's item
                                    UUID placedItemPlayerId = game.getPlayerTurn(boardPosition);

                                    // Determine who actually made that turn and show that item
                                    if (placedItemPlayerId == game.getPlayer1Id()) {
                                        itemFrame.setItem(game.getPlayer1Item(), false);
                                    } else if (placedItemPlayerId == game.getPlayer2Id()) {
                                        itemFrame.setItem(game.getPlayer2Item(), false);
                                    }
                                } else {
                                    // Set to air
                                    itemFrame.setItem(new ItemStack(Material.AIR), false);
                                }
                            }
                            return;
                        }
                    }

                    game.cancelWinAnimationTask();
                }, 0, 5);
                game.setWinTaskAnimationId(bukkitTask.getTaskId());
                break;
            }
        }
    }

    /**
     * Destroys a game instance
     *
     * @param game Game the game to destroy
     */
    public void destroyGame(Game game) {
        games.remove(game);
    }
}
