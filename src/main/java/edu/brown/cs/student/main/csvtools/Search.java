package edu.brown.cs.student.main.csvtools;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that searches for a value in a CSV file.
 *
 * @author Sun Joo Park
 */
public class Search {
  private final List<List<String>> data;
  private final List<String> headerRow;
  private final List<List<String>> result;
  private final String value;

  public Search(String value, List<List<String>> data, List<String> headerRow) {
    this.data = data;
    this.value = value.toLowerCase();
    this.headerRow = headerRow;
    this.result = new ArrayList<>();
  }

  private boolean checkIndexBounds(int index) {
    // If given index is a viable index
    return !this.data.isEmpty() && index >= 0 && this.data.get(0).size() > index;
  }

  public List<List<String>> searchIndex(int index) throws IllegalArgumentException {

    if (!checkIndexBounds(index)) {
      System.out.println("out of bounds");
      throw new IllegalArgumentException();
    }

    for (List<String> row : this.data) {
      List<String> lowerCase = row.stream().map(String::toLowerCase).toList();
      if (lowerCase.get(index).equals(this.value)) {
        this.result.add(row);
      }
    }
    return this.result;
  }

  public List<List<String>> searchColumn(String header) throws IllegalArgumentException {
    // find index associated with header
    int index = this.headerRow.indexOf(header);
    if (index < 0) {
      throw new IllegalArgumentException();
    }
    return searchIndex(index);
  }

  public List<List<String>> searchAll() {
    for (List<String> row : this.data) {
      List<String> lowerCase = row.stream().map(String::toLowerCase).toList();
      if (lowerCase.contains(this.value)) {
        this.result.add(row);
      }
    }
    return this.result;
  }
}
