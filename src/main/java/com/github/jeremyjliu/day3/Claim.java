/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day3;

import com.github.jeremyjliu.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public interface Claim {

    // The id of the claim.
    int getId();

    // The number of inches between the left edge of the fabric and the left edge of the rectangle.
    int getX();

    // The number of inches between the top edge of the fabric and the top edge of the rectangle.
    int getY();

    // The width of the rectangle in inches.
    int getWidth();

    // The height of the rectangle in inches.
    int getHeight();
}
