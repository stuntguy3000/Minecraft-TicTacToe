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

import com.stuntguy3000.minecraft.tictactoe.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Contains a static list of known game ItemStacks
 */
public class ItemHandler {
    private static final String ITEM_TAG_STRING = "§3TicTacToe";
    private static final List<String> ITEM_TAG_LIST = Collections.singletonList(ITEM_TAG_STRING);
    public static final ItemStack ITEM_GAME_JOIN = new ItemBuilder().material(Material.SLIME_BALL).displayName("§aRight-click to join!").displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_WHITE = new ItemBuilder().material(Material.WHITE_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_ORANGE = new ItemBuilder().material(Material.ORANGE_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_MAGENTA = new ItemBuilder().material(Material.MAGENTA_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_CYAN = new ItemBuilder().material(Material.CYAN_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_YELLOW = new ItemBuilder().material(Material.YELLOW_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_LIME = new ItemBuilder().material(Material.LIME_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_PINK = new ItemBuilder().material(Material.PINK_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_RED = new ItemBuilder().material(Material.RED_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_BLUE = new ItemBuilder().material(Material.BLUE_WOOL).displayLore(ITEM_TAG_LIST).getItem();

    // Could use NBT?
    /**
     * Checks if a item is tagged with ITEM_TAG_STRING (determing it to be a game item).
     * @param itemStack ItemStack the item to be checked
     * @return boolean true if itemStack is known to be a game item
     */
    public static boolean isTicTacToeItem(ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta != null) {
                List<String> lore = itemMeta.getLore();

                return lore != null && !lore.isEmpty() && lore.get(0).equals(ITEM_TAG_STRING);
            }
        }

        return false;
    }

    /**
     * Returns a list of all available player items
     * @return List a list of available player items
     */
    public static List<ItemStack> getAllPlayerItems() {
        return Arrays.asList(ITEM_PLAYER_WHITE.clone(), ITEM_PLAYER_ORANGE.clone(), ITEM_PLAYER_MAGENTA.clone(), ITEM_PLAYER_CYAN.clone(), ITEM_PLAYER_YELLOW.clone(), ITEM_PLAYER_LIME.clone(), ITEM_PLAYER_PINK.clone(), ITEM_PLAYER_RED.clone(), ITEM_PLAYER_BLUE.clone());
    }
}
