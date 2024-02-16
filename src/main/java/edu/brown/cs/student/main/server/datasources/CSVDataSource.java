package edu.brown.cs.student.main.server.datasources;


import edu.brown.cs.student.main.csvtools.CSVParser;
import edu.brown.cs.student.main.csvtools.CreatorFromRow;
import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import edu.brown.cs.student.main.csvtools.RowCreator;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * A class that implements CSVDataSourceInterface. Specifically for importing CSV data
 */
public class CSVDataSource implements CSVDataSourceInterface {

  private List<List<String>> data;
  private List<String> headerRow;

  /**
   * A method for parsing a CSV file.
   *
   * @param filePath  path for the file
   * @param hasHeader boolean indicating whether the file has a header row
   * @throws FactoryFailureException thrown when CSV data is malformed
   * @throws IOException             thrown when there is an error reading the file(file not found,
   *                                 corrupted,…)
   */
  @Override
  public void parseData(String filePath, Boolean hasHeader)
      throws FactoryFailureException, IOException {
    Reader reader = new FileReader(filePath);
    CreatorFromRow creator = new RowCreator();
    CSVParser<List<String>> parser = new CSVParser<>(reader, creator);
    this.data = parser.parse(hasHeader);
    if (hasHeader) {
      this.headerRow = parser.getHeaderRow();
    }
  }

  /**
   * A getter for the data in this source.
   *
   * @return data loaded
   */
  @Override
  public List<List<String>> getData() {
    return this.data; // TODO: proxy pattern
  }

  /**
   * A getter for the header row.
   *
   * @return header row in loaded file
   */
  @Override
  public List<String> getHeaderRow() {
    return this.headerRow; // TODO: proxy pattern
  }
}
