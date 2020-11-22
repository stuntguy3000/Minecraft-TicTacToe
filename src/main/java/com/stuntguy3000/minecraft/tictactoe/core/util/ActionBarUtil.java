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

package com.stuntguy3000.minecraft.tictactoe.core.util;

import com.stuntguy3000.minecraft.tictactoe.PluginMain;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class to facilitate action bar messages
 */
public class ActionBarUtil implements Listener {
    /**
     * A map of users and a sticky action bar message (that is re-applied every 2 seconds)
     */
    private final HashMap<UUID, String> activeMessages = new HashMap<>();

    /**
     * Send a single action bar message to Player
     * @param player Player the targeted player
     * @param message String the message to send
     */
    public void sendActionBarMessage(Player player, String message) {
        if (player == null || !player.isOnline()) {
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
    /**
     * Send a sticky action bar message to Player (one that does not disappear)
     * @param player Player the targeted player
     * @param message String the message to send
     */
    public void sendStickyActionBarMessage(final Player player, final String message) {
        sendActionBarMessage(player, message);
        activeMessages.put(player.getUniqueId(), message);
    }

    /**
     * Schedules the loop to re-send sticky action bar messages
     */
    public void runLoop() {
        PluginMain plugin = PluginMain.getInstance();

        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<UUID, String>> iterator = activeMessages.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<UUID, String> entry = iterator.next();

                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player != null && player.isOnline()) {
                        sendActionBarMessage(player, entry.getValue());
                    } else {
                        iterator.remove();
                    }
                }
            }
        }.runTaskTimer(plugin, 40, 40);
    }

    /**
     * Clear the current/sticky action bar message for a Player
     * @param player Player the targeted player
     */
    public void clearActionBarMessage(Player player) {
        activeMessages.remove(player.getUniqueId());
        sendActionBarMessage(player, "");
    }
}