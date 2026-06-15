package io.github.pathetic_skript.pathfinder.expressions;

import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.util.Kleenean;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.doc.*;

import com.github.shanebeee.skr.Registration;

import de.bsommerfeld.pathetic.api.wrapper.PathPosition;
import de.bsommerfeld.pathetic.bukkit.context.BukkitEnvironmentContext;
import de.bsommerfeld.pathetic.bukkit.mapper.BukkitMapper;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.*;
import org.bukkit.World;
import org.bukkit.event.Event;

import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

import de.bsommerfeld.pathetic.api.factory.PathfinderFactory;
import de.bsommerfeld.pathetic.api.pathing.Pathfinder;
import de.bsommerfeld.pathetic.api.pathing.configuration.PathfinderConfiguration;
import de.bsommerfeld.pathetic.bukkit.provider.LoadingNavigationPointProvider;
import de.bsommerfeld.pathetic.engine.factory.AStarPathfinderFactory;

import javax.annotation.Nullable;

public class ExprAllowedBlocks extends SimpleExpression<Material> {
    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprAllowedBlocks.class, Material.class,
                        "allowed block[s]")
                .name("Pathfinder - Add/Remove Block For Valid Node")
                .description("Adds/Removes a block for valid nodes.")
                .examples("add stone to allowed blocks")
                .since("1.0.1")
                .register();
    }
    public static ArrayList<Material> allowedBlocks = new ArrayList<>();
    @Override
    public boolean init(Expression<?>[] exprs, int machedPattern, Kleenean isDelayed, ParseResult parseResult)  {
        return true;
    }

    @Override
    @Nullable
    public Class<?> [] acceptChange(Changer.ChangeMode mode)  {
        return switch (mode) {
            case ADD, REMOVE -> new Class[]{ItemType[].class};
            default -> null;
        };
    }

    @Override
    public Material[] get(Event event) {
        ArrayList<Material> materials = new ArrayList<>();

        return allowedBlocks.toArray(new Material[0]);
    }
    @Override
    public void change(Event event, Object [] delta, Changer.ChangeMode mode)  {
        for (Object obj : delta)  {
            if (!((ItemType) obj instanceof ItemType)) {
                continue;
            }
            switch (mode)  {
                case ADD -> {
                    allowedBlocks.add((Material) ((((ItemType) obj).getMaterial())));
                }
                case REMOVE -> {
                    allowedBlocks.remove((Material) ((((ItemType) obj).getMaterial())));
                }
            }
        }

    }
    @Override
    public boolean isSingle() {
        return false;
    }
    @Override
    public Class getReturnType() {
        return Material.class;
    }
    @Override
    public String toString(Event e, boolean b) {
        return "allowed blocks" + allowedBlocks;
    }
}