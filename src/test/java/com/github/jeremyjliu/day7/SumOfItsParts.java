/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day7;

import com.github.jeremyjliu.AbstractAdventSolution;
import com.google.common.base.Preconditions;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SumOfItsParts extends AbstractAdventSolution {
    private static final Logger LOG = LoggerFactory.getLogger(SumOfItsParts.class);
    private static final Pattern PATTERN =
            Pattern.compile("^Step (\\p{Upper}) must be finished before step (\\p{Upper}) can begin.$");
    private static final int NUM_WORKERS = 5;
    private static final int TIME_PENALTY_SECONDS = 60;

    private SetMultimap<String, String> requirementMap = MultimapBuilder.hashKeys().hashSetValues().build();

    public SumOfItsParts() {
        super("/day7.txt");
        for (String input : inputLines) {
            Matcher matcher = PATTERN.matcher(input);
            Preconditions.checkArgument(matcher.matches());
            Preconditions.checkArgument(matcher.groupCount() == 2);
            String beforeTask = matcher.group(1);
            String afterTask = matcher.group(2);
            requirementMap.put(beforeTask, afterTask);
        }
    }

    @Test
    public void partOne() {
        SetMultimap<String, String> requirementMapCopy =
                MultimapBuilder.hashKeys().hashSetValues().build(requirementMap);

        // populate queue of ready tasks
        Set<String> keySetView = requirementMapCopy.keySet();
        Set<String> valueSet = Sets.newHashSet(requirementMapCopy.values());
        Queue<String> readyTaskQueue = new PriorityQueue<>(Sets.difference(keySetView, valueSet));

        // process all remaining tasks
        StringBuilder taskOrder = new StringBuilder();
        while (!readyTaskQueue.isEmpty()) {
            String task = readyTaskQueue.remove();
            taskOrder.append(task);
            removeFinishedTask(requirementMapCopy, task, readyTaskQueue);
        }
        LOG.info("Result: {}", taskOrder.toString());
    }

    private void removeFinishedTask(
            SetMultimap<String, String> requirementMapCopy,
            String task,
            Queue<String> readyTaskQueue) {
        Set<String> potentialReadyTasks = requirementMapCopy.removeAll(task);
        Set<String> newValueSet = Sets.newHashSet(requirementMapCopy.values());
        potentialReadyTasks.forEach(potentialReadyTask -> {
            if (!newValueSet.contains(potentialReadyTask)) {
                readyTaskQueue.add(potentialReadyTask);
            }
        });
    }

    @Test
    public void partTwo() {
        SetMultimap<String, String> requirementMapCopy =
                MultimapBuilder.hashKeys().hashSetValues().build(requirementMap);

        // populate queue of ready tasks
        Set<String> keySetView = requirementMapCopy.keySet();
        Set<String> valueSet = Sets.newHashSet(requirementMapCopy.values());
        Queue<String> readyTaskQueue = new PriorityQueue<>(Sets.difference(keySetView, valueSet));

        // workers
        int numFreeWorkers = NUM_WORKERS;
        Queue<Worker> runningWorkerQueue = new PriorityQueue<>(Comparator.comparing(Worker::getFinishTime));

        // start processing
        int currentTime = 0;
        StringBuilder taskOrder = new StringBuilder();
        while (!readyTaskQueue.isEmpty() || !runningWorkerQueue.isEmpty()) {
            // if no ready task or no free worker
            if (readyTaskQueue.isEmpty() || numFreeWorkers == 0) {
                // finish possible tasks from running workers, free workers and increment time
                List<Worker> finishedWorkers = finishFirstTasks(runningWorkerQueue);
                currentTime = finishedWorkers.get(0).getFinishTime();
                numFreeWorkers += finishedWorkers.size();

                // finish tasks and make next tasks available
                String finishedTasks = finishedWorkers.stream()
                        .map(Worker::getTask)
                        .peek(task -> removeFinishedTask(requirementMapCopy, task, readyTaskQueue))
                        .collect(Collectors.joining());
                taskOrder.append(finishedTasks);
            }

            // start all available tasks with all available workers
            while (!readyTaskQueue.isEmpty() && numFreeWorkers > 0) {
                String taskToStart = readyTaskQueue.remove();
                int finishTime = currentTime + TIME_PENALTY_SECONDS + (taskToStart.charAt(0) - 'A' + 1);
                numFreeWorkers--;
                runningWorkerQueue.add(ImmutableWorker.builder()
                        .task(taskToStart)
                        .finishTime(finishTime)
                        .build());
            }
        }
        LOG.debug("Task Order: {}", taskOrder.toString());
        LOG.info("Result: {}", currentTime);
    }

    // get all workers at the head of the queue that finish at the same time
    private List<Worker> finishFirstTasks(Queue<Worker> runningWorkerQueue) {
        Preconditions.checkState(!runningWorkerQueue.isEmpty());
        Worker firstFinished = runningWorkerQueue.peek();
        List<Worker> finishedWorkers = new ArrayList<>();
        while (!runningWorkerQueue.isEmpty()) {
            if (firstFinished.getFinishTime() == runningWorkerQueue.peek().getFinishTime()) {
                finishedWorkers.add(runningWorkerQueue.remove());
            } else {
                break;
            }
        }
        return finishedWorkers;
    }
}
