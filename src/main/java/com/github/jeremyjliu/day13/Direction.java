/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day13;

public enum Direction {
    UP, LEFT, DOWN, RIGHT;

    public static Direction of(Character cartCharacter) {
        switch (cartCharacter) {
            case '^':
                return Direction.UP;
            case '<':
                return Direction.LEFT;
            case 'v':
                return Direction.DOWN;
            case '>':
                return Direction.RIGHT;
            default:
                throw new IllegalArgumentException("Unknown cart character");
        }
    }

    public static Direction turnLeft(Direction direction) {
        return values()[Math.floorMod(direction.ordinal() + 1, values().length)];
    }

    public static Direction turnRight(Direction direction) {
        return values()[Math.floorMod(direction.ordinal() - 1, values().length)];
    }

    public static Direction goStraight(Direction direction) {
        return direction;
    }

    public static Direction reverseDirection(Direction direction) {
        switch (direction) {
            case LEFT:
                return RIGHT;
            case UP:
                return DOWN;
            case RIGHT:
                return LEFT;
            case DOWN:
                return UP;
            default:
                throw new IllegalArgumentException("Unknown direction");
        }
    }
}
