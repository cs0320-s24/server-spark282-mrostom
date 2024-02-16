package edu.brown.cs.student.main.Server.DataSources;

import edu.brown.cs.student.main.Server.Handlers.DatasourceException;
import java.io.IOException;
import java.util.List;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

public interface CSVDataSourceInterface {
  List<List<String>> getData(String fileName)
      throws IOException, FactoryFailureException, DatasourceException;

  public List<List<String>> getData2();

  public List<String> getHeaderRow();
}
