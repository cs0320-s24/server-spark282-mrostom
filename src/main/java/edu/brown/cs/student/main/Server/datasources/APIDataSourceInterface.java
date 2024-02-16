package edu.brown.cs.student.main.Server.datasources;

import edu.brown.cs.student.main.Server.handlers.DatasourceException;
import java.io.IOException;
import java.util.List;

/** Interface for an API data source. All classes must have a getter for data. */
public interface APIDataSourceInterface {
  List<List<String>> getData(String State, String County) throws IOException, DatasourceException;
}
