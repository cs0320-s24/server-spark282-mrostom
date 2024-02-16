package edu.brown.cs.student.main.Server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.datasources.APIDataSourceInterface;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * BroadbandHandler which handles requests to the loadcsv handler. It will load and parse the file
 */
public class BroadbandHandler implements Route {

  private final APIDataSourceInterface datasource;
  Moshi moshi = new Moshi.Builder().build();
  Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
  Map<String, Object> responseMap = new HashMap<>();

  /**
   * Constructor for the Broadband handler.
   *
   * @param datasource DataSource that implements the interface
   */
  public BroadbandHandler(APIDataSourceInterface datasource) {
    this.datasource = datasource;
  }

  /**
   * Will handle requests from server and parse the file.
   *
   * @param request a spark Request response – a spark response with parameter state and county
   * @param response a spark response
   * @return a responseMap with the response of the handler: type=success if the loading and parsing
   *     is successful. error_type=* when there is an error
   */
  @Override
  public Object handle(Request request, Response response) {
    String state;
    String county;
    state = request.queryParams("state");
    county = request.queryParams("county");
    if (state == null || county == null) {
      responseMap.put("error_type", "missing_parameter");
      responseMap.put("error_arg", "empty path name");
      return adapter.toJson(responseMap);
    }
    // replace _ with " " to allow entering two words in the query
    state = state.replaceAll("_", " ");
    county = county.replaceAll("_", " ");
    try {
      List<List<String>> data = datasource.getData(state, county);
      LocalDate currentTime = LocalDate.now();
      responseMap.put("type", "success");
      responseMap.put("data", data);
      responseMap.put("date and time", currentTime.toString());
    } catch (DatasourceException e) {
      responseMap.put("error_type", "data_handling_error");
      responseMap.put("error_arg", "malformed data");
    } catch (IOException e) {
      responseMap.put("error_type", "connection_error");
      responseMap.put("error_arg", "Couldn't open port connection");
    }
    return adapter.toJson(responseMap);
  }
}
