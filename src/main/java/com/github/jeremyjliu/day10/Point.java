/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day10;

import com.github.jeremyjliu.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public interface Point {
    int getPositionX();
    int getPositionY();
    int getVelocityX();
    int getVelocityY();
}
