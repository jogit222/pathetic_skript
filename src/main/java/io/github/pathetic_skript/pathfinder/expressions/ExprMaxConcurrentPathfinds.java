package io.github.pathetic_skript.pathfinder.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprMaxConcurrentPathfinds extends SimpleExpression<Integer> {
    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprMaxConcurrentPathfinds.class, Integer.class,
                        "[the ][max ]concurrent path[find[s]]")
                .name("Pathfinder - Set Max Concurrent Pathfinds")
                .description("Set the max concurrent pathfinds. Keep value between 1 and 64. Defaults to 4.")
                .examples("set the max concurrent pathfinds to 8")
                .since("1.2.0")
                .register();
    }

    public static Integer maxConcurrentPathfinds = 4;

    @Override
    @Nullable
    protected Integer[] get(Event event) {
        return new Integer[]{maxConcurrentPathfinds};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class getReturnType() {
        return Integer.class;
    }

    @Override
    public String toString(org.bukkit.event.@org.jetbrains.annotations.Nullable Event event, boolean debug) {
        return "Max Concurrent Pathfinds = " + maxConcurrentPathfinds;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        maxConcurrentPathfinds = 4;
        return true;
    }

    @Override
    public Class<?> [] acceptChange(Changer.ChangeMode mode)  {
        return switch (mode) {
            case SET -> new Class[]{Integer[].class};
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object [] delta, Changer.ChangeMode mode)  {
        if (delta == null) return;
        try {
            if (mode == Changer.ChangeMode.SET) {
                int value = (int) delta[0];
                if (value < 1) value = 1;
                if (value > 64) value = 64;
                maxConcurrentPathfinds = value;
            }
        } catch(Error e) {
            Bukkit.getLogger().warning("You can not add a non integer to an integer! " + e.getMessage());
        }
    }
}
