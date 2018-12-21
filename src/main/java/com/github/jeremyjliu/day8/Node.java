/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day8;

import com.github.jeremyjliu.ImmutableStyle;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public interface Node {
    List<Node> getChildren();
    List<Integer> getMetadata();
    int getLength();
}
