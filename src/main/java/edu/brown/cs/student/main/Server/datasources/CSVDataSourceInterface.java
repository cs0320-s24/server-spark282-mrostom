package edu.brown.cs.student.main.Server.datasources;

import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import java.io.IOException;
import java.util.List;

/** Interface for a CSV data source. All classes must have a parser and a getter. */
public interface CSVDataSourceInterface {

  void parseData(String filePath, Boolean hasHeader) throws FactoryFailureException, IOException;

  List<List<String>> getData();

  List<String> getHeaderRow();
}
