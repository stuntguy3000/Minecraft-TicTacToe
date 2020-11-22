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

package com.stuntguy3000.minecraft.tictactoe.event;

import com.stuntguy3000.minecraft.tictactoe.PluginMain;
import com.stuntguy3000.minecraft.tictactoe.core.objects.*;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.Lang;
import com.stuntguy3000.minecraft.tictactoe.handler.BoardHandler;
import com.stuntguy3000.minecraft.tictactoe.handler.GameHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

/**
 * Handles player action events.
 */
@Data
@AllArgsConstructor
public class PlayerActionEvents implements Listener {
    private final PluginMain plugin;

    public PlayerActionEvents() {
        this.plugin = PluginMain.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemRemove(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (entity instanceof ItemFrame && damager instanceof Player) {
            // Is this a board item?
            Block block = entity.getLocation().getBlock();
            Board board = plugin.getBoardHandler().getBoardAtBlockLocation(new WorldVector(block.getLocation()));
            if (board != null) {
                // Cancel the event
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDestroy(HangingBreakByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity remover = event.getRemover();

        if (entity instanceof ItemFrame && remover instanceof Player) {
            // Is this a board item?
            Block block = entity.getLocation().getBlock();
            Board board = plugin.getBoardHandler().getBoardAtBlockLocation(new WorldVector(block.getLocation()));
            if (board != null) {
                // Cancel the event
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRightClick(PlayerInteractEntityEvent event) {
        // Filter out unwanted events
        if (!(event.getRightClicked() instanceof ItemFrame) || event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        // Setup variables
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
        Block block = itemFrame.getLocation().getBlock();

        BoardHandler boardHandler = plugin.getBoardHandler();

        // Is the player setting up a board?
        if (boardHandler.isBoardCreator(id)) {
            // Cancel the event
            event.setCancelled(true);

            player.sendMessage("");

            // Try to make a board and see if it's valid
            Board newBoard = new Board(UUID.randomUUID(), new WorldVector(itemFrame.getLocation()));
            if (!newBoard.isBoardValid()) {
                Lang.sendMessage(player, Lang.ERROR_BOARD_CREATE);
                Lang.sendMessage(player, Lang.COMMAND_BOARD_CREATE_START_L2, "tictactoe");
            } else {
                boardHandler.addBoard(newBoard);
                boardHandler.removeBoardCreator(id);
                Lang.sendMessage(player, Lang.SUCCESS_BOARD_CREATE);
            }

            player.sendMessage("");
            return;
        }

        // Is this a board item?
        Board board = boardHandler.getBoardAtBlockLocation(new WorldVector(block.getLocation()));
        if (board != null) {
            // Cancel the event
            event.setCancelled(true);

            GameHandler gameHandler = plugin.getGameHandler();

            // Begin to process the events
            Game playerGame = gameHandler.getGameForPlayer(player);
            Game boardGame = gameHandler.getGameForBoard(board);

            if (boardGame == null) {
                // Shouldn't happen, but just in case...
                Lang.sendMessage(player, Lang.ERROR_GAME_JOIN_FAIL);
            } else if (playerGame == null) {
                // Filter out players not in a game
                if (boardGame.getGamestate() == Gamestate.WAITING) {
                    // Join player to game
                    gameHandler.tryAddToGame(player, boardGame);
                } else {
                    // Ignore this action
                    return;
                }
            } else {
                // Process item frame click from a player in a game
                // Have they clicked on the expected board?
                if (boardGame.getBoard() != board) {
                    // I don't think this is possible unless the boards are super close - so it's just ignore it for now
                    return;
                }

                // Is it ingame?
                if (boardGame.getGamestate() != Gamestate.INGAME) {
                    return;
                }

                // Is it their turn?
                if (!(boardGame.getCurrentTurn() == 1 && boardGame.getPlayer1Id() == player.getUniqueId()) && !(boardGame.getCurrentTurn() == 2 && boardGame.getPlayer2Id() == player.getUniqueId())) {
                    // No
                    // Second check possibly redundant, o well
                    Lang.sendMessage(player, Lang.ERROR_NOT_YOUR_TURN);
                    return;
                }

                // Process the click
                BoardPosition itemPosition = board.getPositionOfItemFrame(itemFrame);
                boardGame.playTurn(itemPosition);
            }
        }
    }
}
