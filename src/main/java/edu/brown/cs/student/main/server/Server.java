package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import edu.brown.cs.student.main.server.datasources.APIDataSourceInterface;
import edu.brown.cs.student.main.server.datasources.BroadbandDataSource;
import edu.brown.cs.student.main.server.datasources.CSVDataSource;
import edu.brown.cs.student.main.server.datasources.CSVDataSourceInterface;
import edu.brown.cs.student.main.server.handlers.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.SearchCSVHandler;
import edu.brown.cs.student.main.server.handlers.ViewCSVHandler;

import java.io.IOException;
import spark.Spark;

/**
 * This is the main server class. Running this file will run the server on port 3232. The server
 * will handle the requests sent by the user.
 * Endpoints:
 * loadcsv: for loading a CSV file onto the server, must provide valid filepath parameter.
 * returns success when loading correctly or an error if it couldn't load it.
 * viewcsv: returns loaded CSV file. Causes error if no CSV file is loaded.
 * searchcsv: searches loaded CSV file for provided value. provide index parameter to search by
 * index. provide header name to search by header. If no parameter is provided, it will search the
 * whole CSV file. If provided both index and header name, it searches by index. Returns error if no
 * CSV file is loaded.
 * broadband: searches the ACS 2021 data based on two parameters
 */
public class Server {

  // This is the main port for the server
  public int port = 3232;
  private static final CSVDataSourceInterface CSVSource = new CSVDataSource();
  private static final APIDataSourceInterface APISource = new BroadbandDataSource();

  /**
   * Constructor for Server.
   * TODO: fix the throws
   *
   * @throws IOException
   * @throws FactoryFailureException
   *
   */
  public Server() throws IOException, FactoryFailureException {
    // Set up SparkJava Server.
    Spark.port(this.port);

    // Setting CORS headers
    after((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "*");
    });

    // Listen on load, view, search and broadband endpoints
    Spark.get("loadcsv", new LoadCSVHandler(CSVSource));
    Spark.get("viewcsv", new ViewCSVHandler(CSVSource));
    Spark.get("searchcsv", new SearchCSVHandler(CSVSource));
    Spark.get("broadband", new BroadbandHandler(APISource));

    // Wait until server starts
    Spark.awaitInitialization();
  }

  public static void main(String[] args) throws IOException, FactoryFailureException {
    Server server = new Server();
    System.out.println("Server started; exiting main...");
  }
}
