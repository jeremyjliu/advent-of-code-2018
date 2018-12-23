/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day13;

import com.github.jeremyjliu.ImmutableStyle;
import java.util.EnumSet;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public interface TrackNode {
    Set<Direction> getAvailableDirections();

    // gets a track node based on the cart
    static TrackNode of(Cart cart) {
        Set<Direction> directions;
        switch (cart.getDirection()) {
            case UP:
                directions = EnumSet.of(Direction.UP, Direction.DOWN);
                break;
            case LEFT:
                directions = EnumSet.of(Direction.LEFT, Direction.RIGHT);
                break;
            case DOWN:
                directions = EnumSet.of(Direction.UP, Direction.DOWN);
                break;
            case RIGHT:
                directions = EnumSet.of(Direction.LEFT, Direction.RIGHT);
                break;
            default:
                throw new IllegalArgumentException("Unknown track character");
        }
        return ImmutableTrackNode.builder()
                .availableDirections(directions)
                .build();
    }

    // gets a track node based on track character
    static TrackNode of(Character trackCharacter, boolean hasLeftConnection) {
        Set<Direction> directions;
        switch (trackCharacter) {
            case '-':
                directions = EnumSet.of(Direction.LEFT, Direction.RIGHT);
                break;
            case '|':
                directions = EnumSet.of(Direction.UP, Direction.DOWN);
                break;
            case '+':
                directions = EnumSet.of(Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT);
                break;
            case '/':
                if (hasLeftConnection) {
                    directions = EnumSet.of(Direction.UP, Direction.LEFT);
                } else {
                    directions = EnumSet.of(Direction.DOWN, Direction.RIGHT);
                }
                break;
            case '\\':
                if (hasLeftConnection) {
                    directions = EnumSet.of(Direction.LEFT, Direction.DOWN);
                } else {
                    directions = EnumSet.of(Direction.UP, Direction.RIGHT);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown track character");
        }
        return ImmutableTrackNode.builder()
                .availableDirections(directions)
                .build();
    }
}
