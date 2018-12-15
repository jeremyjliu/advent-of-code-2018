/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu;

import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.slf4j.Logger;

public abstract class AbstractAdventSolution {
    protected final Logger log;
    protected final List<String> inputLines;

    public AbstractAdventSolution(Logger log, String fileName) {
        this.log = log;
        try {
            this.inputLines = getInputLines(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract String partOne();

    public abstract String partTwo();

    public final void runSolution() {
        log.info("Part One: {}", partOne());
        log.info("Part Two: {}", partTwo());
    }

    private static List<String> getInputLines(String fileName) throws IOException {
        URL inputUrl = Resources.getResource(AbstractAdventSolution.class, fileName);
        return Resources.readLines(inputUrl, StandardCharsets.UTF_8);
    }
}
