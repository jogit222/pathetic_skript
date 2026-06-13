package io.github.pathetic_skript.pathfinder.expressions;

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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import de.bsommerfeld.pathetic.api.factory.PathfinderFactory;
import de.bsommerfeld.pathetic.api.pathing.Pathfinder;
import de.bsommerfeld.pathetic.api.pathing.configuration.PathfinderConfiguration;
import de.bsommerfeld.pathetic.bukkit.provider.LoadingNavigationPointProvider;
import de.bsommerfeld.pathetic.engine.factory.AStarPathfinderFactory;

import javax.annotation.Nullable;

// doc stuff
@Name("Pathfinder - Get Path")
@Description("""
        Get a path from a location to a location. Blocks the main server thread.
        """)
@Example("""
        set {_nodes::*} to path from location(0, 0, 0) to location(5, 5, 5)
        """)
@Since("1.0.0")
@Keywords({"Pathfinding", "Pathfinder", "A*"})

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

    public boolean init(Expression<?>[] exprs, int machedPattern, Kleenean isDelayed, ParseResult parseResult)  {
        this.loc1 = (Expression<Location>) exprs[0];
        this.loc2 = (Expression<Location>) exprs[1];
        return true;
    }
    protected Location[] get(Event event) {
        // Create the PathfinderFactory
        PathfinderFactory factory = new AStarPathfinderFactory();

        // Configure the pathfinder
        PathfinderConfiguration configuration = PathfinderConfiguration.builder()
                .provider(new LoadingNavigationPointProvider())
                .async(false)
                .maxIterations(100_000_000)
                .build();

        // Create the pathfinder instance
        Pathfinder pathfinder = factory.createPathfinder(configuration);
        List<Location> nodes = new ArrayList<>();
        Pathfinder pf = pathfinder;
        Location startPosBukkit = this.loc1.getSingle(event);
        World world = startPosBukkit.getWorld();
        Location targetPosBukkit = this.loc2.getSingle(event);
        PathPosition startPos = new PathPosition(startPosBukkit.getBlockX(), startPosBukkit.getBlockY(), startPosBukkit.getBlockZ());
        PathPosition targetPos = new PathPosition(targetPosBukkit.getBlockX(), targetPosBukkit.getBlockY(), targetPosBukkit.getBlockZ());
        pf.findPath(startPos, targetPos, new BukkitEnvironmentContext(world))
                .ifPresent(result -> {

                    // We have an usable result since it either found the path, or fallen back.
                    result.getPath().forEach(position -> {
                        Location location = BukkitMapper.toLocation(position, world);
                        // Do something with it.
                        nodes.add(location);
                    });

                }).orElse(none -> {
                    // Handle no path found scenario
                    System.out.println("No path found between start and target positions.");

                }).exceptionally(ex -> System.err.println("An exception occurred -> " + ex));

        Location[] array = nodes.toArray(new Location[0]);
        return array;
    }
    @Override
    public boolean isSingle() {
        return false;
    }
    public Class  getReturnType() {
        return Location.class;
    }
    public String toString(Event e, boolean b) {
        return get(e).toString();
    }
}