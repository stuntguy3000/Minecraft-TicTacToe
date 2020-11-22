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

package com.stuntguy3000.minecraft.tictactoe;

import com.stuntguy3000.minecraft.tictactoe.command.TicTacToeCommand;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.MinecraftPlugin;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.config.BoardsConfig;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.config.MainConfig;
import com.stuntguy3000.minecraft.tictactoe.core.util.ActionBarUtil;
import com.stuntguy3000.minecraft.tictactoe.event.PlayerActionEvents;
import com.stuntguy3000.minecraft.tictactoe.event.PlayerBlockEvents;
import com.stuntguy3000.minecraft.tictactoe.event.PlayerMoveEvents;
import com.stuntguy3000.minecraft.tictactoe.event.PlayerStateEvents;
import com.stuntguy3000.minecraft.tictactoe.handler.BoardHandler;
import com.stuntguy3000.minecraft.tictactoe.handler.ConfigHandler;
import com.stuntguy3000.minecraft.tictactoe.handler.GameHandler;
import com.stuntguy3000.minecraft.tictactoe.handler.MenuHandler;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.logging.Level;

/**
 * Represents the entry point/main class for this plugin
 */
@Getter
public final class PluginMain extends MinecraftPlugin {

    @Getter
    private static PluginMain instance;

    private ConfigHandler configHandler;
    private GameHandler gameHandler;
    private BoardHandler boardHandler;
    private MenuHandler menuHandler;

    private ActionBarUtil actionBarUtil;

    @Override
    public void registerHandlers() {
        actionBarUtil = new ActionBarUtil();
        actionBarUtil.runLoop();

        configHandler = new ConfigHandler();
        gameHandler = new GameHandler();
        boardHandler = new BoardHandler();
        menuHandler = new MenuHandler();

        configHandler.registerConfiguration(new MainConfig());
        configHandler.registerConfiguration(new BoardsConfig());
        configHandler.loadConfigurations();

        boardHandler.loadBoards();
        gameHandler.generateGames();
    }

    @Override
    public void registerCommands() {
        Objects.requireNonNull(this.getCommand("tictactoe")).setExecutor(new TicTacToeCommand(this));
    }

    @Override
    public void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new PlayerStateEvents(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerActionEvents(this), this);

        if (MainConfig.getConfig().isBlockProtection()) {
            Bukkit.getLogger().log(Level.INFO, "[TicTacToe] Enabling block protection...");
            this.getServer().getPluginManager().registerEvents(new PlayerBlockEvents(this), this);
        }

        if (MainConfig.getConfig().isPlayerMoveEvents()) {
            Bukkit.getLogger().log(Level.INFO, "[TicTacToe] Enabling player movement events...");
            this.getServer().getPluginManager().registerEvents(new PlayerMoveEvents(this), this);
        }
    }

    @Override
    public void setInstance() {
        instance = this;
    }

    @Override
    public void onDisable() {

    }
}
