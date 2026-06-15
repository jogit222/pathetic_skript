package io.github.pathetic_skript.pathfinder.util.costProcessor;

import de.bsommerfeld.pathetic.api.pathing.processing.Cost;
import de.bsommerfeld.pathetic.api.pathing.processing.CostProcessor;
import de.bsommerfeld.pathetic.api.pathing.processing.context.EvaluationContext;
import de.bsommerfeld.pathetic.api.provider.NavigationPointProvider;
import de.bsommerfeld.pathetic.bukkit.provider.BukkitNavigationPoint;
import org.bukkit.Material;

public class CustomCostProcessor implements CostProcessor {
    @Override
    public Cost calculateCostContribution(EvaluationContext context) {
        NavigationPointProvider provider = context.getNavigationPointProvider();

        BukkitNavigationPoint beneath = (BukkitNavigationPoint)
                provider.getNavigationPoint(
                        context.getCurrentPathPosition().subtract(0, 1, 0),
                        context.getEnvironmentContext()
                );

        if (beneath.getMaterial() == Material.STONE) {
            return Cost.of(20);
        }

        return Cost.ZERO;
    }
}