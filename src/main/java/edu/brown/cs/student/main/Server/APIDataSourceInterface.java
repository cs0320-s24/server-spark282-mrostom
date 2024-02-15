package edu.brown.cs.student.main.Server;

import edu.brown.cs.student.main.Server.Handlers.DatasourceException;
import java.io.IOException;
import java.util.List;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

public interface APIDataSourceInterface {
  List<List<String>> getData(String State, String County)
      throws IOException, FactoryFailureException, DatasourceException;
}
