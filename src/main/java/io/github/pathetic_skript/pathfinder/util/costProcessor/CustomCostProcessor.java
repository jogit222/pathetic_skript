package io.github.pathetic_skript.pathfinder.util.costProcessor;

import de.bsommerfeld.pathetic.api.pathing.context.EnvironmentContext;
import de.bsommerfeld.pathetic.bukkit.context.BukkitEnvironmentContext;
import de.bsommerfeld.pathetic.bukkit.mapper.BukkitMapper;
import org.bukkit.Location;

import de.bsommerfeld.pathetic.api.pathing.processing.Cost;
import de.bsommerfeld.pathetic.api.pathing.processing.CostProcessor;
import de.bsommerfeld.pathetic.api.pathing.processing.context.EvaluationContext;
import io.github.pathetic_skript.pathfinder.expressions.ExprCostContribution;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class CustomCostProcessor implements CostProcessor {
    @Override
    public Cost calculateCostContribution(EvaluationContext evaluationContext) {
        EnvironmentContext ec = evaluationContext.getEnvironmentContext();
        World world = ((BukkitEnvironmentContext) ec).getWorld();
        Location location = BukkitMapper.toLocation(evaluationContext.getCurrentPathPosition(), world).add(new Vector(0, -1, 0));
        return Cost.of(ExprCostContribution.cost.get(location.getBlock()));
    }
}
