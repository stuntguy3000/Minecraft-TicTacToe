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
import com.stuntguy3000.minecraft.tictactoe.core.objects.Board;
import com.stuntguy3000.minecraft.tictactoe.core.objects.WorldVector;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.Lang;
import com.stuntguy3000.minecraft.tictactoe.handler.BoardHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Handles player block events.
 */
@Data
@AllArgsConstructor
public class PlayerBlockEvents implements Listener {
    private final PluginMain plugin;

    public PlayerBlockEvents() {
        this.plugin = PluginMain.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        // Try to locate any boards in this location
        BoardHandler boardHandler = plugin.getBoardHandler();
        Board board = boardHandler.getBoardAtBlockLocation(new WorldVector(block.getLocation()));

        if (board != null) {
            event.setCancelled(true);

            Lang.sendMessage(player, Lang.ERROR_BLOCK_PLACE_EVENT_DENY);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        // Try to locate any boards attached to this block
        BoardHandler boardHandler = plugin.getBoardHandler();

        Block northBlock = block.getRelative(BlockFace.NORTH);
        Board northBoard = boardHandler.getBoardAtBlockLocation(new WorldVector(northBlock.getLocation()));
        Block southBlock = block.getRelative(BlockFace.SOUTH);
        Board southBoard = boardHandler.getBoardAtBlockLocation(new WorldVector(southBlock.getLocation()));
        Block westBlock = block.getRelative(BlockFace.WEST);
        Board westBoard = boardHandler.getBoardAtBlockLocation(new WorldVector(westBlock.getLocation()));
        Block eastBlock = block.getRelative(BlockFace.EAST);
        Board eastBoard = boardHandler.getBoardAtBlockLocation(new WorldVector(eastBlock.getLocation()));

        if (northBoard != null || southBoard != null || westBoard != null || eastBoard != null) {
            event.setCancelled(true);
            Lang.sendMessage(player, Lang.ERROR_BLOCK_BREAK_EVENT_DENY);
        }
    }
}
