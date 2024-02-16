package edu.brown.cs.student.main.Server.DataSources;

import edu.brown.cs.student.main.CSVUtilities.CSVParser;
import edu.brown.cs.student.main.CSVUtilities.CreatorFromRow;
import edu.brown.cs.student.main.CSVUtilities.RowCreator;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

public class CSVDataSource implements CSVDataSourceInterface {

  public List<List<String>> data;
  public List<String> headerRow;

  public CSVDataSource() {}

  public List<List<String>> getData2() {
    return this.data;
  }

  @Override
  public List<List<String>> getData(String filePath) {

    // Parsing:
    Reader reader = null;
    try {
      reader = new FileReader(filePath);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e); // TODO: fix
    }
    // TODO: handle exceptions are errors
    CreatorFromRow creator = new RowCreator();
    CSVParser<List<String>> parser = new CSVParser<>(reader, creator);
    try {
      this.data = parser.parse(true);
      this.headerRow = parser.getHeaderRow();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (FactoryFailureException e) {
      throw new RuntimeException(e);
    }
    return this.data;
  }

  @Override
  public List<String> getHeaderRow() {
    return this.headerRow;
  }
}
