/*
 * (c) Copyright 2018 Palantir Technologies Inc. All rights reserved.
 */

package com.github.jeremyjliu;

import com.github.jeremyjliu.day1.ChronalCalibration;
import com.github.jeremyjliu.day2.InventoryManagementSystem;
import com.github.jeremyjliu.day3.NoMatterHowYouSliceIt;

public final class AdventOfCode {
    private AdventOfCode() {}

    public static void main(String[] args) {
        new ChronalCalibration().runSolution();
        new InventoryManagementSystem().runSolution();
        new NoMatterHowYouSliceIt().runSolution();
    }
}
