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
import io.github.pathetic_skript.pathfinder.expressions.ExprNeighborStrategies;
import io.github.pathetic_skript.pathfinder.expressions.ExprPathCacheSize;
import io.github.pathetic_skript.pathfinder.expressions.ExprMaxConcurrentPathfinds;
import io.github.pathetic_skript.pathfinder.util.CustomNeighborStrategies.CustomNeighborStrategies;
import io.github.pathetic_skript.pathfinder.util.validationProcessor.CustomValidationProcessor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;

import java.util.Collections;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Semaphore;

public class EffPathfindStart extends AsyncEffect {

    public static Map<String, Location[]> pathCache = Collections.synchronizedMap(new LinkedHashMap<>());
    private static Semaphore pathfindSemaphore;

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
    PathfinderConfiguration config;

    private PathfinderFactory pathfinder = new AStarPathfinderFactory();
    private static int maxPermits = 4;

    public static int asyncPathfinds = 0;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.loc1 = (Expression<Location>) exprs[0];
        this.loc2 = (Expression<Location>) exprs[1];
        this.id = (Expression<String>) exprs[2];

        int newMax = ExprMaxConcurrentPathfinds.maxConcurrentPathfinds;
        if (newMax != maxPermits) {
            int acquired = (pathfindSemaphore != null) ? (maxPermits - pathfindSemaphore.availablePermits()) : 0;
            pathfindSemaphore = new Semaphore(newMax - acquired);
            maxPermits = newMax;
        } else if (pathfindSemaphore == null) {
            pathfindSemaphore = new Semaphore(newMax);
            maxPermits = newMax;
        }
        return true;
    }

    @Override
    protected void execute(Event event) {
        boolean semap = true;
        try {
            pathfindSemaphore.acquire();
        } catch (InterruptedException e) {
            Bukkit.getLogger().severe("Unable to acquire Semaphore! Make an issue on the github with following stacktrace" + e.getMessage());
            e.printStackTrace();
            semap = false;
        }
        asyncPathfinds++;
        Location startBukkit  = loc1.getSingle(event);
        Location targetBukkit = loc2.getSingle(event);
        World world           = loc1.getSingle(event).getWorld();
        String cacheKey       = id.getSingle(event);

        PathPosition startPos  = BukkitMapper.toPathPosition(startBukkit);
        PathPosition targetPos = BukkitMapper.toPathPosition(targetBukkit);

        if (ExprAllowedBlocks.allowedBlocks.isEmpty()) {
            config = PathfinderConfiguration.builder()
                    .provider(new LoadingNavigationPointProvider())
                    .async(true)
                    .maxIterations(100_000_000)
                    .neighborStrategy((ExprNeighborStrategies.neighborMoves.isEmpty()) ? NeighborStrategies.DIAGONAL_3D : new CustomNeighborStrategies())
                    .build();

        } else {
            config = PathfinderConfiguration.builder()
                    .provider(new LoadingNavigationPointProvider())
                    .async(true)
                    .maxIterations(100_000_000)
                    .neighborStrategy((ExprNeighborStrategies.neighborMoves.isEmpty()) ? NeighborStrategies.DIAGONAL_3D : new CustomNeighborStrategies())
                    .validationProcessors(List.of(new CustomValidationProcessor()))
                    .build();
        }
        try {
            factory.createPathfinder(config)
                    .findPath(startPos, targetPos, new BukkitEnvironmentContext(world))
                    .ifPresent(result -> {
                        List<Location> nodes = new ArrayList<>();
                        result.getPath().forEach(pos -> nodes.add(BukkitMapper.toLocation(pos, world).add(new Vector(0.5, 0, 0.5))));
                        pathCache.put(cacheKey, nodes.toArray(new Location[0]));
                    })
                    .exceptionally(ex -> System.err.println("Pathfinding failed: " + ex.getMessage()));
        } catch (Exception ex) {
            System.err.println("Pathfinding interrupted: " + ex.getMessage());
        }
        if (semap) { pathfindSemaphore.release(); }
        if (pathCache.size() > ExprPathCacheSize.maxPathCacheSize) {
            pathCache.remove(pathCache.entrySet().iterator().next().getKey());
        }
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "start calculating path from " + loc1.toString(e, debug) + " to " + loc2.toString(e, debug);
    }
}