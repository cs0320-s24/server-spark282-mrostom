package edu.brown.cs.student.main.Server;

import static spark.Spark.after;

import java.io.IOException;
import spark.Spark;

public class Server {

  public int port = 3232;
  private static final DataSource state = new CSVDataSource();

  public Server()
      throws IOException,
          src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException {
    // Set up SparkJava Server
    Spark.port(this.port);

    // Setting CORS headers
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
    //  CSVDataSource csv = new CSVDataSource();
    // Listen on load, view, and search endpoints
    Spark.get("loadcsv", new LoadCSVHandler(state));
    Spark.get("viewcsv", new ViewCSVHandler(state));
    Spark.get("searchcsv", new SearchCSVHandler());

    // Wait until server starts
    Spark.awaitInitialization();
  }

  public static void main(String[] args)
      throws IOException,
          src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException {
    Server server = new Server();
    System.out.println("Server started; exiting main...");
  }
}
