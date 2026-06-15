package io.github.pathetic_skript;

import io.github.pathetic_skript.pathfinder.expressions.ExprPathfind;
import io.github.pathetic_skript.pathfinder.expressions.ExprAsyncPathfind;
import io.github.pathetic_skript.pathfinder.asyncEffects.EffPathfindStart;
import io.github.pathetic_skript.pathfinder.expressions.ExprAllowedBlocks;

import de.bsommerfeld.pathetic.bukkit.PatheticBukkit;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.shanebeee.skr.Registration;

public final class Pathetic_skript extends JavaPlugin {
    private Registration registration;
    @Override
    public void onEnable() {
        this.registration = new Registration("pathetic-skript", false);
        PatheticBukkit.initialize(this);
        EffPathfindStart.register(registration);
        ExprAsyncPathfind.register(registration);
        ExprPathfind.register(registration);
        ExprAllowedBlocks.register(registration);
        this.registration.finalizeRegistration();
    }

    @Override
    public void onDisable() {
        PatheticBukkit.shutdown();
    }
}
