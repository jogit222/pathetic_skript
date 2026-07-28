package io.github.patheticSkript;

import com.github.shanebeee.skr.Registration;
import io.github.patheticSkript.pathfinder.effects.EffPathfindStart;
import io.github.patheticSkript.pathfinder.expressions.*;
public class PatheticRegistration {
    public static void register(Registration registration) {
        EffPathfindStart.register(registration);
        ExprAsyncPathfind.register(registration);
        ExprPathfind.register(registration);
        ExprAllowedBlocks.register(registration);
        ExprPathCacheSize.register(registration);
        ExprMaxConcurrentPathfinds.register(registration);
        ExprNeighborStrategies.register(registration);
        ExprCostContribution.register(registration);
        registration.finalizeRegistration();
    }
}
