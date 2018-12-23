/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day13;

import com.github.jeremyjliu.AbstractAdventSolution;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MineCartMadness extends AbstractAdventSolution {
    private static final Logger LOG = LoggerFactory.getLogger(MineCartMadness.class);
    private static final int GRID_SIZE = 150;
    private static final Comparator<Cart> CART_COMPARATOR = (c1, c2) -> {
        Position p1 = c1.getPosition();
        Position p2 = c2.getPosition();
        int result = Integer.compare(p1.getPositionY(), p2.getPositionY());
        if (result == 0) {
            // both X are equal -> compare Y too
            result = Integer.compare(p1.getPositionX(), p2.getPositionX());
        }
        return result;
    };
    private static final Set<Character> cartCharacters = ImmutableSet.of('^', '<', 'v', '>');
    private static final Set<Character> trackCharacters = ImmutableSet.of('|', '-', '+', '\\', '/');
    private TrackNode[][] parsedTrackGrid;
    private List<Cart> parsedCarts;

    public MineCartMadness() {
        super("/day13.txt");

        TrackNode[][] trackGrid = new TrackNode[GRID_SIZE][];
        List<Cart> carts = new ArrayList<>();
        for (int y = 0; y < inputLines.size(); y++) {
            trackGrid[y] = new TrackNode[GRID_SIZE];
            char[] line = inputLines.get(y).toCharArray();
            for (int x = 0; x < line.length; x++) {
                char character = line[x];
                if (cartCharacters.contains(character)) {
                    Cart cart = Cart.of(character, x, y);
                    carts.add(cart);
                    trackGrid[y][x] = TrackNode.of(cart);
                } else if (trackCharacters.contains(character)) {
                    // assumes that \/ and /\ will never occur
                    boolean hasLeftConnection = (x - 1 >= 0)
                            && trackGrid[y][x - 1] != null
                            && trackGrid[y][x - 1].getAvailableDirections().contains(Direction.RIGHT);
                    trackGrid[y][x] = TrackNode.of(character, hasLeftConnection);
                }
            }
        }
        parsedTrackGrid = trackGrid;
        parsedCarts = carts;
    }

    @Test
    public void partOne() {
        // simulate carts until two carts have same position
        SortedSet<Cart> carts = new TreeSet<>(CART_COMPARATOR);
        carts.addAll(parsedCarts);
        Set<Position> cartPositions = new HashSet<>();
        carts.forEach(cart -> cartPositions.add(cart.getPosition()));

        boolean crashHappened = false;
        int tick = 0;
        while (!crashHappened) {
            LOG.debug("Carts on Tick {}: {}", tick, carts);
            SortedSet<Cart> newCarts = new TreeSet<>(CART_COMPARATOR);
            for (Cart cart : carts) {
                Cart newCart = simulateCartOneTick(cart);
                cartPositions.remove(cart.getPosition());
                newCarts.add(newCart);
                if (!cartPositions.add(newCart.getPosition())) {
                    LOG.info("Result: Crash on tick {} at position {}", tick, newCart.getPosition());
                    crashHappened = true;
                    break;
                }
                tick++;
            }
            carts = newCarts;
        }
    }

    private Cart simulateCartOneTick(Cart cart) {
        TrackNode track = parsedTrackGrid[cart.getPosition().getPositionY()][cart.getPosition().getPositionX()];
        Preconditions.checkState(track != null);

        // available directions does not include the reverse direction, cart cannot turn around
        Set<Direction> availableDirections = track.getAvailableDirections()
                .stream()
                .filter(direction -> !direction.equals(Direction.reverseDirection(cart.getDirection())))
                .collect(Collectors.toSet());

        Preconditions.checkState(availableDirections.size() == 1 || availableDirections.size() == 3);
        // intersection
        if (availableDirections.size() == 3) {
            Direction chosenDirection = getNewIntersectionDirection(
                    cart.getDirection(), cart.getIntersectionBehavior());
            Preconditions.checkState(availableDirections.contains(chosenDirection));
            Position newPosition = getNewPosition(cart.getPosition(), chosenDirection);
            return ImmutableCart.builder()
                    .position(newPosition)
                    .direction(chosenDirection)
                    .intersectionBehavior(IntersectionBehavior.getNext(cart.getIntersectionBehavior()))
                    .build();
        }

        // curve or straight, cart takes the only possible direction
        Direction availableDirection = Iterables.getOnlyElement(availableDirections);
        Position newPosition = getNewPosition(cart.getPosition(), availableDirection);
        return ImmutableCart.builder()
                .position(newPosition)
                .direction(availableDirection)
                .intersectionBehavior(cart.getIntersectionBehavior())
                .build();
    }

    private Direction getNewIntersectionDirection(Direction currentDirection, IntersectionBehavior behavior) {
        switch (behavior) {
            case LEFT:
                return Direction.turnLeft(currentDirection);
            case RIGHT:
                return Direction.turnRight(currentDirection);
            case STRAIGHT:
                return Direction.goStraight(currentDirection);
            default:
                throw new IllegalArgumentException("Unknown intersection behavior");
        }
    }

    private Position getNewPosition(Position currentPosition, Direction direction) {
        ImmutablePosition.Builder newPosition = ImmutablePosition.builder()
                .from(currentPosition);
        switch (direction) {
            case LEFT:
                newPosition.positionX(currentPosition.getPositionX() - 1);
                break;
            case UP:
                newPosition.positionY(currentPosition.getPositionY() - 1);
                break;
            case RIGHT:
                newPosition.positionX(currentPosition.getPositionX() + 1);
                break;
            case DOWN:
                newPosition.positionY(currentPosition.getPositionY() + 1);
                break;
            default:
                throw new IllegalArgumentException("Unknown direction");
        }
        return newPosition.build();
    }

    @Test
    public void partTwo() {
        // remove carts when they collide
        SortedSet<Cart> carts = new TreeSet<>(CART_COMPARATOR);
        carts.addAll(parsedCarts);
        Map<Position, Cart> cartPositions = new HashMap<>();
        carts.forEach(cart -> cartPositions.put(cart.getPosition(), cart));

        boolean cartsRemain = true;
        int tick = 0;
        while (cartsRemain) {
            LOG.debug("Carts on tick {}: {}", tick, carts);
            SortedSet<Cart> newCarts = new TreeSet<>(CART_COMPARATOR);
            Set<Cart> crashedCarts = new HashSet<>();
            for (Cart cart : carts) {
                tick++;
                // this cart has already crashed, do not process
                if (crashedCarts.contains(cart)) {
                    continue;
                }

                Cart newCart = simulateCartOneTick(cart);
                cartPositions.remove(cart.getPosition());
                if (cartPositions.containsKey(newCart.getPosition())) {
                    Cart crashedCart = cartPositions.get(newCart.getPosition());
                    LOG.debug("Crash on tick {} at position {} between new cart {} and {}",
                            tick, newCart.getPosition(), newCart, crashedCart);
                    cartPositions.remove(crashedCart.getPosition());
                    if (newCarts.contains(crashedCart)) {
                        newCarts.remove(crashedCart);
                    } else {
                        crashedCarts.add(crashedCart);
                    }
                } else {
                    cartPositions.put(newCart.getPosition(), newCart);
                    newCarts.add(newCart);
                }
            }

            if (newCarts.size() == 1) {
                LOG.info("Result: {}", Iterables.getOnlyElement(newCarts));
                cartsRemain = false;
            }
            carts = newCarts;
        }
    }
}
