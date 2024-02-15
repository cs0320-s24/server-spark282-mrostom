package edu.brown.cs.student.main.CSVUtilities;

import java.util.ArrayList;
import java.util.List;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

/**
 * A class that creates Lists of strings, implements CreatorFromRow interface.
 *
 * @author Sun Joo Park
 */
public class RowCreator implements CreatorFromRow<List<String>> {

  /**
   * A method that creates a List of Strings.
   *
   * @param row The input List of Strings
   * @return A List of Strings
   */
  @Override
  public List<String> create(List<String> row) {
    return new ArrayList<>(row);
  }

  /**
   * A method that creates a List of Strings if the row is a certain size.
   *
   * @param row The input List of strings
   * @param size The specified size requirement of the row
   * @throws FactoryFailureException If the row's size doesn't match
   * @return A List of Strings
   */
  @Override
  public List<String> create(List<String> row, int size) throws FactoryFailureException {
    // If the given row is not equal to the standard size
    if (row.size() != size) {
      // Declare that data is malformed;
      throw new FactoryFailureException("Found malformed row.", row);
    }
    return new ArrayList<>(row);
  }
}
