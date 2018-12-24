/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day14;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChocolateCharts {
    private static final Logger LOG = LoggerFactory.getLogger(ChocolateCharts.class);
    private int firstElfRecipeNum;
    private int secondElfRecipeNum;
    // part one
    private List<Integer> currentRecipes;

    // part two
    private int currentIndex;
    private Map<Integer, Integer> currentRecipesMap = new HashMap<>();
    private List<String> lastTwoRecipes = new ArrayList<>();

    @Test
    public void partOne() {
        int recipesToGenerate = 47801;
        String seedInput = "37";
        List<Integer> seedRecipes = Arrays.stream(seedInput.split(""))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        firstElfRecipeNum = 0;
        secondElfRecipeNum = 1;

        currentRecipes = new ArrayList<>(seedRecipes);
        while (currentRecipes.size() <= recipesToGenerate + 10) {
            runRecipeIteration();
        }
        List<Integer> result = currentRecipes.subList(recipesToGenerate, recipesToGenerate + 10);
        LOG.info("Result: {}", result.stream().map(String::valueOf).collect(Collectors.joining()));
    }

    private static Stack<Integer> getDigits(int sum) {
        Stack<Integer> stack = new Stack<>();
        if (sum == 0) {
            stack.push(0);
            return stack;
        }
        int number = sum;
        while (number > 0) {
            stack.push(number % 10);
            number = number / 10;
        }
        return stack;
    }

    private void runRecipeIteration() {
        LOG.debug("{} first {},{} second {},{} recipes {}",
                currentRecipes.size(),
                firstElfRecipeNum, currentRecipes.get(firstElfRecipeNum),
                secondElfRecipeNum, currentRecipes.get(secondElfRecipeNum),
                currentRecipes.stream().map(String::valueOf).collect(Collectors.joining()));
        // generate new recipes
        int recipeSum = currentRecipes.get(firstElfRecipeNum) + currentRecipes.get(secondElfRecipeNum);
        Stack<Integer> newRecipes = getDigits(recipeSum);
        while (!newRecipes.empty()) {
            currentRecipes.add(newRecipes.pop());
        }

        // choose new recipes
        firstElfRecipeNum = getNextRecipeNumber(
                firstElfRecipeNum, currentRecipes.get(firstElfRecipeNum), currentRecipes.size());
        secondElfRecipeNum = getNextRecipeNumber(
                secondElfRecipeNum, currentRecipes.get(secondElfRecipeNum), currentRecipes.size());
    }

    private static int getNextRecipeNumber(int currentRecipeNumber, int currentRecipeValue, int size) {
        return Math.floorMod(currentRecipeNumber + 1 + currentRecipeValue, size);
    }

    @Test
    // redo with map of index -> value, keeping only the last 2 n-length sequences for comparison purposes
    public void partTwo() {
        String matchSequence = "047801";
        firstElfRecipeNum = 0;
        secondElfRecipeNum = 1;
        currentRecipesMap.put(0, 3);
        currentRecipesMap.put(1, 7);
        lastTwoRecipes.add("3");
        lastTwoRecipes.add("37");

        int firstAppearanceIndex = -1;
        currentIndex = 1;
        while (firstAppearanceIndex < 0) {
            runRecipeIterationFaster(matchSequence.length());
            Preconditions.checkState(lastTwoRecipes.size() == 2);
            if (lastTwoRecipes.get(0).equals(matchSequence)) {
                firstAppearanceIndex = currentIndex - matchSequence.length();
            } else if (lastTwoRecipes.get(1).equals(matchSequence)) {
                firstAppearanceIndex = currentIndex - matchSequence.length() + 1;
            }
            LOG.debug("{} first {},{} second {},{} last recipes {}",
                    currentRecipesMap.size(),
                    firstElfRecipeNum, currentRecipesMap.get(firstElfRecipeNum),
                    secondElfRecipeNum, currentRecipesMap.get(secondElfRecipeNum),
                    lastTwoRecipes);
        }
        LOG.info("Result: {}", firstAppearanceIndex);
    }

    private void runRecipeIterationFaster(int sequenceSize) {
        // generate new recipes
        int recipeSum = currentRecipesMap.get(firstElfRecipeNum) + currentRecipesMap.get(secondElfRecipeNum);
        Stack<Integer> newRecipes = getDigits(recipeSum);
        while (!newRecipes.empty()) {
            int newRecipe = newRecipes.pop();
            currentIndex++;
            currentRecipesMap.put(currentIndex, newRecipe);
            lastTwoRecipes.remove(0);
            String newRecipeSequence = lastTwoRecipes.get(0) + String.valueOf(newRecipe);
            lastTwoRecipes.add(newRecipeSequence.substring(Math.max(0, newRecipeSequence.length() - sequenceSize)));
        }

        // choose new recipes
        firstElfRecipeNum = getNextRecipeNumber(
                firstElfRecipeNum, currentRecipesMap.get(firstElfRecipeNum), currentRecipesMap.size());
        secondElfRecipeNum = getNextRecipeNumber(
                secondElfRecipeNum, currentRecipesMap.get(secondElfRecipeNum), currentRecipesMap.size());
    }
}
