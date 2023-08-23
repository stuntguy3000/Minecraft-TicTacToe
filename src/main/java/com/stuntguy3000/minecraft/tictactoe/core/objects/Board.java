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
import com.stuntguy3000.minecraft.tictactoe.handler.BoardHandler;
import com.stuntguy3000.minecraft.tictactoe.handler.ItemHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a 3x3 grid of item frames in a Minecraft world (aka a 'board')
 */
@Data
@AllArgsConstructor
public class Board {
    private UUID id;
    private WorldVector centerVector;

    /**
     * Returns the BoardItem object located at boardPosition
     *
     * @param boardPosition the position of the board item
     * @return the BoardItem located at boardPosition (or null)
     */
    public BoardItem getBoardItem(BoardPosition boardPosition) {
        // Validation checks
        if (centerVector == null || centerVector.getWorldName() == null || centerVector.getLocation() == null || centerVector.getLocation().getWorld() == null) {
            return null;
        }

        // Find the ItemFrame entity at the center
        Location centerLocation = centerVector.getLocation();
        Collection<Entity> entities = centerLocation.getWorld().getNearbyEntities(centerLocation, 0, 0, 0, entity -> entity instanceof ItemFrame);

        BlockFace attachedFace = null;

        // Identify the center item's attached face
        if (entities.size() == 1) {
            for (Entity entity : entities) {
                attachedFace = ((ItemFrame) entity).getAttachedFace();
                break;
            }
        }

        // Sanity check
        if (attachedFace == null) {
            return null;
        }

        // Identify the correct offsets
        double xOffset = 0;
        double yOffset = 0;
        double zOffset = 0;

        switch (boardPosition) {
            case TOP_LEFT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = -1;
                        yOffset = 1;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = 1;
                        zOffset = 1;
                        break;
                    case SOUTH:
                        xOffset = 1;
                        yOffset = 1;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = 1;
                        zOffset = -1;
                        break;
                }
                break;
            }
            case TOP_RIGHT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = 1;
                        yOffset = 1;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = 1;
                        zOffset = -1;
                        break;
                    case SOUTH:
                        xOffset = -1;
                        yOffset = 1;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = 1;
                        zOffset = 1;
                        break;
                }
                break;
            }
            case MIDDLE_LEFT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = 1;
                        yOffset = 0;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = 0;
                        zOffset = 1;
                        break;
                    case SOUTH:
                        xOffset = -1;
                        yOffset = 0;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = 0;
                        zOffset = -1;
                        break;
                }
                break;
            }
            case MIDDLE_RIGHT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = -1;
                        yOffset = 0;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = 0;
                        zOffset = -1;
                        break;
                    case SOUTH:
                        xOffset = 1;
                        yOffset = 0;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = 0;
                        zOffset = 1;
                        break;
                }
                break;
            }
            case BOTTOM_LEFT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = -1;
                        yOffset = -1;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = -1;
                        zOffset = 1;
                        break;
                    case SOUTH:
                        xOffset = 1;
                        yOffset = -1;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = -1;
                        zOffset = -1;
                        break;
                }
                break;
            }
            case BOTTOM_RIGHT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = 1;
                        yOffset = -1;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = -1;
                        zOffset = -1;
                        break;
                    case SOUTH:
                        xOffset = -1;
                        yOffset = -1;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = -1;
                        zOffset = 1;
                        break;
                }
                break;
            }
            case TOP_MIDDLE: {
                yOffset = 1;
                break;
            }
            case BOTTOM_MIDDLE: {
                yOffset = -1;
                break;
            }
        }

        // Identify the new item
        Location newLocation = centerLocation.clone().add(xOffset, yOffset, zOffset);
        BoardItem boardItem = new BoardItem(new WorldVector(newLocation), this, boardPosition);
        ItemFrame itemFrame = boardItem.getItemFrame();

        // Final sanity check
        if (itemFrame == null) {
            return null;
        }

        return boardItem;
    }

    /**
     * Gets the BoardPosition of itemFrame
     *
     * @param itemFrame the input itemFrame
     * @return the BoardPosition where itemFrame is located
     */
    public BoardPosition getPositionOfItemFrame(ItemFrame itemFrame) {
        for (BoardPosition itemPosition : BoardPosition.values()) {
            BoardItem boardItem = getBoardItem(itemPosition);

            if (boardItem != null && boardItem.getItemFrame().equals(itemFrame)) {
                return itemPosition;
            }
        }

        return null;
    }

    /**
     * Performs checks of the item frame entities to determine if the grid pattern is intact and is ready to be used to
     * play
     * <p>
     * Invalid states are - Any board item that is missing - Any board item that contains any UNEXPECTED items (non-game
     * items)
     *
     * @return true if the board is deemed "valid"
     */
    public boolean isBoardValid() {
        // Loop through all board items
        for (BoardPosition itemPosition : BoardPosition.values()) {
            BoardItem boardItem = getBoardItem(itemPosition);

            if (boardItem == null) {
                return false;
            } else {
                ItemFrame itemFrame = boardItem.getItemFrame();

                if (itemFrame.getItem().getType() != Material.AIR && !ItemHandler.isTicTacToeItem(itemFrame.getItem())) {
                    return false;
                }
            }
        }

        // Check if any other boards exist at this location
        BoardHandler boardHandler = PluginMain.getInstance().getBoardHandler();
        Board otherBoard = boardHandler.getBoardAtBlockLocation(getCenterVector().getBlockLocationVector());

        return otherBoard == null || otherBoard == this;
    }

    /**
     * Plays a sound to all players near a Board
     *
     * @param sound Sound the sound to play
     * @param pitch int the pitch to play at (1-2)
     */
    public void playSound(Sound sound, int pitch) {
        Location location = getCenterVector().getBlockLocation();
        Objects.requireNonNull(location.getWorld()).playSound(location, sound, SoundCategory.NEUTRAL, 1, pitch);
    }

    /**
     * Puts an ItemStack in an ItemFrame on the board
     *
     * @param position      BoardPosition the position to fill
     * @param doDisplayName boolean true to enable the display the name of the item on the board (on player hover)
     * @param item          ItemStack the item to display
     */
    private void setBoardItem(BoardPosition position, boolean doDisplayName, ItemStack item) {
        ItemFrame itemFrame = getBoardItem(position).getItemFrame();

        // Remove Display Name
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            if (!doDisplayName) {
                itemMeta.setDisplayName(null);
                item.setItemMeta(itemMeta);
            }
        }

        // Clear any rotation
        itemFrame.setRotation(Rotation.NONE);

        // Apply Item
        itemFrame.setItem(item, false);
    }

    /**
     * Fill all items in the board
     *
     * @param item          ItemStack the item to display
     * @param doDisplayName boolean true to enable the display the name of the item on the board (on player hover)
     */
    public void fillBoardItems(ItemStack item, boolean doDisplayName) {
        ItemStack fillItem = item.clone();
        PluginMain pluginMain = PluginMain.getInstance();

        for (BoardPosition boardPosition : BoardPosition.values()) {
            // Task required to fix issue with block updates not being delivered
            Bukkit.getScheduler().runTask(pluginMain, () -> setBoardItem(boardPosition, doDisplayName, fillItem));
        }
    }
}
