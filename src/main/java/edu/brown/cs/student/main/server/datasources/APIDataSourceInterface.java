package edu.brown.cs.student.main.server.datasources;

import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import edu.brown.cs.student.main.server.handlers.DatasourceException;
import java.io.IOException;
import java.util.List;

/**
 * Interface for an Api data source. All classes must have a parser and a getter.
 */
public interface APIDataSourceInterface {
  List<List<String>> getData(String State, String County)
      throws DatasourceException;
}
