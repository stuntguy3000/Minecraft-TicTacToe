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
import com.stuntguy3000.minecraft.tictactoe.core.objects.*;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.config.BoardsConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import java.util.*;
import java.util.logging.Level;

/**
 * Handles game boards
 * <p>
 * A game board consists of a 3x3 square of item frames
 */
public class BoardHandler {
    private final PluginMain plugin;
    private final ArrayList<UUID> boardCreators = new ArrayList<>();
    @Getter
    private HashMap<UUID, Board> boards = new HashMap<>();

    public BoardHandler() {
        this.plugin = PluginMain.getInstance();
    }

    /**
     * Returns a Board by it's unique ID.
     *
     * @param id UUID the id of the board
     * @return Board the associated Board, or null if not found.
     */
    public Board getBoardById(UUID id) {
        return boards.get(id);
    }

    /**
     * Returns a Board that is at an exact BlockLocation
     *
     * @param blockLocation locationVector the BlockLocation to check for a Board.
     * @return Board the associated Board, or null if not found.
     */
    public Board getBoardAtBlockLocation(WorldVector blockLocation) {
        for (Board board : boards.values()) {
            for (BoardPosition itemPosition : BoardPosition.values()) {
                BoardItem boardItem = board.getBoardItem(itemPosition);

                if (boardItem != null) {
                    WorldVector boardItemBlockVector = boardItem.getLocation().getBlockLocationVector();

                    if (boardItemBlockVector.equals(blockLocation)) {
                        return board;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Returns a Board that is at an exact BlockLocation
     *
     * @param searchLocation Location the BlockLocation to check for a Board.
     * @param maxDistance    double the maximum distance searchLocation can be away from the center of a Board
     * @return Board the associated Board, or null if not found.
     */
    public Board getBoardClosestToLocation(Location searchLocation, double maxDistance) {
        HashMap<Board, Double> nearbyBoards = new HashMap<>();

        // Find nearby boards
        for (Board board : boards.values()) {
            for (BoardPosition itemPosition : BoardPosition.values()) {
                BoardItem boardItem = board.getBoardItem(itemPosition);

                if (boardItem != null) {
                    try {
                        double distance = boardItem.getLocation().getLocation().distance(searchLocation);

                        if (distance <= maxDistance) {
                            nearbyBoards.put(board, distance);
                        }
                    } catch (IllegalArgumentException ignored) {
                        // Thrown when two locations are in different dimensions
                    }
                }
            }
        }

        // Sort by smallest distance
        if (!nearbyBoards.isEmpty()) {
            Board closestBoard = null;
            double closestDistance = Double.MAX_VALUE;

            for (Map.Entry<Board, Double> entrySet : nearbyBoards.entrySet()) {
                Board board = entrySet.getKey();
                double distance = entrySet.getValue();

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestBoard = board;
                }
            }

            return closestBoard;
        }

        return null;
    }

    /**
     * Adds a new Board (and saves to disk).
     *
     * @param board Board the board to add.
     */
    public void addBoard(Board board) {
        boards.put(board.getId(), board);
        saveBoards();

        // Generate Game
        plugin.getGameHandler().generateGame(board);
    }

    /**
     * Destroys a Board from the plugin (and removes it from disk).
     *
     * @param board Board the board to remove.
     */
    public void destroyBoard(Board board) {
        // Stop any active games
        Game game = plugin.getGameHandler().getGameForBoard(board);
        game.changeGamestate(Gamestate.NONE);
        plugin.getGameHandler().destroyGame(game);

        // Destroy the board
        Bukkit.getLogger().log(Level.INFO, String.format("[TicTacToe] Board %s was removed due to an admin command.", board.getId()));
        boards.remove(board.getId());
        saveBoards();
    }

    /**
     * Validate and save all boards to disk.
     */
    public void saveBoards() {
        // Validate boards
        validateBoards();

        // Save to disk
        BoardsConfig boardsConfig = BoardsConfig.getConfig();
        boardsConfig.setSavedBoards(boards);
        boardsConfig.saveConfig();

        // Log
        Bukkit.getLogger().log(Level.INFO, String.format("[TicTacToe] Saved and validated %s board%s to boards.json", boards.size(), (boards.size() > 1 ? "s" : "")));
    }

    /**
     * Load all boards from disk into the plugin.
     */
    public void loadBoards() {
        // Load from disk
        BoardsConfig boardsConfig = BoardsConfig.getConfig();
        boards = boardsConfig.getSavedBoards();

        // Validate & Save boards
        saveBoards();

        // Log
        Bukkit.getLogger().log(Level.INFO, String.format("[TicTacToe] Loaded and validated %s board%s from boards.json", boards.size(), (boards.size() > 1 ? "s" : "")));
    }

    /**
     * Validate all loaded boards
     */
    private void validateBoards() {
        // Validate boards
        Iterator<Board> iterator = boards.values().iterator();
        while (iterator.hasNext()) {
            Board board = iterator.next();

            if (!board.isBoardValid()) {
                // Invalid board found
                iterator.remove();

                Bukkit.getLogger().log(Level.WARNING, String.format("[TicTacToe] Board %s was removed due to a validation error.", board.getId()));
            }
        }
    }

    /**
     * Adds a board creator to the boardCreator tracker map
     *
     * @param id UUID the id of the player who is creating a board
     */
    public void addBoardCreator(UUID id) {
        boardCreators.add(id);
    }

    /**
     * Returns if a UUID is currently creating a board
     *
     * @param id UUID the player's unique id
     * @return true if the player is creating a board
     */
    public boolean isBoardCreator(UUID id) {
        return boardCreators.contains(id);
    }

    /**
     * Removes a board creator from boardCreator tracker map
     *
     * @param id UUID the id of the player who is creating a board
     */
    public void removeBoardCreator(UUID id) {
        boardCreators.remove(id);
    }

    public boolean isBoardBlock(Block originalBlock) {
        Collection<Entity> entities = originalBlock.getWorld().getNearbyEntities(originalBlock.getLocation(), 2, 2, 2, entity -> entity instanceof ItemFrame);

        for (Entity entity : entities) {
            if (entity instanceof ItemFrame) {
                ItemFrame itemFrame = (ItemFrame) entity;
                BlockFace attachedFace = itemFrame.getAttachedFace();
                Block attachedBlock = itemFrame.getLocation().getBlock().getRelative(attachedFace);

                // Is this itemFrame attached to this block?
                // This is determined by getting the Block location of the ItemFrame and then testing the location
                // of the block on the attached face.
                if (attachedBlock.getLocation().equals(originalBlock.getLocation())) {
                    return true;
                }
            }
        }

        return false;
    }
}
