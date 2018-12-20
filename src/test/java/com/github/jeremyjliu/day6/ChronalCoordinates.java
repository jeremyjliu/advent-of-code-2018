/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day6;

import com.github.jeremyjliu.AbstractAdventSolution;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// improvement: sort the list of coordinates or use a quad-tree / kd-tree to speed up nearest neighbor(s) search
public final class ChronalCoordinates extends AbstractAdventSolution {
    private static final Logger LOG = LoggerFactory.getLogger(ChronalCoordinates.class);
    private static final int GRID_SIZE = 400;
    private static final int SAFE_REGION_MAX_DISTANCE = 10000;
    private List<Coordinate> coordinates;

    public ChronalCoordinates() {
        super("/day6.txt");
        coordinates = ImmutableList.copyOf(parseCoordinates(inputLines));
    }

    private static List<Coordinate> parseCoordinates(List<String> inputLines) {
        return inputLines.stream()
                .map(line -> {
                    String[] parts = line.split(", ");
                    Preconditions.checkArgument(parts.length == 2);
                    return ImmutableCoordinate.builder()
                            .label(UUID.randomUUID().toString())
                            .x(Integer.parseInt(parts[0]))
                            .y(Integer.parseInt(parts[1]))
                            .build();
                }).collect(Collectors.toList());
    }

    @Test
    public void partOne() {
        Map<Coordinate, Integer> regionAreaByCoordinate = new HashMap<>();
        Set<Coordinate> infiniteRegions = new HashSet<>();
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                List<Coordinate> closestCoordinattes = findClosestCoordinate(x, y);
                if (closestCoordinattes.size() == 1) {
                    Coordinate coordinate = closestCoordinattes.get(0);
                    regionAreaByCoordinate.compute(coordinate, (k, v) -> v == null ? 1 : v + 1);
                    if (x == 0 || x == GRID_SIZE - 1 || y == 0 || y == GRID_SIZE - 1) {
                        infiniteRegions.add(coordinate);
                    }
                }
            }
        }
        int maxFiniteRegionArea = findMaximumFiniteArea(regionAreaByCoordinate, infiniteRegions);
        LOG.info("Result: {}", maxFiniteRegionArea);
    }

    // gets closest coordinate(s) by manhattan distance
    private List<Coordinate> findClosestCoordinate(int xIndex, int yIndex) {
        int minDistance = Integer.MAX_VALUE;
        List<Coordinate> minCoordinates = new ArrayList<>();
        for (Coordinate coordinate : coordinates) {
            int distance = Math.abs(xIndex - coordinate.getX()) + Math.abs(yIndex - coordinate.getY());
            if (distance == minDistance) {
                minCoordinates.add(coordinate);
            } else if (distance < minDistance) {
                minDistance = distance;
                minCoordinates.clear();
                minCoordinates.add(coordinate);
            }
        }
        return minCoordinates;
    }

    private int findMaximumFiniteArea(
            Map<Coordinate, Integer> regionAreaByCoordinate,
            Set<Coordinate> infiniteRegions) {
        int maxFiniteRegion = Integer.MIN_VALUE;
        for (Map.Entry<Coordinate, Integer> regionEntry : regionAreaByCoordinate.entrySet()) {
            if (regionEntry.getValue() > maxFiniteRegion && !infiniteRegions.contains(regionEntry.getKey())) {
                maxFiniteRegion = regionEntry.getValue();
            }
        }
        return maxFiniteRegion;
    }

    @Test
    public void partTwo() {
        int safeRegionArea = 0;
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                int totalDistance = getTotalDistanceToAllCoordinates(x, y);
                if (totalDistance < SAFE_REGION_MAX_DISTANCE) {
                    safeRegionArea++;
                }
            }
        }
        LOG.info("Result: {}", safeRegionArea);
    }

    private int getTotalDistanceToAllCoordinates(int xIndex, int yIndex) {
        int totalDistance = 0;
        List<Coordinate> minCoordinates = new ArrayList<>();
        for (Coordinate coordinate : coordinates) {
            int distance = Math.abs(xIndex - coordinate.getX()) + Math.abs(yIndex - coordinate.getY());
            totalDistance += distance;
        }
        return totalDistance;
    }
}
