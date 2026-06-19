package io.github.pathetic_skript.pathfinder.expressions;

import ch.njol.skript.doc.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.util.Kleenean;
import ch.njol.skript.lang.SkriptParser.ParseResult;

import com.github.shanebeee.skr.Registration;

import de.bsommerfeld.pathetic.api.pathing.NeighborStrategies;
import de.bsommerfeld.pathetic.api.wrapper.PathPosition;
import de.bsommerfeld.pathetic.bukkit.context.BukkitEnvironmentContext;
import de.bsommerfeld.pathetic.bukkit.mapper.BukkitMapper;

import io.github.pathetic_skript.pathfinder.util.CustomNeighborStrategies.CustomNeighborStrategies;
import io.github.pathetic_skript.pathfinder.util.costProcessor.CustomCostProcessor;
import io.github.pathetic_skript.pathfinder.util.validationProcessor.CustomValidationProcessor;
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

    private PathfinderFactory pathfinder = new AStarPathfinderFactory();
    private Expression<Location> loc1;
    private Expression<Location> loc2;
    private Location[] array;
    public boolean init(Expression<?>[] exprs, int machedPattern, Kleenean isDelayed, ParseResult parseResult)  {
        this.loc1 = (Expression<Location>) exprs[0];
        this.loc2 = (Expression<Location>) exprs[1];
        return true;
    }
    public static int syncPathfinds = 0;
    protected Location[] get(Event event) {
        syncPathfinds++;
        try {
            // Create the PathfinderFactory
            // Configure the pathfinder
            PathfinderConfiguration config;
            if (ExprAllowedBlocks.allowedBlocks.isEmpty()) {
                config = PathfinderConfiguration.builder()
                        .provider(new LoadingNavigationPointProvider())
                        .async(false)
                        .maxIterations(100_000_000)
                        .costProcessor(List.of(new CustomCostProcessor()))
                        .neighborStrategy((ExprNeighborStrategies.neighborMoves.isEmpty()) ? NeighborStrategies.DIAGONAL_3D : new CustomNeighborStrategies())
                        .build();

            } else {
                config = PathfinderConfiguration.builder()
                        .provider(new LoadingNavigationPointProvider())
                        .async(false)
                        .maxIterations(100_000_000)
                        .costProcessor(List.of(new CustomCostProcessor()))
                        .neighborStrategy((ExprNeighborStrategies.neighborMoves.isEmpty()) ? NeighborStrategies.DIAGONAL_3D : new CustomNeighborStrategies())
                        .validationProcessors((!ExprAllowedBlocks.allowedBlocks.isEmpty()) ? List.of(new CustomValidationProcessor()) : null)
                        .build();
            }

            Logger logger = Bukkit.getLogger();
            // Create the pathfinder instance
            Pathfinder pathfinder = this.pathfinder.createPathfinder(config);
            List<Location> nodes = new ArrayList<>();
            assert this.loc1 != null;
            Location startPosBukkit = this.loc1.getSingle(event);
            World world = startPosBukkit.getWorld();
            Location targetPosBukkit = this.loc2.getSingle(event);
            PathPosition startPos = new PathPosition(startPosBukkit.getBlockX(), startPosBukkit.getBlockY(), startPosBukkit.getBlockZ());
            PathPosition targetPos = new PathPosition(targetPosBukkit.getBlockX(), targetPosBukkit.getBlockY(), targetPosBukkit.getBlockZ());
            pathfinder.findPath(startPos, targetPos, new BukkitEnvironmentContext(world))
                    .ifPresent(result -> {

                        // We have an usable result since it either found the path, or fallen back.
                        result.getPath().forEach(position -> {
                            Location location = BukkitMapper.toLocation(position, world).add(0.5, 0, 0.5);
                            nodes.add(location);
                        });

                    }).orElse(none -> {
                        // Handle no path found scenario
                        System.out.println("No path found between start and target positions.");

                    }).exceptionally(ex -> System.err.println("An exception occurred -> " + ex));
            array = nodes.toArray(new Location[0]);
        } catch (Exception e) {
            Bukkit.getLogger().severe("An exception occurred in ExprPathFind: get(Event event)" + e.getMessage());
            array = new Location[0];
        } finally {
            return array;
        }
    }
    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class  getReturnType() {
        return Location.class;
    }

    @Override
    public String toString(Event e, boolean b) {
        return "path from " + this.loc1.toString(e,b) + " to " + this.loc2.toString(e,b);
    }
}