package edu.brown.cs.student.main.CSVUtilities;

import java.util.List;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

/**
 * This interface defines a method that allows your CSV parser to convert each row into an object of
 * some arbitrary passed type.
 */
public interface CreatorFromRow<T> {
  T create(List<String> row) throws FactoryFailureException;

  T create(List<String> row, int size) throws FactoryFailureException;
}
