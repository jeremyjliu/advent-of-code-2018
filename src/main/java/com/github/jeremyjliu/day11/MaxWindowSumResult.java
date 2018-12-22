/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day11;

import com.github.jeremyjliu.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public interface MaxWindowSumResult {
    int getX();
    int getY();
    int getWindowSize();
    int getSum();
}
