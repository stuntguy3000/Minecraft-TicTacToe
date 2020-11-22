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

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * Represents a location in a Minecraft world, minus the World object (used for map storage and configuration files)
 */
@Data
public class WorldVector {
    private final String worldName;
    private final Vector coords;

    public WorldVector(Location location) {
        this.worldName = Objects.requireNonNull(location.getWorld()).getName();
        this.coords = location.toVector();
    }

    /**
     * Generates and returns a Bukkit Location object based on this vector.
     *
     * @return Location the created Bukkit Location object
     */
    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            return null;
        }

        return new Location(world, coords.getX(), coords.getY(), coords.getZ());
    }

    /**
     * Generates and returns a Bukkit Block Location object based on this vector.
     *
     * @return Location the created Bukkit Block Location object
     */
    public Location getBlockLocation() {
        Location location = getLocation();

        if (location == null) {
            return null;
        }

        return location.getBlock().getLocation();
    }

    /**
     * Generates and returns a WorldVector of the Block Location of this vector
     *
     * @return WorldVector the created WorldVector of the Block Location of this vector
     */
    public WorldVector getBlockLocationVector() {
        Location blockLocation = getBlockLocation();

        if (blockLocation != null) {
            return new WorldVector(blockLocation);
        } else {
            return null;
        }
    }
}