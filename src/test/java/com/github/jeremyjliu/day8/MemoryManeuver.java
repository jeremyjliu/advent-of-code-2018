/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day8;

import com.github.jeremyjliu.AbstractAdventSolution;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MemoryManeuver extends AbstractAdventSolution {
    private static final Logger LOG = LoggerFactory.getLogger(MemoryManeuver.class);
    private Node rootNode;

    public MemoryManeuver() {
        super("/day8.txt");
        Preconditions.checkArgument(inputLines.size() == 1);
        List<Integer> license = Arrays.stream(inputLines.get(0).split(" "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        rootNode = getNodeFromLicense(license, 0);
    }

    private Node getNodeFromLicense(List<Integer> license, int startingIndex) {
        Preconditions.checkArgument(license.size() >= startingIndex + 3);
        int numChildren = license.get(startingIndex);
        int numMetadata = license.get(startingIndex + 1);

        // base condition
        if (numChildren == 0) {
            List<Integer> metadata = license.subList(startingIndex + 2, startingIndex + 2 + numMetadata);
            return ImmutableNode.builder()
                    .children(Collections.emptyList())
                    .metadata(metadata)
                    .length(2 + numMetadata)
                    .build();
        }

        // recursively find each child node
        int newStartingIndex = startingIndex + 2;
        List<Node> children = new ArrayList<>();
        for (int i = 0; i < numChildren; i++) {
            Node child = getNodeFromLicense(license, newStartingIndex);
            children.add(child);
            newStartingIndex += child.getLength();
        }

        // create new node
        int length = 2 + children.stream().map(Node::getLength).reduce(0, Integer::sum) + numMetadata;
        List<Integer> metadata = license.subList(newStartingIndex, newStartingIndex + numMetadata);
        return ImmutableNode.builder()
                .children(children)
                .metadata(metadata)
                .length(length)
                .build();
    }

    @Test
    public void partOne() {
        int metadataSum = sumMetadata(rootNode);
        LOG.info("Result: {}", metadataSum);
    }

    private int sumMetadata(Node node) {
        int childMetadataSum = node.getChildren().stream()
                .map(this::sumMetadata)
                .reduce(0, Integer::sum);
        int metadataSum = node.getMetadata().stream().reduce(0, Integer::sum);
        return childMetadataSum + metadataSum;
    }

    @Test
    public void partTwo() {
        int nodeValue = calculateNodeValue(rootNode);
        LOG.info("Result: {}", nodeValue);
    }

    private int calculateNodeValue(Node node) {
        if (node.getChildren().size() == 0) {
            return node.getMetadata().stream().reduce(0, Integer::sum);
        }

        List<Integer> childrenValues = node.getChildren().stream()
                .map(this::calculateNodeValue)
                .collect(Collectors.toList());
        return node.getMetadata().stream()
                .map(index -> {
                    int trueIndex = index - 1;
                    if (trueIndex < 0 || trueIndex > childrenValues.size() - 1) {
                        return 0;
                    } else {
                        return childrenValues.get(trueIndex);
                    }
                }).reduce(0, Integer::sum);
    }
}
