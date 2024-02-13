package edu.brown.cs.student.main.Server;

import static spark.Spark.after;

import spark.Spark;

public class Server {
  public int port = 3000;

  public Server() {
    Spark.port(this.port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    Spark.get("/Data/RI.csv", new LoadcsvHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  public static void main(String[] args) {
    // At time of creation, we decide on a specific datasource class:
    Server server = new Server();
    // Notice that this runs, but the program continues executing. Why
    // do you think that is? (We'll address this in a couple of weeks.)
    System.out.println("Server started; exiting main...");
  }
}
