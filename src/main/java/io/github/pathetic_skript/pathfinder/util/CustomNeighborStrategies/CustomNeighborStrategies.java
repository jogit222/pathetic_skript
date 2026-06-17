package io.github.pathetic_skript.pathfinder.util.CustomNeighborStrategies;

import de.bsommerfeld.pathetic.api.pathing.INeighborStrategy;
import de.bsommerfeld.pathetic.api.wrapper.PathVector;
import de.bsommerfeld.pathetic.bukkit.mapper.BukkitMapper;
import io.github.pathetic_skript.pathfinder.expressions.ExprNeighborStrategies;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;

public class CustomNeighborStrategies implements INeighborStrategy {

    public static <T> Iterable<T>
    getIterableFromIterator(Iterator<T> iterator)
    {
        return () -> iterator;
    }

    @Override
    public Iterable<PathVector> getOffsets() {
        ArrayList<PathVector> pathVectors = new ArrayList<>();
        for (Vector v : ExprNeighborStrategies.neighborMoves) {
            pathVectors.add(BukkitMapper.toPathVector(v));
        }
        return getIterableFromIterator(pathVectors.iterator());
    }
}
