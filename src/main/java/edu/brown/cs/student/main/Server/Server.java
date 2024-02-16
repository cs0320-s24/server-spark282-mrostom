package edu.brown.cs.student.main.Server;

import static spark.Spark.after;

import edu.brown.cs.student.main.Server.datasources.*;
import edu.brown.cs.student.main.Server.handlers.BroadbandHandler;
import edu.brown.cs.student.main.Server.handlers.LoadCSVHandler;
import edu.brown.cs.student.main.Server.handlers.SearchCSVHandler;
import edu.brown.cs.student.main.Server.handlers.ViewCSVHandler;
import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import spark.Spark;

/**
 * This is the main server class. Running this file will run the server on port 3232. The server
 * will handle the requests sent by the user.
 *
 * @author Michael Rostom, Sun Joo Park Endpoints: loadcsv: loads CSV file onto the server, must
 *     provide valid filepath parameter. viewcsv: returns loaded CSV file. Returns success when
 *     loaded correctly, causes an error if not. searchcsv: searches loaded CSV file for provided
 *     value (index, header name, all). Causes an error if no CSV file is loaded. broadband:
 *     searches the ACS 2021 data based on two parameters.
 */
public class Server {

  // This is the main port for the server
  public int port = 3232;

  // Instantiate data sources
  private static final CSVDataSourceInterface CSVSource = new CSVDataSource();
  private static final APIDataSourceInterface APISource = new BroadbandDataSource();
  private static final APIDataSourceInterface CachingProxy = new CachingProxy(APISource);

  /** Constructor for Server. */
  public Server() {
    // Set up SparkJava Server
    Spark.port(this.port);

    // Setting CORS headers
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Listen on load, view, search and broadband endpoints
    Spark.get("loadcsv", new LoadCSVHandler(CSVSource));
    Spark.get("viewcsv", new ViewCSVHandler(CSVSource));
    Spark.get("searchcsv", new SearchCSVHandler(CSVSource));
    Spark.get("broadband", new BroadbandHandler(CachingProxy));

    // Wait until server starts
    Spark.awaitInitialization();
  }

  /**
   * The main method. Generates a server and runs it.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args)
      throws IOException, FactoryFailureException, ExecutionException {
    Server server = new Server();
    System.out.println("Server started; exiting main...");
  }
}
