/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day11;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChronalCharge {
    private static final Logger LOG = LoggerFactory.getLogger(ChronalCharge.class);
    private static final int GRID_SIZE = 300;

    @Test
    public void partOne() {
        int gridSerialNumber = 3463;
        int windowSize = 3;
        int[][] fuelCellGrid = generateFuelCellGrid(gridSerialNumber);
        int[][] summedFuelCellGrid = generateSummedFuelCellGrid(fuelCellGrid);
        MaxWindowSumResult result = computeMaxSquareWindowSum(summedFuelCellGrid, windowSize);
        LOG.info("Result: {},{}", result.getX() + 1, result.getY() + 1);
    }

    private static int[][] generateFuelCellGrid(int gridSerialNumber) {
        int[][] fuelCellGrid = new int[GRID_SIZE][];
        for (int i = 0; i < GRID_SIZE; i++) {
            fuelCellGrid[i] = new int[GRID_SIZE];
        }

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                int rackId = x + 1 + 10;
                int powerLevel = rackId * (y + 1);
                powerLevel += gridSerialNumber;
                powerLevel *= rackId;
                powerLevel = (powerLevel / 100) % 10;
                powerLevel -= 5;
                fuelCellGrid[x][y] = powerLevel;
            }
        }
        return fuelCellGrid;
    }

    private static int[][] generateSummedFuelCellGrid(int[][] grid) {
        int[][] fuelCellGrid = new int[GRID_SIZE][];
        for (int i = 0; i < GRID_SIZE; i++) {
            fuelCellGrid[i] = new int[GRID_SIZE];
        }

        // first row
        fuelCellGrid[0][0] = grid[0][0];
        for (int x = 1; x < GRID_SIZE; x++) {
            fuelCellGrid[x][0] = fuelCellGrid[x - 1][0] + grid[x][0];
        }

        // first col
        for (int y = 1; y < GRID_SIZE; y++) {
            fuelCellGrid[0][y] = fuelCellGrid[0][y - 1] + grid[0][y];
        }

        for (int x = 1; x < GRID_SIZE; x++) {
            for (int y = 1; y < GRID_SIZE; y++) {
                fuelCellGrid[x][y] =
                        fuelCellGrid[x - 1][y] + fuelCellGrid[x][y - 1] + grid[x][y] - fuelCellGrid[x - 1][y - 1];
            }
        }
        return fuelCellGrid;
    }

    @Test
    public void partTwo() {
        int gridSerialNumber = 3463;
        int[][] fuelCellGrid = generateFuelCellGrid(gridSerialNumber);
        int[][] summedFuelCellGrid = generateSummedFuelCellGrid(fuelCellGrid);
        MaxWindowSumResult maxResult = ImmutableMaxWindowSumResult.builder()
                .x(0)
                .y(0)
                .sum(summedFuelCellGrid[0][0])
                .windowSize(1)
                .build();
        for (int windowSize = 1; windowSize <= GRID_SIZE; windowSize++) {
            MaxWindowSumResult result = computeMaxSquareWindowSum(summedFuelCellGrid, windowSize);
            if (result.getSum() >  maxResult.getSum()) {
                maxResult = result;
            }
        }
        LOG.info("Result: {},{},{}", maxResult.getX() + 1, maxResult.getY() + 1, maxResult.getWindowSize());
    }

    private static MaxWindowSumResult computeMaxSquareWindowSum(int[][] summedGrid, int windowSize) {
        int maxX = 0;
        int maxY = 0;
        int maxSum = summedGrid[windowSize - 1][windowSize - 1];

        for (int x = 1; x < GRID_SIZE - windowSize + 1; x++) {
            for (int y = 1; y < GRID_SIZE - windowSize + 1; y++) {
                int sum = computeSquareWindowSum(summedGrid, x, y, windowSize);
                if (sum > maxSum) {
                    maxSum = sum;
                    maxX = x;
                    maxY = y;
                }
            }
        }
        return ImmutableMaxWindowSumResult.builder()
                .x(maxX)
                .y(maxY)
                .sum(maxSum)
                .windowSize(windowSize)
                .build();
    }

    private static int computeSquareWindowSum(int[][] summedGrid, int startingX, int startingY, int windowSize) {
        LOG.info("{},{},{}", startingX, startingY, windowSize);
        return summedGrid[startingX + windowSize - 1][startingY + windowSize - 1]
                - summedGrid[startingX - 1][startingY + windowSize - 1]
                - summedGrid[startingX + windowSize - 1][startingY - 1]
                + summedGrid[startingX - 1][startingY - 1];
    }
}
