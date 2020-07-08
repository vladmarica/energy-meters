package com.vladmarica.energymeters.client.gui;

public enum RelativeBlockSide {
  FRONT("Front"),
  BACK("Back"),
  TOP("Top"),
  BOTTOM("Bottom"),
  LEFT("Left"),
  RIGHT("Right");

  private String label;

  RelativeBlockSide(String label) {
    this.label = label;
  }

  public String getLabel() {
    return this.label;
  }
}


