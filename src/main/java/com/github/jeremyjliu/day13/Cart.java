/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day13;

import com.github.jeremyjliu.ImmutableStyle;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public interface Cart {
    Position getPosition();
    Direction getDirection();
    IntersectionBehavior getIntersectionBehavior();

    static Cart of(Character cartCharacter, int positionX, int positionY) {
        return ImmutableCart.builder()
                .position(ImmutablePosition.builder()
                        .positionX(positionX)
                        .positionY(positionY)
                        .build())
                .direction(Direction.of(cartCharacter))
                .intersectionBehavior(IntersectionBehavior.getInitial())
                .build();
    }
}
