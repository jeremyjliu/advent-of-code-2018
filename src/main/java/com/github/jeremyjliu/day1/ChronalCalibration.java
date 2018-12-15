/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day1;

import com.github.jeremyjliu.AbstractAdventSolution;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;

public final class ChronalCalibration extends AbstractAdventSolution {

    public ChronalCalibration() {
        super(LoggerFactory.getLogger(ChronalCalibration.class), "/day1.txt");
    }

    @Override
    public String partOne() {
        return inputLines.stream()
                .map(Integer::parseInt)
                .reduce(0, Integer::sum)
                .toString();
    }

    @Override
    public String partTwo() {
        inputLines.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        boolean foundRepeatedFrequency = false;
        Integer frequency = 0;
        Set<Integer> seenFrequencies = new HashSet<>(frequency);
        while (!foundRepeatedFrequency) {
            for (String input : inputLines) {
                frequency += Integer.parseInt(input);
                if (seenFrequencies.contains(frequency)) {
                    foundRepeatedFrequency = true;
                    break;
                }
                seenFrequencies.add(frequency);
            }
        }
        return frequency.toString();
    }
}
