/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day2;

import com.github.jeremyjliu.AbstractAdventSolution;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;

public final class InventoryManagementSystem extends AbstractAdventSolution {

    public InventoryManagementSystem() {
        super(LoggerFactory.getLogger(InventoryManagementSystem.class), "/day2.txt");
    }

    private static Map<Character, Integer> getLetterMap(String input) {
        Map<Character, Integer> letterMap = new HashMap<>();
        for (char letter : input.toCharArray()) {
            letterMap.compute(letter, (k, v) -> (v == null) ? 1 : v + 1);
        }
        return letterMap;
    }

    @Override
    public String partOne() {
        int numExactTwoLetters = 0;
        int numExactThreeLetters = 0;
        for (String input : inputLines) {
            boolean foundTwo = false;
            boolean foundThree = false;
            Map<Character, Integer> letterMap = getLetterMap(input);
            for (Integer letterCount : letterMap.values()) {
                if (!foundTwo && letterCount.equals(2)) {
                    numExactTwoLetters++;
                    foundTwo = true;
                } else if (!foundThree && letterCount.equals(3)) {
                    numExactThreeLetters++;
                    foundThree = true;
                }
            }
        }
        return Integer.toString(numExactTwoLetters * numExactThreeLetters);
    }

    // returns the index of the only non matching letter, -1 otherwise
    private static Integer getOnlyNonMatchingLetterIndex(String s1, String s2) {
        int result = -1;
        int index = 0;
        while (index < s1.length() && index < s2.length()) {
            if (s1.charAt(index) != s2.charAt(index)) {
                if (result == -1) {
                    result = index;
                } else {
                    return -1;
                }
            }
            index++;
        }
        return result;
    }

    @Override
    public String partTwo() {
        for (String input1 : inputLines) {
            for (String input2 : inputLines) {
                int index = getOnlyNonMatchingLetterIndex(input1, input2);
                if (index > -1) {
                    return input1.substring(0, index) + input1.substring(index + 1);
                }
            }
        }
        return "";
    }
}
