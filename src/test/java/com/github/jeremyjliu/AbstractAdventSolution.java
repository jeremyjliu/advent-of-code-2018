/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu;

import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class AbstractAdventSolution {
    protected final List<String> inputLines;

    public AbstractAdventSolution(String fileName) {
        try {
            this.inputLines = getInputLines(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getInputLines(String fileName) throws IOException {
        URL inputUrl = Resources.getResource(AbstractAdventSolution.class, fileName);
        return Resources.readLines(inputUrl, StandardCharsets.UTF_8);
    }
}
