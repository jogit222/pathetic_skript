package io.github.pathetic_skript.pathfinder.expressions;

import ch.njol.skript.doc.*;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.util.Kleenean;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.doc.*;

import com.github.shanebeee.skr.Registration;

import de.bsommerfeld.pathetic.api.pathing.NeighborStrategies;
import de.bsommerfeld.pathetic.api.wrapper.PathPosition;
import de.bsommerfeld.pathetic.bukkit.context.BukkitEnvironmentContext;
import de.bsommerfeld.pathetic.bukkit.mapper.BukkitMapper;

import io.github.pathetic_skript.pathfinder.util.validationProcessor.CustomValidationProcessor;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.bsommerfeld.pathetic.api.factory.PathfinderFactory;
import de.bsommerfeld.pathetic.api.pathing.Pathfinder;
import de.bsommerfeld.pathetic.api.pathing.configuration.PathfinderConfiguration;
import de.bsommerfeld.pathetic.bukkit.provider.LoadingNavigationPointProvider;
import de.bsommerfeld.pathetic.engine.factory.AStarPathfinderFactory;

import javax.annotation.Nullable;

public class ExprPathfind extends SimpleExpression<Location> {
    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprPathfind.class, Location.class,
                        "path[[ ]find] from %location% to %location%")
                .name("Pathfinder - Get Path")
                .description("Get a path from a location to a location. Blocks the main server thread.")
                .examples("set {_nodes::*} to path from location(0, 0, 0) to location(5, 5, 5)")
                .since("1.0.0")
                .register();
    }
    @Nullable

    private Expression<Location> loc1;
    private Expression<Location> loc2;
    private Location[] array;
    public boolean init(Expression<?>[] exprs, int machedPattern, Kleenean isDelayed, ParseResult parseResult)  {
        this.loc1 = (Expression<Location>) exprs[0];
        this.loc2 = (Expression<Location>) exprs[1];
        return true;
    }
    protected Location[] get(Event event) {
        try {
            // Create the PathfinderFactory
            PathfinderFactory factory = new AStarPathfinderFactory();

            // Configure the pathfinder
            PathfinderConfiguration config;
            if (ExprAllowedBlocks.allowedBlocks.size() == 0) {
                config = PathfinderConfiguration.builder()
                        .provider(new LoadingNavigationPointProvider())
                        .async(false)
                        .maxIterations(100_000_000)
                        .neighborStrategy(NeighborStrategies.DIAGONAL_3D)
                        .build();

            } else {
                config = PathfinderConfiguration.builder()
                        .provider(new LoadingNavigationPointProvider())
                        .async(false)
                        .maxIterations(100_000_000)
                        .neighborStrategy(NeighborStrategies.DIAGONAL_3D)
                        .validationProcessors((ExprAllowedBlocks.allowedBlocks.size() > 0) ? List.of(new CustomValidationProcessor()) : null)
                        .build();
            }

            Logger logger = Bukkit.getLogger();
            // Create the pathfinder instance
            Pathfinder pathfinder = factory.createPathfinder(config);
            List<Location> nodes = new ArrayList<>();
            Pathfinder pf = pathfinder;
            assert this.loc1 != null;
            Location startPosBukkit = this.loc1.getSingle(event);
            World world = startPosBukkit.getWorld();
            logger.info("World " + world.toString());
            Location targetPosBukkit = this.loc2.getSingle(event);
            PathPosition startPos = new PathPosition(startPosBukkit.getBlockX(), startPosBukkit.getBlockY(), startPosBukkit.getBlockZ());
            PathPosition targetPos = new PathPosition(targetPosBukkit.getBlockX(), targetPosBukkit.getBlockY(), targetPosBukkit.getBlockZ());
            pf.findPath(startPos, targetPos, new BukkitEnvironmentContext(world))
                    .ifPresent(result -> {

                        // We have an usable result since it either found the path, or fallen back.
                        result.getPath().forEach(position -> {
                            Location location = BukkitMapper.toLocation(position, world).add(0.5, 0, 0.5);
                            // Do something with it.
                            nodes.add(location);
                        });

                    }).orElse(none -> {
                        // Handle no path found scenario
                        System.out.println("No path found between start and target positions.");

                    }).exceptionally(ex -> System.err.println("An exception occurred -> " + ex));
            array = nodes.toArray(new Location[0]);}
        catch (Exception e) {
            Bukkit.getLogger().severe("An exception occurred in ExprPathFind: get(Event event)" + e.getMessage());
            array = new Location[0];
        }
        finally {
            return array;
        }
    }
    @Override
    public boolean isSingle() {
        return false;
    }
    public Class  getReturnType() {
        return Location.class;
    }
    public String toString(Event e, boolean b) {
        return "path from " + this.loc1.toString(e,b) + " to " + this.loc2.toString(e,b);
    }
}