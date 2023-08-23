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

package com.stuntguy3000.minecraft.tictactoe.core.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Represents a Minecraft Plugin
 * <p>
 * This is an abstraction layer that sits in the middle between Bukkit and the Plugin to help simplify development
 */
public abstract class MinecraftPlugin extends JavaPlugin {
    /**
     * Register all defined handlers
     */
    public abstract void registerHandlers();

    /**
     * Register all defined commands
     */
    public abstract void registerCommands();

    /**
     * Register all defined event handlers
     */
    public abstract void registerEvents();

    /**
     * Set the instance of the plugin's main class object
     */
    public abstract void setInstance();

    @Override
    public void onLoad() {
        setInstance();

        // Setup data folder
        if (!this.getDataFolder().exists()) {
            if (!this.getDataFolder().mkdir()) {
                Bukkit.getLogger().log(Level.SEVERE, "[TicTacToe] Unable to create configuration folder!");
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }

        PluginDescriptionFile pluginDescriptionFile = this.getDescription();
        Bukkit.getLogger().log(Level.INFO, String.format("[TicTacToe] Loaded %s version %s by stuntguy3000.", pluginDescriptionFile.getName(), pluginDescriptionFile.getVersion()));
    }

    @Override
    public void onEnable() {
        registerHandlers();
        registerEvents();
        registerCommands();
    }

    @Override
    public abstract void onDisable();
}
