package edu.brown.cs.student.main.csvtools;

import java.util.List;

/**
 * This interface defines a method that allows your CSV parser to convert each row into an object of
 * some arbitrary passed type.
 */
public interface CreatorFromRow<T> {
  T create(List<String> row) throws FactoryFailureException;

  T create(List<String> row, int size) throws FactoryFailureException;
}
