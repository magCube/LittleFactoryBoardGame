package org.magcube.exception;

public class BuildingActivationException extends Exception {

  public static final String alreadyActivated = "Building has already activated.";
  public static final String cannotProducePoint = "Building cannot produce point.";
  public static final String cannotProduceProduct = "Building cannot produce product.";

  public BuildingActivationException(String message) {
    super(message);
  }
}
