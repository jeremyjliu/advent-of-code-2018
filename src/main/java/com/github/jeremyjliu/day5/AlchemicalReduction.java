/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day5;

import com.github.jeremyjliu.AbstractAdventSolution;
import org.slf4j.LoggerFactory;

public final class AlchemicalReduction extends AbstractAdventSolution {

    public AlchemicalReduction() {
        super(LoggerFactory.getLogger(AlchemicalReduction.class), "/day5.txt");
    }

    @Override
    public String partOne() {
        String polymer = inputLines.stream().findFirst().orElseThrow(() -> new RuntimeException("No polymer"));
        return Integer.toString(reducePolymer(polymer).length());
    }

    // condenses polymer by matching consecutive letters until it cannot be reduced any longer
    private String reducePolymer(String polymer) {
        String oldPolymer = polymer;
        String newPolymer = reducePolymerOnce(oldPolymer);
        while (oldPolymer.length() != newPolymer.length()) {
            oldPolymer = newPolymer;
            newPolymer = reducePolymerOnce(oldPolymer);
        }
        return newPolymer;
    }

    private String reducePolymerOnce(String polymer) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        boolean lastMatch = false;
        while (index < polymer.length() - 1) {
            if (Character.toLowerCase(polymer.charAt(index)) == Character.toLowerCase(polymer.charAt(index + 1))
                    && polymer.charAt(index) != polymer.charAt(index + 1)) {
                if (index == polymer.length() - 2) {
                    lastMatch = true;
                }
                index++;
            } else {
                builder.append(polymer.charAt(index));
            }
            index++;
        }

        if (!lastMatch) {
            builder.append(polymer.charAt(polymer.length() - 1));
        }
        return builder.toString();
    }

    @Override
    public String partTwo() {
        String polymer = inputLines.stream().findFirst().orElseThrow(() -> new RuntimeException("No polymer"));
        int minPolymerLength = Integer.MAX_VALUE;
        for (int i = 0; i < 26; i++) {
            char unit = (char) ('a' + i);
            String strippedPolymer = removeUnit(polymer, unit);
            String reducedPolymer = reducePolymer(strippedPolymer);
            minPolymerLength = Math.min(minPolymerLength, reducedPolymer.length());
        }
        return Integer.toString(minPolymerLength);
    }

    // assumes unit is lowercase
    private String removeUnit(String polymer, char unit) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < polymer.length(); i++) {
            if (Character.toLowerCase(polymer.charAt(i)) == unit) {
                continue;
            }
            builder.append(polymer.charAt(i));
        }
        return builder.toString();
    }
}
