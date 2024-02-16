package edu.brown.cs.student.main.server.datasources;

import edu.brown.cs.student.main.server.handlers.DatasourceException;
import java.io.IOException;
import java.util.List;
import edu.brown.cs.student.main.csvtools.FactoryFailureException;

public interface APIDataSourceInterface {
  List<List<String>> getData(String State, String County)
      throws IOException, FactoryFailureException, DatasourceException;
}
