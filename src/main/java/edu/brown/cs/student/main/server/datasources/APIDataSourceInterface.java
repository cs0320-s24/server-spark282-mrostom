package edu.brown.cs.student.main.server.datasources;

import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import edu.brown.cs.student.main.server.handlers.DatasourceException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Interface for an API data source. All classes must have a getter for data. */
public interface APIDataSourceInterface {
  List<List<String>> getData(String State, String County)
      throws IOException, FactoryFailureException, DatasourceException;
  String operation() throws ExecutionException;
}

