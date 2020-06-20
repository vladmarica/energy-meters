package com.vladmarica.energymeters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MovingAverageTest {

  @Test
  public void testMovingAverages() {
    MovingAverage avg = new MovingAverage(3);
    assertEquals(0, avg.getAverage());

    avg.add(10);
    assertEquals(10, avg.getAverage());

    avg.add(20);
    assertEquals(15, avg.getAverage());

    avg.add(30);
    assertEquals(20, avg.getAverage());

    avg.add(16);
    assertEquals(22, avg.getAverage());
  }
}
