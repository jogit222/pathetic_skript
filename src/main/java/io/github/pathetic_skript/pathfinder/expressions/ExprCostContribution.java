package io.github.pathetic_skript.pathfinder.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExprCostContribution extends SimpleExpression<Double> {
    public static void register(Registration registration) {
        registration.newSimpleExpression(ExprCostContribution.class, Double.class,
                        "[the ][pathfinding ]cost [contribution ]of %block%")
                .name("Pathfinder - Cost Contribution")
                .description("Get/set the cost contribution of a specified block type")
                .examples("""
                        set the pathfinding cost contribution of block below player to 10
                        """)
                .since("1.2.2")
                .register();
    }

    private Expression<Block> block;
    public static Map<Block, Double> cost = new HashMap<>();
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.block = (Expression<Block>) expressions[0];
        return true;
    }
    @Override
    protected @Nullable Double[] get(Event event) {
        return List.of(cost.getOrDefault(this.block, 0.0)).toArray(new Double[0]);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<Double> getReturnType() {
        return Double.class;
    }

    public Class<?> [] acceptChange(Changer.ChangeMode mode)  {
        return switch (mode) {
            case SET, ADD -> new Class[]{Double.class};
            case REMOVE -> new Class[]{Block.class};
            default -> null;
        };
    }

    @Override
    public void change(Event event, Object [] delta, Changer.ChangeMode mode)  {
        if (delta == null) return;
        switch (mode) {
            case ADD -> {
                Double element = cost.getOrDefault(this.block, null);
                cost.remove(this.block);
                if (element.isNaN()) { return; }
                cost.put((Block) this.block, Double.valueOf(Arrays.toString(delta)));
            }
            case SET -> {
                cost.remove(this.block);
                cost.put((Block) this.block, (Double) delta[0]);
            }
            case REMOVE -> {
                cost.remove((Block) delta[0]);
            }

        }
    }

    @Override
    public String toString(@org.jetbrains.annotations.Nullable Event event, boolean debug) {
        return "Cost contribution of " + this.block;
    }
}
