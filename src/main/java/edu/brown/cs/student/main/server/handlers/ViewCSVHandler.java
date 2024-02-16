package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.datasources.CSVDataSourceInterface;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** ViewCSVHandler which handles requests view CSV data if loaded. */
public class ViewCSVHandler implements Route {

  final CSVDataSourceInterface CSVDataSource;

  /**
   * Constructor for the viewCSVhandler.
   *
   * @param CSVDataSource DataSource that implements the interface
   */
  public ViewCSVHandler(CSVDataSourceInterface CSVDataSource) {
    this.CSVDataSource = CSVDataSource;
  }

  /**
   * Will handle requests from server and returns the loaded file.
   *
   * @param request a spark Request
   * @param response a spark response
   * @return a responseMap with the data if successful, with error otherwise
   */
  @Override
  public Object handle(Request request, Response response) {

    // configure moshi to read JSON response
    final Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();
    List<List<String>> data = this.CSVDataSource.getData();
    if (data.isEmpty()) {
      responseMap.put("error_type", "missing_csv");
      responseMap.put("error_arg", "no CSV file loaded");
    } else {
      // response returns okay for loading properly
      responseMap.put("data", data);
      responseMap.put("type", "success");
    }
    return adapter.toJson(responseMap);
  }
}
