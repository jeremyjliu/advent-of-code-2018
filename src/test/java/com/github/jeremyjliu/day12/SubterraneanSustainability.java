/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day12;

import com.github.jeremyjliu.AbstractAdventSolution;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SubterraneanSustainability extends AbstractAdventSolution {
    private static final Logger LOG = LoggerFactory.getLogger(SubterraneanSustainability.class);
    private static final Pattern INITIAL_STATE_PATTERN = Pattern.compile("initial state: ([.#]*)");
    private static final Pattern TRANSITION_PATTERN = Pattern.compile("([.#]{5}) => ([.#])");
    private static final int WINDOW_SIZE = 5;
    private final String parsedInitialState;
    private Map<String, String> transitions = new HashMap<>();

    public SubterraneanSustainability() {
        super("/day12.txt");
        Preconditions.checkArgument(inputLines.size() >= 3);

        // get the initial state
        String initialStateInput = inputLines.get(0);
        Matcher initialStateMatcher = INITIAL_STATE_PATTERN.matcher(initialStateInput);
        Preconditions.checkArgument(initialStateMatcher.matches());
        Preconditions.checkArgument(initialStateMatcher.groupCount() == 1);
        parsedInitialState = initialStateMatcher.group(1);

        // get the transitions
        inputLines.stream()
                .skip(2)
                .forEach(input -> {
                    Matcher matcher = TRANSITION_PATTERN.matcher(input);
                    Preconditions.checkArgument(matcher.matches());
                    Preconditions.checkArgument(matcher.groupCount() == 2);
                    transitions.put(matcher.group(1), matcher.group(2));
                });
    }

    @Test
    public void partOne() {
        int numGenerations = 20;
        int zeroIndex = 0;
        String state = parsedInitialState;
        for (int i = 0; i < numGenerations; i++) {
            StringBuilder builder = new StringBuilder(state);
            if (!state.startsWith(".....")) {
                int numStartingDots = getNumberOfStartingDots(state);
                builder.insert(0, Strings.repeat(".", WINDOW_SIZE - numStartingDots));
                zeroIndex += WINDOW_SIZE - numStartingDots;
            }

            if (!state.endsWith(".....")) {
                int numEndingDots = getNumberOfStartingDots(new StringBuilder(state).reverse().toString());
                builder.append(Strings.repeat(".", WINDOW_SIZE - numEndingDots));
            }
            state = builder.toString();
            LOG.debug("Generation {} Index {}: {}", i, zeroIndex, state);
            state = applyGeneration(state);
            zeroIndex -= 2;
            LOG.debug("Generation {} Index {} Sum {}: {}", i + 1, zeroIndex, getResult(state, zeroIndex), state);
        }

        LOG.debug("Zero Index: {}", zeroIndex);
        LOG.debug("Final State: {}", state);
        LOG.info("Result: {}", getResult(state, zeroIndex));
    }

    private static long getResult(String state, long zeroIndex) {
        long plantsIndexSum = 0;
        char[] stateArray = state.toCharArray();
        for (long i = 0; i < stateArray.length; i++) {
            if (stateArray[(int) i] == '#') {
                plantsIndexSum += i - zeroIndex;
            }
        }
        return plantsIndexSum;
    }

    private int getNumberOfStartingDots(String state) {
        Preconditions.checkArgument(state.length() >= WINDOW_SIZE);
        int numStartingDots = 0;
        char[] stateArray = state.toCharArray();
        for (int i = 0; i < WINDOW_SIZE; i++) {
            if (stateArray[i] == '.') {
                numStartingDots++;
            } else {
                return numStartingDots;
            }
        }
        return numStartingDots;
    }

    // assumes that there is room to grow on either side
    private String applyGeneration(String state) {
        Preconditions.checkArgument(state.startsWith("....."));
        Preconditions.checkArgument(state.endsWith("....."));
        StringBuilder builder = new StringBuilder();

        int halfWindowSize = WINDOW_SIZE / 2;
        for (int i = halfWindowSize; i < state.length() - halfWindowSize; i++) {
            String subString = state.substring(i - halfWindowSize, i + halfWindowSize + 1);
            String newValue = transitions.getOrDefault(subString, ".");
            builder.append(newValue);
        }
        return builder.toString();
    }

    @Test
    public void partTwo() {
        long numGenerations = 5L * (long) Math.pow(10, 10);
        // the sequence converges starting on generation 98 so the final state is always the same
        // the sum then progresses linearly and can be calculated
        long sum = 62 * numGenerations + 655;
        LOG.info("Result: {}", sum);
    }
}
