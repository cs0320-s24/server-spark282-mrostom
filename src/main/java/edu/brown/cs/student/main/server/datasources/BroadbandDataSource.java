package edu.brown.cs.student.main.server.datasources;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.DatasourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import okio.Buffer;

/**
 * A class that implements APIDataSourceInterface. Specifically for importing Api data from ACS.
 */
public class BroadbandDataSource implements APIDataSourceInterface {

  private List<List<String>> stateList;

  /**
   * connects to the url.
   *
   * @param requestUrl the url to connect to
   * @return HttpURLConnection: connection to server
   * @throws DatasourceException if it wasn't able to open a connection
   * @throws IOException         if the server didn't respond correctly
   */
  private static HttpURLConnection connect(URL requestUrl) throws DatasourceException, IOException {
    URLConnection urlConnection = requestUrl.openConnection();
    if (!(urlConnection instanceof HttpURLConnection)) {
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    }
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200) {
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    }
    return clientConnection;
  }

  /**
   * makes a call to the api to fetch list of state codes.
   *
   * @return a hashmap with state names as keys and their ids as values
   * @throws DatasourceException if the Urls is malformed, or the census server returned malformed
   *                             data
   */
  private HashMap<String, String> fetchStateData() throws DatasourceException {
    final HashMap<String, String> stateMap = new HashMap<>();
    try {
      URL requestUrl =
          new URL(
              "https",
              "api.census.gov",
              "/data/2010/dec/sf1?get=NAME"
                  + "&key=22c0172d3498ce90376946c16dea94d407fe76fb"
                  + "&for=state:*");
      this.stateList = fetch(requestUrl);
      for (List<String> row : stateList) { // TODO: check if row has 2 items
        stateMap.put(row.get(0), row.get(1));
      }
      return stateMap;
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  /**
   * makes a call to the api to fetch list of county codes.
   *
   * @return a hashmap with county names as keys and their ids as values
   * @throws DatasourceException if the Urls is malformed, or the census server returned malformed
   *                             data
   */
  private HashMap<String, String> fetchCountyData(String stateId)
      throws DatasourceException {
    List<List<String>> countyList;
    HashMap<String, String> countyMap = new HashMap<>();
    try {
      URL requestUrl =
          new URL(
              "https",
              "api.census.gov",
              "/data/2010/dec/sf1?get=NAME"
                  + "&key=22c0172d3498ce90376946c16dea94d407fe76fb"
                  + "&for=county:*"
                  + "&for=state:"
                  + stateId);
      countyList = fetch(requestUrl);
      for (List<String> row : countyList) { // TODO: check if row has 2 items
        countyMap.put(row.get(0), row.get(2));
      }
      return countyMap;
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  /**
   * makes a call to the api to fetch broadband data.
   *
   * @param stateId  the id that represents the state
   * @param countyId the id that represents the county
   * @return a percentage of households that have internet according to the data
   * @throws DatasourceException if the Urls is malformed, or the census server returned malformed
   *                             data
   */
  private String fetchBroadbandData(String stateId, String countyId)
      throws DatasourceException {
    List<List<String>> broadbandList;
    try {
      URL requestUrl =
          new URL(
              "https",
              "api.census.gov",
              "data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E"
                  + "&key=22c0172d3498ce90376946c16dea94d407fe76fb"
                  + "&for=county:"
                  + countyId
                  + "&in=state:"
                  + stateId);
      broadbandList = fetch(requestUrl);
      String result = broadbandList.get(1).get(1); // TODO: if empty return error to user
      System.out.println(result);
      return result;
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  @Override
  public List<List<String>> getData(String state, String county) throws DatasourceException {
    HashMap<String, String> stateIdMap = new HashMap<>();
    if (stateList == null) {
      // not cached
      stateIdMap = fetchStateData();
    }
    String stateId = stateIdMap.get(state);
    HashMap<String, String> countyIdMap = fetchCountyData(stateId);
    String countyId = countyIdMap.get(county + ", " + state);

    LocalDate currentTime = LocalDate.now(); // TODO: return time
    System.out.println(currentTime);
    String percentage = fetchBroadbandData(stateId, countyId);
    System.out.print(percentage);
    return List.of(List.of("hi"));
  }

  /**
   * Makes call to URL and returns JSON response formatted as List of List of String.
   *
   * @param requestUrl URL to make call to(usually involves parameters)
   * @return JSON response from server formatted as  List of List of String
   * @throws DatasourceException if the server response was malformed
   */
  private List<List<String>> fetch(URL requestUrl) throws DatasourceException {
    try {
      HttpURLConnection clientConnection = connect(requestUrl);
      Moshi moshi = new Builder().build();
      Type listType = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);
      // NOTE: important! pattern for handling the input stream
      List<List<String>> responseList = adapter.fromJson(
          new Buffer().readFrom(clientConnection.getInputStream()));
      if (responseList == null) {
        throw new DatasourceException("Malformed response from Census");
      }
      clientConnection.disconnect();
      return responseList;
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }
}
