package io.github.pathetic_skript.pathfinder.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;

import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ExprNeighborStrategies extends SimpleExpression<Vector> {
    public static void register(Registration registration) {
        registration.newSimpleExpression(ExprNeighborStrategies.class, Vector.class, "[all ][of ][the ] allowed moves")
                .name("Pathfinder - Set Allowed Moves")
                .description("All the moves the pathfinder can take as vectors.")
                .examples("""
                        loop all integers between -1 and 1:
                            loop all integers between -1 and 1:
                                add vector(loop-value-1, 0, loop-value-2) to all of the allowed moves""")
                .register();
    }

    public static ArrayList<Vector> neighborMoves = new ArrayList<>();

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    public Vector[] get(Event event) {
        return neighborMoves.toArray(new Vector[0]);
    }

    @Override
    @Nullable
    public Class<?> [] acceptChange(Changer.ChangeMode mode)  {
        return switch (mode) {
            case ADD, REMOVE -> new Class[]{Vector[].class};
            default -> null;
        };
    }

    @Override
    public void change(org.bukkit.event.Event event, Object [] delta, Changer.ChangeMode mode)  {
        if (delta == null) return;
        for (Object obj : delta) {
            switch (mode) {
                case ADD -> neighborMoves.add((Vector) obj);
                case REMOVE -> neighborMoves.remove((Vector) obj);
            }
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class getReturnType() {
        return Vector.class;
    }

    @Override
    public String toString(org.bukkit.event.@org.jetbrains.annotations.Nullable Event event, boolean debug) {
        return "allowed moves " + neighborMoves;
    }
}
