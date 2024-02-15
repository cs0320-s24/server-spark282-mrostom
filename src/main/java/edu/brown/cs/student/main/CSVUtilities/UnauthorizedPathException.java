package edu.brown.cs.student.main.CSVUtilities;

/**
 * An exception that's generated when an attempted path access is out of protected bounds, extends
 * Exception.
 *
 * @author Sun Joo Park
 */
public class UnauthorizedPathException extends Exception {
  public UnauthorizedPathException(String message) {
    super(message);
  }
}
