package io.github.pathetic_skript.pathfinder.expressions;

import io.github.pathetic_skript.pathfinder.asyncEffects.EffPathfindStart;

import org.bukkit.Location;
import org.bukkit.event.Event;

import com.github.shanebeee.skr.Registration;

import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.util.Kleenean;
import ch.njol.skript.lang.SkriptParser.ParseResult;

import javax.annotation.Nullable;

public class ExprAsyncPathfind extends SimpleExpression<Location> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprAsyncPathfind.class, Location.class,
                        "calculated path %string%")
                .name("Pathfinder - Get Path Async")
                .description("Get a path from a location to a location async. Does NOT block the main server thread. In most cases use this over the non async vesion.")
                .examples("set {_nodes::*} to path from location(0, 0, 0) to location(5, 5, 5)")
                .since("1.0.0")
                .register();
    }

    private Expression<String> id;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        this.id = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected Location[] get(Event event) {
        String cacheKey = id.getSingle(event);
        return EffPathfindStart.pathCache.getOrDefault(cacheKey, new Location[0]);
    }

    @Override
    public boolean isSingle() { return false; }

    @Override
    public Class<Location> getReturnType() { return Location.class; }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "calculated path " + id.toString(e, debug);
    }
}
