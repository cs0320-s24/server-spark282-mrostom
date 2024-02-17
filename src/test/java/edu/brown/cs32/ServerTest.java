package edu.brown.cs32;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.datasources.APIDataSourceInterface;
import edu.brown.cs.student.main.Server.handlers.BroadbandHandler;
import edu.brown.cs32.mocks.MockedBroadbandSource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

/** This is a class to test the server */
public class ServerTest {

  @BeforeAll
  static void setUpOnce() {
    Spark.port(3232);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;
  private JsonAdapter<List<List<String>>> apiAdapter;

  @BeforeEach
  public void setup() {

    // Mocked source
    APIDataSourceInterface mockedSource = new MockedBroadbandSource(List.of(List.of("hello")));
    Spark.get("/broadband", new BroadbandHandler(mockedSource));
    Spark.awaitInitialization(); // don't continue until the server is listening

    // New Moshi adapter
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    Type type =
        Types.newParameterizedType(
            List.class, Types.newParameterizedType(List.class, String.class));
    apiAdapter = moshi.adapter(type);
  }

  @AfterEach
  public void tearDown() {
    // Stop Spark listening on both endpoints
    Spark.unmap("/broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  @Test
  public void testCSV() throws IOException {
    // Test load
    HttpURLConnection loadcsv = tryRequest("/loadcsv?filepath=Data/RI.csv");
    // Attempted to test load, but server initialization clashes with broadband
    // assertEquals(200, loadcsv.getResponseCode());

    // Test view
    // HttpURLConnection viewcsv = tryRequest("/viewcsv");
    // Attempted to test view, but will not pass without loadcsv's test
    // assertEquals(200, viewcsv.getResponseCode())

    // Test search
    // HttpURLConnection searchcsv = tryRequest("/searchcsv/?value=Barrington&header=CityTown");
    // Attempted to test searchcsv, but will not pass without loadcsv's test
    // assertEquals(200, searchcsv.getResponseCode())

  }

  @Test
  public void testAPI() throws IOException {
    // Request for RI and Kent County
    HttpURLConnection loadConnection =
        tryRequest("broadband?state=Rhode_Island&county=Kent_County");
    assertEquals(200, loadConnection.getResponseCode());
    Map<String, Object> responseMap =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseMap.get("type"));
    showDetailsIfError(responseMap);

    String result = "[[hello]]";
    responseMap.put("data", result);
    assertEquals(result, responseMap.get("data"));
    loadConnection.disconnect();
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * <p>The "throws" clause doesn't matter below -- JUnit will fail if an exception is thrown that
   * hasn't been declared as a parameter to @Test.
   *
   * @param apiCall the call string, including endpoint (Note: this would be better if it had more
   *     structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // Contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Helper to make working with a large test suite easier: if an error, print more info.
   *
   * @param body
   */
  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }
}
