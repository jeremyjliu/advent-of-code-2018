/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day1;

import com.github.jeremyjliu.AbstractAdventSolution;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChronalCalibration extends AbstractAdventSolution {
    private static final Logger LOG = LoggerFactory.getLogger(ChronalCalibration.class);

    public ChronalCalibration() {
        super("/day1.txt");
    }

    @Test
    public void partOne() {
        String answer = inputLines.stream()
                .map(Integer::parseInt)
                .reduce(0, Integer::sum)
                .toString();
        LOG.info("Result: {}", answer);
    }

    @Test
    public void partTwo() {
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
        LOG.info("Result: {}", frequency.toString());
    }
}
