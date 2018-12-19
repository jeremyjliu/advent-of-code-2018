/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day4;

import com.github.jeremyjliu.AbstractAdventSolution;
import com.google.common.base.Preconditions;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;

public final class ReposeRecord extends AbstractAdventSolution {
    private static final String INSTANCE_PATTERN = "yyyy-MM-dd HH:mm";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(INSTANCE_PATTERN)
            .withZone(ZoneId.systemDefault());
    private static final int MINUTES_IN_DAY = 60;
    private Map<Integer, Integer> totalMinutesAsleepByGuardId = new HashMap<>();
    private Map<Integer, int[]> minutesAsleepByGuardId = new HashMap<>();

    public ReposeRecord() {
        super(LoggerFactory.getLogger(ReposeRecord.class), "/day4.txt");

        // sort lines
        List<UnprocessedGuardEvent> unprocessedGuardEvents = parseGuardEvents(inputLines);
        unprocessedGuardEvents.sort(Comparator.comparing(UnprocessedGuardEvent::getLocalDateTime));

        // process lines
        List<GuardEvent> events = processGuardEvents(unprocessedGuardEvents);

        // process guard events in pairs (sleep + wake)
        for (int i = 0; i < events.size(); i += 2) {
            GuardEvent sleep = events.get(i);
            GuardEvent wake = events.get(i + 1);
            Preconditions.checkState(sleep.getType().equals(GuardEventType.FALL_ASLEEP));
            Preconditions.checkState(wake.getType().equals(GuardEventType.WAKE_UP));
            Preconditions.checkState(sleep.getGuardId() == wake.getGuardId());
            Preconditions.checkState(sleep.getLocalDateTime().getHour() == 0);
            Preconditions.checkState(wake.getLocalDateTime().getHour() == 0);
            updateTotalMinutesAsleep(sleep.getGuardId(), sleep.getLocalDateTime(), wake.getLocalDateTime());
            updateMinutesAsleep(sleep.getGuardId(), sleep.getLocalDateTime(), wake.getLocalDateTime());
        }
    }

    private List<UnprocessedGuardEvent> parseGuardEvents(List<String> inputLines) {
        return inputLines.stream()
                .map(input -> {
                    int leftBracket = input.indexOf('[');
                    int rightBracket = input.indexOf(']');
                    String timestamp = input.substring(leftBracket + 1, rightBracket);
                    String eventMessage = input.substring(rightBracket + 2);
                    return ImmutableUnprocessedGuardEvent.builder()
                            .eventMessage(eventMessage)
                            .localDateTime(DATETIME_FORMATTER.parse(timestamp, LocalDateTime::from))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<GuardEvent> processGuardEvents(List<UnprocessedGuardEvent> unprocessedGuardEvents) {
        List<GuardEvent> processedEvents = new ArrayList<>();
        Integer guardId = null;
        for (UnprocessedGuardEvent event : unprocessedGuardEvents) {
            String eventMessage = event.getEventMessage();
            if (eventMessage.startsWith("Guard")) {
                guardId = Integer.parseInt(eventMessage.split(" ")[1].substring(1));
            } else if (eventMessage.startsWith("wakes up")) {
                Preconditions.checkState(guardId != null);
                processedEvents.add(ImmutableGuardEvent.builder()
                        .guardId(guardId)
                        .type(GuardEventType.WAKE_UP)
                        .localDateTime(event.getLocalDateTime())
                        .build());
            } else if (eventMessage.startsWith("falls asleep")) {
                Preconditions.checkState(guardId != null);
                processedEvents.add(ImmutableGuardEvent.builder()
                        .guardId(guardId)
                        .type(GuardEventType.FALL_ASLEEP)
                        .localDateTime(event.getLocalDateTime())
                        .build());
            } else {
                throw new IllegalArgumentException(String.format("Unknown event message %s", eventMessage));
            }
        }
        return processedEvents;
    }

    private void updateTotalMinutesAsleep(int guardId, LocalDateTime sleepTime, LocalDateTime wakeTime) {
        Preconditions.checkArgument(sleepTime.isBefore(wakeTime));
        Preconditions.checkArgument(sleepTime.getHour() == wakeTime.getHour());
        int minutesAsleep = wakeTime.getMinute() - sleepTime.getMinute();
        totalMinutesAsleepByGuardId.compute(guardId, (k, v) -> v == null ? minutesAsleep : v + minutesAsleep);
    }

    private void updateMinutesAsleep(int guardId, LocalDateTime sleepTime, LocalDateTime wakeTime) {
        Preconditions.checkArgument(sleepTime.isBefore(wakeTime));
        Preconditions.checkArgument(sleepTime.getHour() == wakeTime.getHour());
        int[] minutesAsleep = minutesAsleepByGuardId.getOrDefault(guardId, new int[MINUTES_IN_DAY]);
        for (int i = sleepTime.getMinute(); i < wakeTime.getMinute(); i++) {
            minutesAsleep[i]++;
        }
        minutesAsleepByGuardId.put(guardId, minutesAsleep);
    }

    @Override
    public String partOne() {
        int maxGuardId = Collections.max(totalMinutesAsleepByGuardId.entrySet(),
                Comparator.comparingInt(Map.Entry::getValue)).getKey();

        int maxIndex = getMaxMinuteInArray(minutesAsleepByGuardId.get(maxGuardId));
        return Integer.toString(maxGuardId * maxIndex);
    }

    private int getMaxMinuteInArray(int[] minutesAsleep) {
        int maxMinutes = Integer.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 0; i < minutesAsleep.length; i++) {
            if (minutesAsleep[i] > maxMinutes) {
                maxMinutes = minutesAsleep[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    @Override
    public String partTwo() {
        int maxGuardId = Integer.MIN_VALUE;
        int maxIndex = Integer.MIN_VALUE;
        int maxMinutes = Integer.MIN_VALUE;

        for (Map.Entry<Integer, int[]> entry : minutesAsleepByGuardId.entrySet()) {
            int[] minutesAsleep = entry.getValue();
            for (int i = 0; i < minutesAsleep.length; i++) {
                if (minutesAsleep[i] > maxMinutes) {
                    maxMinutes = minutesAsleep[i];
                    maxIndex = i;
                    maxGuardId = entry.getKey();
                }
            }
        }
        return Integer.toString(maxGuardId * maxIndex);
    }
}
