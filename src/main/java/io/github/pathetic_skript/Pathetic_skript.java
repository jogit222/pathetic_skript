package io.github.pathetic_skript;

import io.github.pathetic_skript.pathfinder.expressions.ExprPathfind;
import io.github.pathetic_skript.pathfinder.asyncEffects.EffPathfindStart;
import io.github.pathetic_skript.pathfinder.expressions.ExprAllowedBlocks;

import de.bsommerfeld.pathetic.bukkit.PatheticBukkit;

import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.shanebeee.skr.Registration;

import org.bstats.bukkit.Metrics;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class Pathetic_skript extends JavaPlugin {
    @Override
    public void onEnable() {
        Registration registration = new Registration("pathetic-skript", false);
        PatheticBukkit.initialize(this);
        PatheticRegistration.register(registration);
        File file = new File(getDataFolder(), "allowedBlocks.txt");

        int pluginId = 32036;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(
                new AdvancedPie("syncAsync", () -> {
                    Map<String, Integer> map = new HashMap<>();
                    map.put("Sync", ExprPathfind.syncPathfinds);
                    map.put("Async", EffPathfindStart.asyncPathfinds);
                    return map;
                }));
        metrics.addCustomChart(
                new singleLineChart("totalPaths", () -> {
                    int total = ExprPathfind.syncPathfinds + EffPathfindStart.asyncPathfinds;
                    return total;
                })
        );

        if (!file.exists()) {
            getLogger().warning("File not found!");
            try {
                file.createNewFile();
            } catch(Exception e) {
                getLogger().severe("Error: cant create file allowedBlocks.txt");
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    ExprAllowedBlocks.allowedBlocks.add(Material.getMaterial(line));
                }
                catch(Exception e) {
                    getLogger().warning("invalid Material in allowedBlocks.txt: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            getLogger().severe("Could not read file: " + e.getMessage());
        }
        getLogger().info("Loaded allowedBlocks; Amount: " + ExprAllowedBlocks.allowedBlocks.size());

    }

    @Override
    public void onDisable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        File file = new File(getDataFolder(), "allowedBlocks.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Material material : ExprAllowedBlocks.allowedBlocks) {
                writer.write(material.name().toUpperCase());
                writer.newLine();
            }

        } catch (IOException e) {
            getLogger().severe("Could not write file: " + e.getMessage());
        }
        getLogger().info("Saved allowedBlocks");
        PatheticBukkit.shutdown();
    }
}
