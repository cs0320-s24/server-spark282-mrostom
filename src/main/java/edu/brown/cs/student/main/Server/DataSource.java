package edu.brown.cs.student.main.Server;

import java.io.IOException;
import java.util.List;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

public interface DataSource {
  List<List<String>> getData() throws IOException, FactoryFailureException;
//  List<List<String>> getData(String fileName) throws IOException, FactoryFailureException;
}
