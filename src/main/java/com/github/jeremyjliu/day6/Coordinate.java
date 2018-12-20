/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day6;

import com.github.jeremyjliu.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public interface Coordinate {
    String getLabel();
    int getX();
    int getY();
}
