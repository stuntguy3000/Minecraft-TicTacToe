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

package com.stuntguy3000.minecraft.tictactoe.core.objects;

import com.stuntguy3000.minecraft.tictactoe.PluginMain;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.Lang;
import com.stuntguy3000.minecraft.tictactoe.core.util.ArrayUtil;
import com.stuntguy3000.minecraft.tictactoe.handler.BoardHandler;
import com.stuntguy3000.minecraft.tictactoe.handler.ItemHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Represents an active game (or lobby), asscoated with one particular Board
 */
@Data
@RequiredArgsConstructor
public class Game {
    private final UUID gameId;
    private final UUID boardId;
    // Used to store a digital map of tic tac toe placed items (you know, the actual game)
    // Only contains played placed board position items
    private final HashMap<BoardPosition, UUID> playerTurns = new HashMap<>();
    private UUID player1Id;
    private UUID player2Id;
    private UUID winnerId;
    private ItemStack player1Item;
    private ItemStack player2Item;
    private int currentTurn; // 1 or 2
    private Gamestate gamestate = Gamestate.NONE;
    private int winTaskAnimationId = 0;

    /**
     * Change the gamestate of the game and perform the required game functions for the state change
     *
     * @param newGamestate Gamestate the new gamestate
     */
    public void changeGamestate(Gamestate newGamestate) {
        this.gamestate = newGamestate;

        switch (newGamestate) {
            case NONE: {
                // Reset Board (only valid on game start or Board destroy)
                getBoard().fillBoardItems(new ItemStack(Material.AIR), false);

                player1Id = null;
                player2Id = null;
                winnerId = null;
                player1Item = null;
                player2Item = null;
                currentTurn = 0;
                playerTurns.clear();
                break;
            }
            case WAITING: {
                // Reset Board and Put up the join blocks
                getBoard().fillBoardItems(ItemHandler.ITEM_GAME_JOIN, true);

                winnerId = null;
                currentTurn = 0;
                playerTurns.clear();
                break;
            }
            case INGAME: {
                // Start the game
                getBoard().fillBoardItems(new ItemStack(Material.AIR), false);
                playerTurns.clear();

                sendPlayersMessage(Lang.EVENT_GAME_START);
                setTurn(new Random().nextBoolean() ? 1 : 2); // Pick either 1 or 2
                break;
            }
            case END: {
                break;
            }
        }

        PluginMain.getInstance().getGameHandler().checkGame(this);
    }

    /**
     * Set's the current turn to playerNumber
     *
     * @param playerNumber int a numeric value representing a player (either 1 or 2)
     */
    private void setTurn(int playerNumber) {
        currentTurn = playerNumber;
        if (playerNumber != 1 && playerNumber != 2) {
            setTurn(1);
        }
    }

    /**
     * Plays the turn of the current player at a particular position on the Board
     *
     * @param boardPosition BoardPosition the position to play
     */
    public void playTurn(BoardPosition boardPosition) {
        // Check if the square already has been filled
        if (playerTurns.containsKey(boardPosition)) {
            // Silently cancel
            return;
        }

        // Update the item frame
        BoardItem boardItem = getBoard().getBoardItem(boardPosition);
        ItemFrame itemFrame = boardItem.getItemFrame();

        Player player = null;
        ItemStack playerItem = null;
        if (currentTurn == 1) {
            playerItem = getPlayer1Item();
            player = Bukkit.getPlayer(player1Id);
        } else if (currentTurn == 2) {
            playerItem = getPlayer2Item();
            player = Bukkit.getPlayer(player2Id);
        }

        ItemStack finalPlayerItem = playerItem;
        Bukkit.getScheduler().runTask(PluginMain.getInstance(), () -> {
            itemFrame.setItem(finalPlayerItem);
            itemFrame.setRotation(Rotation.NONE);
        });

        // Update the register
        playerTurns.put(boardPosition, player.getUniqueId());

        // Provide user feedback
        getBoard().playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2);
        setTurn(currentTurn + 1);

