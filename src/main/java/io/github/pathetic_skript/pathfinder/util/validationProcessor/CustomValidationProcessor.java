package io.github.pathetic_skript.pathfinder.util.validationProcessor;

import de.bsommerfeld.pathetic.api.wrapper.PathPosition;
import de.bsommerfeld.pathetic.bukkit.context.BukkitEnvironmentContext;
import de.bsommerfeld.pathetic.bukkit.mapper.BukkitMapper;
import io.github.pathetic_skript.pathfinder.expressions.ExprAllowedBlocks;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import de.bsommerfeld.pathetic.api.pathing.context.EnvironmentContext;
import de.bsommerfeld.pathetic.api.pathing.processing.context.EvaluationContext;
import de.bsommerfeld.pathetic.api.pathing.processing.ValidationProcessor;

import java.util.ArrayList;

public class CustomValidationProcessor implements ValidationProcessor {

    @Override
    public boolean isValid(EvaluationContext context) {
        EnvironmentContext ec = context.getEnvironmentContext();
        World world = ((BukkitEnvironmentContext) ec).getWorld();

        // The space where the mob's legs/feet will occupy
        PathPosition feetPos = context.getCurrentPathPosition();
        Block feetBlock = BukkitMapper.toLocation(feetPos, world).getBlock();

        // The space where the mob's head will occupy
        Block headBlock = feetBlock.getRelative(0, 1, 0);

        // The actual block the mob will stand on top of
        Block groundBlock = feetBlock.getRelative(0, -1, 0);

        ArrayList<Material> allowedBlocks = ExprAllowedBlocks.allowedBlocks;
        Material groundMaterial = groundBlock.getType();

        // 1. Ensure the ground under their feet is an allowed pathing surface
        if (!allowedBlocks.contains(groundMaterial)) {
            return false;
        }

        // 2. Ensure both the feet space and head space are clear (passable)
        // This allows them to step up onto hills, as long as air is clear above it.
        return feetBlock.isPassable() && headBlock.isPassable();
    }
}