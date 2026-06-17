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

public class ExprPathCacheSize extends SimpleExpression<Integer> {
    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprPathCacheSize.class, Integer.class,
                        "[the ][max ]path[ ]cache size")
                .name("Pathfinder - Set Max PathCache Size")
                .description("Set the max path cache size. Keep value below 1000 unless you know what you are doing. Defaults to 500.")
                .examples("set the max path cache size to 500")
                .since("1.1.1")
                .register();
    }

    public static Integer maxPathCacheSize;

    @Override
    @Nullable
    protected Integer[] get(Event event) {
        return new Integer[]{maxPathCacheSize};
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
        return "Max Path Cache Size = " + maxPathCacheSize;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        maxPathCacheSize = 500;
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
        assert delta != null;
        try {
            if (mode == Changer.ChangeMode.SET) {maxPathCacheSize = (int) delta[0];}
        } catch(Error e) {
            Bukkit.getLogger().warning("You can not add a non integer to an integer! " + e.getMessage());
        }

    }
}
