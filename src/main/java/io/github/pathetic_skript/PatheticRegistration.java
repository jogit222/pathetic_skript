package io.github.pathetic_skript;

import com.github.shanebeee.skr.Registration;
import io.github.pathetic_skript.pathfinder.asyncEffects.EffPathfindStart;
import io.github.pathetic_skript.pathfinder.expressions.ExprAllowedBlocks;
import io.github.pathetic_skript.pathfinder.expressions.ExprAsyncPathfind;
import io.github.pathetic_skript.pathfinder.expressions.ExprPathCacheSize;
import io.github.pathetic_skript.pathfinder.expressions.ExprMaxConcurrentPathfinds;
import io.github.pathetic_skript.pathfinder.expressions.ExprPathfind;

public class PatheticRegistration {
    public static void register(Registration registration) {
        EffPathfindStart.register(registration);
        ExprAsyncPathfind.register(registration);
        ExprPathfind.register(registration);
        ExprAllowedBlocks.register(registration);
        ExprPathCacheSize.register(registration);
        ExprMaxConcurrentPathfinds.register(registration);
        registration.finalizeRegistration();
    }
}
