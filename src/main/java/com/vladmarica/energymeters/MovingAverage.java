package com.vladmarica.energymeters;

import java.util.LinkedList;

public class MovingAverage {
  private long sum = 0;
  private int count = 0;
  private int maxCount;
  private LinkedList<Long> values;

  public MovingAverage(int maxCount) {
    if (maxCount < 1) {
      throw new IllegalArgumentException("maxCount must be at least 1");
    }

    this.maxCount = maxCount;
    this.values = new LinkedList<>();
  }

  public void add(long value) {
    sum += value;
    values.addLast(value);
    if (count == maxCount) {
      sum -= values.removeFirst();
    } else {
      count++;
    }
  }

  public float getAverage() {
    if (count == 0) {
      return 0;
    }
    return sum / (float) count;
  }
}
