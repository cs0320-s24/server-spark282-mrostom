package edu.brown.cs.student.main.Server.datasources;

import edu.brown.cs.student.main.Server.handlers.DatasourceException;
import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Interface for an API data source. All classes must have a getter for data. */
public interface APIDataSourceInterface {
  List<List<String>> getData(String State, String County)
      throws IOException, DatasourceException;

  String operation() throws ExecutionException;
}
