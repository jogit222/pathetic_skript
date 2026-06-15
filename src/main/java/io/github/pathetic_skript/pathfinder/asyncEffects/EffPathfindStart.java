package io.github.pathetic_skript.pathfinder.asyncEffects;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import ch.njol.skript.doc.*;

import com.github.shanebeee.skr.Registration;

import de.bsommerfeld.pathetic.api.factory.PathfinderFactory;
import de.bsommerfeld.pathetic.api.pathing.NeighborStrategies;
import de.bsommerfeld.pathetic.api.pathing.configuration.PathfinderConfiguration;
import de.bsommerfeld.pathetic.api.wrapper.PathPosition;
import de.bsommerfeld.pathetic.bukkit.context.BukkitEnvironmentContext;
import de.bsommerfeld.pathetic.bukkit.mapper.BukkitMapper;
import de.bsommerfeld.pathetic.bukkit.provider.LoadingNavigationPointProvider;
import de.bsommerfeld.pathetic.engine.factory.AStarPathfinderFactory;

import io.github.pathetic_skript.pathfinder.expressions.ExprAllowedBlocks;
import io.github.pathetic_skript.pathfinder.util.validationProcessor.CustomValidationProcessor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffPathfindStart extends AsyncEffect {

    public static Map<String, Location[]> pathCache = new HashMap<>();

    public static void register(Registration reg) {
        reg.newEffect(EffPathfindStart.class,
                        "start [calculating] path[finding] from %location% to %location% with id %string%")
                .name("Pathfinder - Start Async Pathfinder")
                .description("Start the async pathfinder, use calculated path %string% to retrieve values of this operation. Does NOT block the main server thread")
                .examples("start pathfinding from location(0, 0, 0) to location(5, 5, 5) with id \"example\"",
                        "set {_nodes::*} to calculated path \"example\"")
                .since("1.0.0")
                .register();
    }

    private Expression<Location> loc1, loc2;
    private Expression<String> id;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.loc1 = (Expression<Location>) exprs[0];
        this.loc2 = (Expression<Location>) exprs[1];
        this.id   = (Expression<String>)   exprs[2];
        return true;
    }

    @Override
    protected void execute(Event event) {
        Location startBukkit  = loc1.getSingle(event);
        Location targetBukkit = loc2.getSingle(event);
        World world           = loc1.getSingle(event).getWorld();
        String cacheKey       = id.getSingle(event);

        PathPosition startPos  = new PathPosition(startBukkit.getBlockX(), startBukkit.getBlockY(), startBukkit.getBlockZ());
        PathPosition targetPos = new PathPosition(targetBukkit.getBlockX(), targetBukkit.getBlockY(), targetBukkit.getBlockZ());

        PathfinderFactory factory = new AStarPathfinderFactory();
        PathfinderConfiguration config;
        if (ExprAllowedBlocks.allowedBlocks.size() == 0) {
            config = PathfinderConfiguration.builder()
                    .provider(new LoadingNavigationPointProvider())
                    .async(true)
                    .maxIterations(100_000_000)
                    .neighborStrategy(NeighborStrategies.DIAGONAL_3D)
                    .build();

        } else {
            config = PathfinderConfiguration.builder()
                    .provider(new LoadingNavigationPointProvider())
                    .async(true)
                    .maxIterations(100_000_000)
                    .neighborStrategy(NeighborStrategies.DIAGONAL_3D)
                    .validationProcessors((ExprAllowedBlocks.allowedBlocks.size() > 0) ? List.of(new CustomValidationProcessor()) : null)
                    .build();
        }
        try {
            factory.createPathfinder(config)
                    .findPath(startPos, targetPos, new BukkitEnvironmentContext(world))
                    .ifPresent(result -> {
                        List<Location> nodes = new ArrayList<>();
                        result.getPath().forEach(pos -> nodes.add(BukkitMapper.toLocation(pos, world)));
                        pathCache.put(cacheKey, nodes.toArray(new Location[0]));
                    })
                    .exceptionally(ex -> {
                        System.err.println("Pathfinding failed: " + ex.getMessage());

                    });
        } catch (Exception ex) {
            System.err.println("Pathfinding interrupted: " + ex.getMessage());
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "start calculating path from " + loc1.toString(e, debug) + " to " + loc2.toString(e, debug);
    }
}