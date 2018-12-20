/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day3;

import com.github.jeremyjliu.AbstractAdventSolution;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NoMatterHowYouSliceIt extends AbstractAdventSolution {
    private static final Logger LOG = LoggerFactory.getLogger(NoMatterHowYouSliceIt.class);
    private static final Pattern PATTERN = Pattern.compile("^#(\\d*) @ (\\d*),(\\d*): (\\d*)x(\\d*)$");
    private static final int GRID_SIZE = 1000;
    private final List<Claim> claims;

    public NoMatterHowYouSliceIt() {
        super("/day3.txt");
        claims = parseClaims(inputLines);
    }

    private List<Claim> parseClaims(List<String> inputLines) {
        return inputLines.stream()
                .map(input -> {
                    Matcher matcher = PATTERN.matcher(input);
                    Preconditions.checkArgument(matcher.matches());
                    Preconditions.checkArgument(matcher.groupCount() == 5);
                    return ImmutableClaim.builder()
                            .id(Integer.parseInt(matcher.group(1)))
                            .x(Integer.parseInt(matcher.group(2)))
                            .y(Integer.parseInt(matcher.group(3)))
                            .width(Integer.parseInt(matcher.group(4)))
                            .height(Integer.parseInt(matcher.group(5)))
                            .build();
                }).collect(Collectors.toList());
    }

    @Test
    public void partOne() {
        int [][] fabric = new int[GRID_SIZE][];
        for (int i = 0; i < GRID_SIZE; i++) {
            fabric[i] = new int[GRID_SIZE];
        }

        claims.forEach(claim -> {
            for (int i = claim.getX(); i < claim.getX() + claim.getWidth(); i++) {
                for (int j = claim.getY(); j < claim.getY() + claim.getHeight(); j++) {
                    fabric[j][i]++;
                }
            }
        });

        int numInOverlapping = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (fabric[j][i] >= 2) {
                    numInOverlapping++;
                }
            }
        }
        LOG.info("Result: {}", Integer.toString(numInOverlapping));
    }

    @Test
    public void partTwo() {
        int [][] fabric = new int[GRID_SIZE][];
        for (int i = 0; i < GRID_SIZE; i++) {
            fabric[i] = new int[GRID_SIZE];
        }

        claims.forEach(claim -> {
            for (int i = claim.getX(); i < claim.getX() + claim.getWidth(); i++) {
                for (int j = claim.getY(); j < claim.getY() + claim.getHeight(); j++) {
                    fabric[j][i]++;
                }
            }
        });

        for (Claim claim : claims) {
            boolean allOnes = true;
            for (int i = claim.getX(); i < claim.getX() + claim.getWidth(); i++) {
                for (int j = claim.getY(); j < claim.getY() + claim.getHeight(); j++) {
                    if (fabric[j][i] != 1) {
                        allOnes = false;
                    }
                }
            }

            if (allOnes) {
                LOG.info("Result: {}", Integer.toString(claim.getId()));
            }
        }
    }
}
