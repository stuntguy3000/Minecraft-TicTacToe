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

package com.stuntguy3000.minecraft.tictactoe.core.plugin.config;

import com.stuntguy3000.minecraft.tictactoe.PluginMain;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.PluginConfig;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.PluginConfigData;
import lombok.Getter;

/**
 * Represents the main configuration setting for the games
 */
@Getter
@PluginConfigData(configFilename = "main")
public class MainConfig extends PluginConfig {
    private boolean blockProtection = true;
    private boolean playerMoveEvents = true;
    private int maxPlayerBoardDistance = 10;
    private int endOfRoundSeconds = 3;

    public MainConfig() {
        super("main");
    }

    public static MainConfig getConfig() {
        return (MainConfig) PluginMain.getInstance().getConfigHandler().getConfig(MainConfig.class.getAnnotation(PluginConfigData.class).configFilename());
    }

    @Override
    public PluginConfig getSampleConfig() {
        return new MainConfig();
    }
}
