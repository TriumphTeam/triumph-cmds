/*
 * MIT License
 *
 * Copyright (c) 2019 Matt
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

package me.mattstudios.mf.base.components;

import me.mattstudios.mf.base.CommandBase;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.CodeSource;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Util {

    /**
     * Utility to use color codes easily
     *
     * @param message The message String
     * @return returns the string with color
     */
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Utility to get all the commands in a package for easy registration.
     * Special thanks to @PiggyPiglet - https://github.com/PiggyPiglet
     *
     * @param plugin The plugin's main class.
     * @param packge The package to search commands on.
     * @return A set containing all commands found in the specified package.
     */
    public static Set<CommandBase> getCommandsInPackage(JavaPlugin plugin, String packge) {

        final Set<CommandBase> commands = new HashSet<>();

        try {
            final ClassLoader loader = plugin.getClass().getClassLoader();
            final CodeSource src = plugin.getClass().getProtectionDomain().getCodeSource();

            if (src == null) return commands;

            final ZipInputStream zip = new ZipInputStream(src.getLocation().openStream());
            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                final String name = entry.getName();

                if (name.endsWith(".class") && name.startsWith(packge.replace('.', '/'))) {
                    final Class<?> clss;

                    try {
                        clss = loader.loadClass(name.replace('/', '.').replace(".class", ""));
                    } catch (Exception e) {
                        continue;
                    }

                    if (clss.getSuperclass() != CommandBase.class) continue;

                    commands.add((CommandBase) clss.newInstance());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error registering commands!");
        }

        return commands;
    }

}
