package edu.brown.cs32;

import edu.brown.cs.student.main.Server.Server;
import edu.brown.cs.student.main.Server.datasources.APIDataSourceInterface;
import edu.brown.cs.student.main.Server.datasources.BroadbandDataSource;
import edu.brown.cs.student.main.Server.handlers.BroadbandHandler;
import edu.brown.cs32.mocks.MockedBroadbandSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import spark.Spark;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {

  @BeforeAll
  static void setUpOnce() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }
  // Helping Moshi serialize Json responses; see the gearup for more info.
  // NOTE WELL: THE TYPES GIVEN HERE WOULD VARY ANYTIME THE RESPONSE TYPE VARIES
  // We are testing an API that returns Map<String, Object>
  // It would be different if the response was, e.g., List<List<String>>.
  private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;
  private JsonAdapter<List<List<String>>> weatherDataAdapter;

  @BeforeEach
  public void setup() {
    // Re-initialize parser, state, etc. for every test method

    // Use *MOCKED* data when in this test environment.
    // Notice that the WeatherHandler code doesn't need to care whether it has
    // "real" data or "fake" data. Good separation of concerns enables better testing.
    APIDataSourceInterface mockedSource = new MockedBroadbandSource(List.of(List.of("hello")));
    Spark.get("/broadband", new BroadbandHandler(mockedSource));
    Spark.awaitInitialization(); // don't continue until the server is listening

    // New Moshi adapter for responses (and requests, too; see a few lines below)
    //   For more on this, see the Server gearup.
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    Type type = Types.newParameterizedType(List.class, Types.newParameterizedType(List.class, String.class));
    weatherDataAdapter = moshi.adapter(type);
  }

  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }


  @Test
  public void testCSV()
  {

  }

  @Test
  public void testAPI() {
    APIDataSourceInterface broadband = new BroadbandDataSource();
    Server server = new Server(broadband);

  }

  @Test
  public void testCaching() {
    Server server = new Server();
  }
}

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * The "throws" clause doesn't matter below -- JUnit will fail if an
   *     exception is thrown that hasn't been declared as a parameter to @Test.
   *
   * @param apiCall the call string, including endpoint
   *                (Note: this would be better if it had more structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }
  @Test
  public void testWeatherRequestSuccess() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    // Set up the request, make the request
    HttpURLConnection loadConnection = tryRequest("weather?"+providence.toOurServerParams());
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("success", responseBody.get("type"));

    // Mocked data: correct temp? We know what it is, because we mocked.
    assertEquals(
            weatherDataAdapter.toJson(new WeatherData(20.0)),
            responseBody.get("temperature"));
    // Notice we had to do something strange above, because the map is
    // from String to *Object*. Awkward testing caused by poor API design...

    loadConnection.disconnect();
  }

  @Test
  public void testWeatherRequestFail_missing() throws IOException {
    // Setup without any parameters (oops!)
    HttpURLConnection loadConnection = tryRequest("weather");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());

    // Get the expected response: an error
    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error", responseBody.get("type"));
    loadConnection.disconnect(); // close gracefully
  }

  @Test
  public void testWeatherRequestFail_badcoords() throws IOException {
    // Setup with bad parameters (oops)
    HttpURLConnection loadConnection = tryRequest("weather?lat=1000&lon=1000");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());

    // Get the expected response: an error
    Map<String, Object> responseBody = adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(responseBody);
    assertEquals("error", responseBody.get("type"));
    loadConnection.disconnect(); // close gracefully
  }



  /**
   * Helper to make working with a large test suite easier: if an error, print more info.
   * @param body
   */
  private void showDetailsIfError(Map<String, Object> body) {
    if(body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }


}
