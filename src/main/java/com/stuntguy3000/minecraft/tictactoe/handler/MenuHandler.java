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
import com.stuntguy3000.minecraft.tictactoe.core.objects.Game;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.Lang;
import io.mazenmc.menuapi.MenuFactory;
import io.mazenmc.menuapi.items.Item;
import io.mazenmc.menuapi.menu.Menu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Handles the interactions to MenuAPI
 */
public class MenuHandler {
    private final PluginMain plugin;

    public MenuHandler() {
        this.plugin = PluginMain.getInstance();
    }

    /**
     * Create a colour selection menu
     *
     * @param player Player the targeted playern
     */
    public void createColourSelectionMenu(Player player) {
        // Build Menu
        Menu menu = MenuFactory.createMenu(Lang.MENU_COLOURSELECTION_TITLE, 9, true);

        int i = 0;
        for (ItemStack playerItem : ItemHandler.getAllPlayerItems()) {
            menu.setItem(i, new Item() {
                @Override
                public ItemStack stack() {
                    return playerItem;
                }

                @Override
                public void act(Player player, ClickType clickType) {
                    Game game = plugin.getGameHandler().getGameForPlayer(player);

                    if (game != null) {
                        // Check if the other player has swiped this colour
                        ItemStack otherPlayer;
                        if (game.getPlayer1Id() == player.getUniqueId()) {
                            otherPlayer = game.getPlayer2Item();
                        } else {
                            otherPlayer = game.getPlayer1Item();
                        }

                        if (otherPlayer != null) {
                            if (otherPlayer.getType() == playerItem.getType()) {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                                Lang.sendMessage(player, Lang.ERROR_COLOUR_SELECT_IN_USE);
                                return;
                            }
                        }

                        // Save the selection
                        if (game.getPlayer1Id() == player.getUniqueId()) {
                            game.setPlayer1Item(playerItem);
                        } else {
                            game.setPlayer2Item(playerItem);
                        }

                        // Show success feedback
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                    }

                    // Wrap it up!
                    player.closeInventory();
                }
            });

            i++;
        }

        // Show to Player
        menu.showTo(player);
    }
}
