package edu.brown.cs.student.main.Server.DataSources;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.Handlers.DatasourceException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import okio.Buffer;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

public class BroadbandDataSource implements APIDataSourceInterface {

  public static List<List<String>> stateList;
  public static HashMap<String, String> stateMap;

  private static HashMap<String, String> fetchStateData() throws DatasourceException {
    try {
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2010/dec/sf1?get=NAME&key=22c0172d3498ce90376946c16dea94d407fe76fb&for=state:*");
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type listType = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);
      // NOTE: important! pattern for handling the input stream
      stateList = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      if (stateList == null) {
        throw new DatasourceException("Malformed response from Census");
      }
      clientConnection.disconnect();
      stateMap = new HashMap<>();
      for (List<String> row : stateList) { // TODO: check if row has 2 items
        stateMap.put(row.get(0), row.get(1));
      }

      return stateMap;
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
  }

  public static List<List<String>> countyList;
  public static HashMap<String, String> countyMap;

  private static HashMap<String, String> fetchCountyData(String stateID)
      throws DatasourceException {
    try {
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "/data/2010/dec/sf1?get=NAME&key=22c0172d3498ce90376946c16dea94d407fe76fb&for=county:*&for=state:"
                  + stateID);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type listType = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);
      // NOTE: important! pattern for handling the input stream
      countyList = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      countyMap = new HashMap<>();
      if (countyList == null) {
        throw new DatasourceException("Malformed response from Census");
      }
      clientConnection.disconnect();
      for (List<String> row : countyList) { // TODO: check if row has 2 items
        countyMap.put(row.get(0), row.get(2));
      }
      return countyMap;
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
    // TODO: maybe merge the two methods into one later
  }

  @Override
  public List<List<String>> getData(String state, String county)
      throws IOException, FactoryFailureException, DatasourceException {
    HashMap<String, String> stateCodes = new HashMap<>();
    if (stateList == null) {
      stateCodes = fetchStateData();
    }
    String stateId = stateCodes.get(state);

    HashMap<String, String> countyCodes = new HashMap<>();
    countyCodes = fetchCountyData(stateId);
    String countyId = countyCodes.get(county + ", " + state);

    LocalDate currentTime = LocalDate.now(); // Data Object
    System.out.println(currentTime);
    String percentage = fetchBroadbandData(stateId, countyId);
    System.out.print(percentage);

    return List.of(List.of("hi"));
  }

  public static List<List<String>> broadbandList;

  private static String fetchBroadbandData(String stateID, String countyID)
      throws DatasourceException {
    try {
      URL requestURL =
          new URL(
              "https",
              "api.census.gov",
              "data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&key=22c0172d3498ce90376946c16dea94d407fe76fb&for=county:"
                  + countyID
                  + "&in=state:"
                  + stateID);
      HttpURLConnection clientConnection = connect(requestURL);
      Moshi moshi = new Moshi.Builder().build();
      Type listType = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);
      // NOTE: important! pattern for handling the input stream
      broadbandList = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
      if (broadbandList == null) {
        throw new DatasourceException("Malformed response from Census");
      }
      clientConnection.disconnect();
      String result = broadbandList.get(1).get(1); // TODO: if empty return error to user
      System.out.println(result);
      return result;
    } catch (IOException e) {
      throw new DatasourceException(e.getMessage());
    }
    // TODO: maybe merge the two methods into one later
  }

  private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
    URLConnection urlConnection = requestURL.openConnection();
    if (!(urlConnection instanceof HttpURLConnection))
      throw new DatasourceException("unexpected: result of connection wasn't HTTP");
    HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
    clientConnection.connect(); // GET
    if (clientConnection.getResponseCode() != 200)
      throw new DatasourceException(
          "unexpected: API connection not success status " + clientConnection.getResponseMessage());
    return clientConnection;
  }
}
