/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day4;

import com.github.jeremyjliu.ImmutableStyle;
import java.time.LocalDateTime;
import org.immutables.value.Value;

@Value.Immutable
@ImmutableStyle
public interface UnprocessedGuardEvent {
    LocalDateTime getLocalDateTime();
    String getEventMessage();
}
