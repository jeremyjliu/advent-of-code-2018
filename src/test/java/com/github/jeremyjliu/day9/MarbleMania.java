/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day9;

import com.github.jeremyjliu.AbstractAdventSolution;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MarbleMania extends AbstractAdventSolution {
    private static final Logger LOG = LoggerFactory.getLogger(MarbleMania.class);

    public MarbleMania() {
        super("/day9.txt");
    }

    @Test
    public void partOne() {
        int lastMarblePoints = 71436;
        int numPlayers = 466;
        playGame(lastMarblePoints, numPlayers);
    }

    private static void playGame(int lastMarblePoints, int numPlayers) {
        List<Integer> circle = new ArrayList<>();
        Map<Integer, Integer> scoresByPlayer = new HashMap<>();
        IntStream.range(0, numPlayers).forEach(playerNumber -> scoresByPlayer.put(playerNumber, 0));

        // initialize game
        circle.add(0);
        int currentMarbleIndex = 0;
        int currentPlayer = 0;

        for (int turn = 1; turn <= lastMarblePoints; turn++) {
            int currentMarbleValue = turn;
            if (turn % 23 == 0) {
                int indexToRemove = Math.floorMod(currentMarbleIndex - 7, circle.size());
                int removedMarbleValue = circle.remove(indexToRemove);
                scoresByPlayer.computeIfPresent(currentPlayer, (k, v) -> v + currentMarbleValue + removedMarbleValue);
                currentMarbleIndex = Math.floorMod(indexToRemove, circle.size());
            } else {
                int indexToInsert = Math.floorMod(currentMarbleIndex + 2, circle.size());
                circle.add(indexToInsert, currentMarbleValue);
                currentMarbleIndex = indexToInsert;
            }
            currentPlayer = (currentPlayer + 1) % numPlayers;
        }
        int maxScore = scoresByPlayer.values().stream().max(Integer::compareTo).orElseThrow(
                () -> new RuntimeException("No maximum score!"));
        LOG.debug("Circle: {}", circle);
        LOG.debug("Player Scores: {}", scoresByPlayer);
        LOG.info("Result: {}", maxScore);
    }

    @Test
    public void partTwo() {
        int lastMarblePoints = 71436 * 100;
        int numPlayers = 466;
        playGameMoreEfficiently(lastMarblePoints, numPlayers);
    }

    // play game with a circular deque for more efficient inserts/deletions, keeping current position at head
    private static void playGameMoreEfficiently(int lastMarblePoints, int numPlayers) {
        Deque<Integer> circle = new ArrayDeque<>();
        Map<Integer, Long> scoresByPlayer = new HashMap<>();
        IntStream.range(0, numPlayers).forEach(playerNumber -> scoresByPlayer.put(playerNumber, 0L));

        // initialize game
        circle.addLast(0);
        int currentPlayer = 0;

        for (int turn = 1; turn <= lastMarblePoints; turn++) {
            int currentMarbleValue = turn;
            if (turn % 23 == 0) {
                moveCounterClockwiseN(circle, 7);
                int removedMarbleValue = circle.removeFirst();
                scoresByPlayer.computeIfPresent(currentPlayer, (k, v) -> v + currentMarbleValue + removedMarbleValue);
            } else {
                moveClockwiseN(circle, 2);
                circle.addFirst(currentMarbleValue);
            }
            currentPlayer = (currentPlayer + 1) % numPlayers;
        }
        long maxScore = scoresByPlayer.values().stream().max(Long::compareTo).orElseThrow(
                () -> new RuntimeException("No maximum score!"));
        LOG.debug("Circle: {}", circle);
        LOG.info("Player Scores: {}", scoresByPlayer);
        LOG.info("Result: {}", maxScore);
    }

    private static <E> void moveClockwiseN(Deque<E> deque, int num) {
        Preconditions.checkArgument(deque.size() > 0);
        IntStream.range(0, num).forEach(i -> {
            E head = deque.removeFirst();
            deque.addLast(head);
        });
    }

    private static <E> void moveCounterClockwiseN(Deque<E> deque, int num) {
        Preconditions.checkArgument(deque.size() > 0);
        IntStream.range(0, num).forEach(i -> {
            E head = deque.removeLast();
            deque.addFirst(head);
        });
    }
}
