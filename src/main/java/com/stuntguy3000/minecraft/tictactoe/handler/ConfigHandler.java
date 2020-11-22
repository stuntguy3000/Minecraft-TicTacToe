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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stuntguy3000.minecraft.tictactoe.PluginMain;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.PluginConfig;
import com.stuntguy3000.minecraft.tictactoe.core.plugin.MinecraftPlugin;
import lombok.Getter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles configuration files. Configuration files are implemented manually using GSON to serialize and deserialize against known @see {PluginConfig} classes.
 */
public class ConfigHandler {
    @Getter
    private final HashMap<String, PluginConfig> configClasses = new HashMap<>();
    @Getter
    private final Gson gson;
    @Getter
    private final List<String> loadedConfigs = new ArrayList<>();

    private final MinecraftPlugin plugin;

    /**
     * Create a new ConfigHandler instance and setup GSON
     */
    public ConfigHandler() {
        this.plugin = PluginMain.getInstance();

        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        gson = builder.create();
    }

    /**
     * Register a PluginConfig class
     *
     * @param pluginConfig PluginConfig the plugin config class to be registered
     */
    public void registerConfiguration(PluginConfig pluginConfig) {
        configClasses.put(pluginConfig.getConfigName(), pluginConfig);
    }

    /**
     * Load all configuration files from disk
     */
    public void loadConfigurations() {
        for (Map.Entry<String, PluginConfig> config : new HashMap<>(configClasses).entrySet()) {
            File configFile = new File(plugin.getDataFolder() + File.separator + config.getKey() + ".json");

            try {
                if (!configFile.exists()) {
                    saveConfiguration(config.getValue());
                    loadConfigurations();
                    return;
                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8);

                    configClasses.put(config.getKey(), gson.fromJson(inputStreamReader, config.getValue().getClass()));
                    loadedConfigs.add(config.getKey());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * Returns a PluginConfig by it's name
     * @param name String the name of the PluginConfig
     * @return PluginConfig the associated object, or null if not found
     */
    public PluginConfig getConfig(String name) {
        for (Map.Entry<String, PluginConfig> minecraftConfig : getConfigClasses().entrySet()) {
            if (minecraftConfig.getKey().equalsIgnoreCase(name)) {
                return minecraftConfig.getValue();
            }
        }

        return null;
    }

    /**
     * Saves a PluginConfig instance to disk
     *
     * @param pluginConfig PluginConfig the config class object to save to disk
     */
    public void saveConfiguration(PluginConfig pluginConfig) {
        File configFile = new File(plugin.getDataFolder() + File.separator + pluginConfig.getConfigName() + ".json");

        if (!loadedConfigs.contains(pluginConfig.getConfigName())) {
            loadedConfigs.add(pluginConfig.getConfigName());
        }

        String json = gson.toJson(pluginConfig);
        FileOutputStream outputStream;

        try {
            if (!configFile.exists()) {
                json = gson.toJson(pluginConfig.getSampleConfig());
                configFile.createNewFile();
            }
            outputStream = new FileOutputStream(configFile);
            assert json != null;
            outputStream.write(json.getBytes(StandardCharsets.UTF_8));
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