        // Check the game's status
        PluginMain.getInstance().getGameHandler().checkGame(this);
    }

    /**
     * Send all players in the Game a formatted message
     *
     * @param message String the message to send
     * @param format  Object[] any format variables
     */
    public void sendPlayersMessage(String message, Object... format) {
        if (getPlayer1Id() != null) {
            Player player1 = Bukkit.getPlayer(getPlayer1Id());

            if (player1 != null) {
                Lang.sendMessage(player1, message, format);
            }
        }

        if (getPlayer2Id() != null) {
            Player player2 = Bukkit.getPlayer(getPlayer2Id());
            if (player2 != null) {
                Lang.sendMessage(player2, message, format);
            }
        }
    }

    /**
     * Send all players an action bar message
     *
     * @param message String the message to send
     * @param format  Object[] any format variables
     */
    public void sendPlayersActionBar(String message, Object... format) {
        if (getPlayer1Id() != null) {
            Player player1 = Bukkit.getPlayer(getPlayer1Id());
            if (player1 != null) {
                PluginMain.getInstance().getActionBarUtil().sendStickyActionBarMessage(player1, String.format(message, format));
            }
        }

        if (getPlayer2Id() != null) {
            Player player2 = Bukkit.getPlayer(getPlayer2Id());
            if (player2 != null) {
                PluginMain.getInstance().getActionBarUtil().sendStickyActionBarMessage(player2, String.format(message, format));
            }
        }
    }

    /**
     * Returns the Board associated with this Game
     *
     * @return Board the associated Board object
     */
    public Board getBoard() {
        BoardHandler boardHandler = PluginMain.getInstance().getBoardHandler();

        return boardHandler.getBoardById(getBoardId());
    }

    /**
     * Returns if the game is tied
     *
     * @return true if the game is tied
     */
    public boolean isGameATie() {
        return findThreeInARow() == null && playerTurns.size() == 9;
    }

    // Optimization opportunity - only test modified cells and not the whole board

    /**
     * Used to find three items in a row (indicating a win condition)
     *
     * @return List the list of BoardPositions containing the same three items in a row (or null if not found)
     */
    public List<BoardPosition> findThreeInARow() {
        // Test Vertical Columns
        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_LEFT), getPlayerTurn(BoardPosition.TOP_MIDDLE), getPlayerTurn(BoardPosition.TOP_RIGHT))) {
            return Arrays.asList(BoardPosition.TOP_LEFT, BoardPosition.TOP_MIDDLE, BoardPosition.TOP_RIGHT);
        }

        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.MIDDLE_LEFT), getPlayerTurn(BoardPosition.CENTER), getPlayerTurn(BoardPosition.MIDDLE_RIGHT))) {
            return Arrays.asList(BoardPosition.MIDDLE_LEFT, BoardPosition.CENTER, BoardPosition.MIDDLE_RIGHT);
        }

        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.BOTTOM_LEFT), getPlayerTurn(BoardPosition.BOTTOM_MIDDLE), getPlayerTurn(BoardPosition.BOTTOM_RIGHT))) {
            return Arrays.asList(BoardPosition.BOTTOM_LEFT, BoardPosition.BOTTOM_MIDDLE, BoardPosition.BOTTOM_RIGHT);
        }

        // Test Horizontal Rows
        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_LEFT), getPlayerTurn(BoardPosition.MIDDLE_LEFT), getPlayerTurn(BoardPosition.BOTTOM_LEFT))) {
            return Arrays.asList(BoardPosition.TOP_LEFT, BoardPosition.MIDDLE_LEFT, BoardPosition.BOTTOM_LEFT);
        }

        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_MIDDLE), getPlayerTurn(BoardPosition.CENTER), getPlayerTurn(BoardPosition.BOTTOM_MIDDLE))) {
            return Arrays.asList(BoardPosition.TOP_MIDDLE, BoardPosition.CENTER, BoardPosition.BOTTOM_MIDDLE);
        }

        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_RIGHT), getPlayerTurn(BoardPosition.MIDDLE_RIGHT), getPlayerTurn(BoardPosition.BOTTOM_RIGHT))) {
            return Arrays.asList(BoardPosition.TOP_RIGHT, BoardPosition.MIDDLE_RIGHT, BoardPosition.BOTTOM_RIGHT);
        }

        // Test Diagonals
        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_LEFT), getPlayerTurn(BoardPosition.CENTER), getPlayerTurn(BoardPosition.BOTTOM_RIGHT))) {
            return Arrays.asList(BoardPosition.TOP_LEFT, BoardPosition.CENTER, BoardPosition.BOTTOM_RIGHT);
        }

        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_RIGHT), getPlayerTurn(BoardPosition.CENTER), getPlayerTurn(BoardPosition.BOTTOM_LEFT))) {
            return Arrays.asList(BoardPosition.TOP_RIGHT, BoardPosition.CENTER, BoardPosition.BOTTOM_LEFT);
        }

        return null;
    }

    /**
     * Returns the UUID of a player's turn at a boardPosition
     *
     * @param boardPosition BoardPosition the position of the board
     * @return uuid the id of the player who made the turn (or null if no turn made)
     */
    public UUID getPlayerTurn(BoardPosition boardPosition) {
        return playerTurns.getOrDefault(boardPosition, null);
    }

    /**
     * Cancels the win animation scheduled task
     */
    public void cancelWinAnimationTask() {
        Bukkit.getScheduler().cancelTask(winTaskAnimationId);
    }
}
