package edu.brown.cs.student.main.Server;

import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVParser.CreatorFromRow;
import edu.brown.cs.student.main.CSVParser.RowCreator;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

public class CSVDataSource implements DataSource {

  public List<List<String>> data;

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
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (FactoryFailureException e) {
      throw new RuntimeException(e);
    }
    System.out.println(this.data);
    return this.data;
  }
}
