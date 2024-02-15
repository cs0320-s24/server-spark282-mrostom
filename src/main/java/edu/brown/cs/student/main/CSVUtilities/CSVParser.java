package edu.brown.cs.student.main.CSVUtilities;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

/**
 * A generic CSVParser class that parses data into a List of T.
 *
 * @author Sun Joo Park
 */
public class CSVParser<T> {
  private final BufferedReader bufferedReader;
  private final CreatorFromRow<T> creatorFromRow;
  private List<String> headerRow;
  // Regex for splitting CSV rows
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /**
   * The constructor for CSVParser.
   *
   * @param reader A reader to read the CSV
   * @param creatorFromRow A row generator to use with data input
   */
  public CSVParser(Reader reader, CreatorFromRow<T> creatorFromRow) {
    this.bufferedReader = new BufferedReader(reader); // Wraps reader w/ BufferedReader
    this.creatorFromRow = creatorFromRow;
    this.headerRow = new ArrayList<>();
  }

  /**
   * A method that parses CSV data into a List of T.
   *
   * @param header Boolean that indicates whether the header is in the CSV (no header = false)
   * @return A List of T objects created from the CSV
   * @exception IOException Failed or interrupted I/O operations
   * @exception FactoryFailureException Malformed data
   * @throw IOException When there is an issue reading the source
   * @throw src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException When object T
   *     cannot be made
   */
  public List<T> parse(boolean header) throws IOException, FactoryFailureException {
    List<T> rowList = new ArrayList<>();

    // Read and add first line, set size standard
    String line = this.bufferedReader.readLine();
    if (line == null) {
      return rowList;
    }
    int size = List.of(regexSplitCSVRow.split(line)).size();
    List<String> firstLine = List.of(regexSplitCSVRow.split(line));

    if (header) {
      // If first row is header, store first row separately as list of strings.
      this.headerRow = firstLine;
      // IMPORTANT: Will not include the first line in the output data.
    } else {
      T firstRow = this.creatorFromRow.create(firstLine);
      rowList.add(firstRow);
    }

    // Read and add rest of the lines, compare size with standard size
    while ((line = this.bufferedReader.readLine()) != null) {
      T row = this.creatorFromRow.create(List.of(regexSplitCSVRow.split(line)), size);
      rowList.add(row);
    }
    return rowList;
  }

  /**
   * A getter that returns the header row as a List of Strings.
   *
   * @return The header row
   */
  public List<String> getHeaderRow() {
    return this.headerRow;
  }
}
