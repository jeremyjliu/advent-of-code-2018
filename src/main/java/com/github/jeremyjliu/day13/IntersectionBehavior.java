/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day13;

public enum IntersectionBehavior {
    LEFT, STRAIGHT, RIGHT;

    public static IntersectionBehavior getInitial() {
        return LEFT;
    }

    public static IntersectionBehavior getNext(IntersectionBehavior behavior) {
        return values()[Math.floorMod(behavior.ordinal() + 1, values().length)];
    }
}
