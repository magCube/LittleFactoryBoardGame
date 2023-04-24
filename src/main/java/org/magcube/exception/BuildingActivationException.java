package org.magcube.exception;

public class BuildingActivationException extends Exception {

  public BuildingActivationException() {
    super("Building has already activated.");
  }
}
