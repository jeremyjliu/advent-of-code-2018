/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu.day10;

import com.github.jeremyjliu.AbstractAdventSolution;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.Test;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StarsAlign extends AbstractAdventSolution {
    private static final Logger LOG = LoggerFactory.getLogger(StarsAlign.class);
    private static final Pattern PATTERN =
            Pattern.compile("^position=<([ -]\\d*), ([ -]\\d*)> velocity=<([ -]\\d*), ([ -]\\d*)>$");

    private List<Point> parsedPoints;

    public StarsAlign() {
        super("/day10.txt");
        parsedPoints = parsePoints(inputLines);
    }

    private List<Point> parsePoints(List<String> inputLines) {
        return inputLines.stream()
                .map(input -> {
                    Matcher matcher = PATTERN.matcher(input);
                    Preconditions.checkArgument(matcher.matches());
                    Preconditions.checkArgument(matcher.groupCount() == 4);
                    return ImmutablePoint.builder()
                            .positionX(Integer.parseInt(matcher.group(1).replace(" ", "")))
                            .positionY(Integer.parseInt(matcher.group(2).replace(" ", "")))
                            .velocityX(Integer.parseInt(matcher.group(3).replace(" ", "")))
                            .velocityY(Integer.parseInt(matcher.group(4).replace(" ", "")))
                            .build();
                }).collect(Collectors.toList());
    }

    @Test
    public void partOne() throws Exception {
        List<Point> points = new ArrayList<>(parsedPoints);
        points = updatePoints(points, 10675); // fast forward by some amount, actual amount 10681
        List<Integer> initXData = points.stream()
                .map(Point::getPositionX)
                .collect(Collectors.toList());
        List<Integer> initYData = points.stream()
                .map(Point::getPositionY)
                .collect(Collectors.toList());

        // Create Chart
        final XYChart chart = new XYChartBuilder()
                .width(1000)
                .height(200)
                .title("Stars")
                .xAxisTitle("X")
                .yAxisTitle("Y").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        chart.getStyler().setMarkerSize(8);

        chart.addSeries("stars", initXData, initYData);

        // Show it
        final SwingWrapper<XYChart> sw = new SwingWrapper<>(chart);
        sw.displayChart();

        while (true) {
            Thread.sleep(1000);
            LOG.info("Points: {}", points);
            points = updatePoints(points, 1);
            List<Integer> xData = points.stream()
                    .map(Point::getPositionX)
                    .collect(Collectors.toList());
            List<Integer> yData = points.stream()
                    .map(Point::getPositionY)
                    .collect(Collectors.toList());

            javax.swing.SwingUtilities.invokeLater(() -> {
                chart.updateXYSeries("stars", xData, yData, null);
                sw.repaintChart();
            });
        }
    }

    private List<Point> updatePoints(List<Point> points, int numCycles) {
        return points.stream()
                .map(point -> ImmutablePoint.builder()
                        .positionX(point.getPositionX() + numCycles * point.getVelocityX())
                        .positionY(point.getPositionY() + numCycles * point.getVelocityY())
                        .velocityX(point.getVelocityX())
                        .velocityY(point.getVelocityY())
                        .build())
                .collect(Collectors.toList());
    }
}
