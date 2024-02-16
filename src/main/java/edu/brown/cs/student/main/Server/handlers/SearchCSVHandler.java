package edu.brown.cs.student.main.Server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.datasources.CSVDataSourceInterface;
import edu.brown.cs.student.main.csvtools.Search;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** SearchCSVHandler which handles requests to search CSV data if loaded. */
public class SearchCSVHandler implements Route {

  private final CSVDataSourceInterface CSVDataSource;

  /**
   * Constructor for the SearchCSVHandler.
   *
   * @param CSVDataSource DataSource that implements the interface
   */
  public SearchCSVHandler(CSVDataSourceInterface CSVDataSource) {
    this.CSVDataSource = CSVDataSource;
  }

  /**
   * Will handle requests to search the loaded csv file based on parameters.
   *
   * @param request a spark Request with value, index(optionally), header(optionally)
   * @param response a spark response
   * @return a responseMap with the data if successful
   */
  @Override
  public Object handle(Request request, Response response) {
    // configure moshi to read JSON response
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    List<List<String>> result;
    List<String> headerRow = this.CSVDataSource.getHeaderRow();
    String value = request.queryParams("value"); // value is the keyword to search for
    String indexString = request.queryParams("index");
    String header = request.queryParams("header");
    System.out.println(headerRow);
    List<List<String>> data = this.CSVDataSource.getData();
    if (value == null) {
      responseMap.put("error_type", "missing_parameter");
      responseMap.put("error_arg", "no value for search");
      return adapter.toJson(responseMap);
    }
    Search searcher = new Search(value, data, headerRow);
    if (indexString != null) {
      // searching by index
      try {
        int index = Integer.parseInt(indexString);
        result = searcher.searchIndex(index);
      } catch (NumberFormatException e) {
        responseMap.put("error_type", "Incorrect_Argument");
        responseMap.put("error_arg", "non integer in index parameter");
        return adapter.toJson(responseMap);
      } catch (IllegalArgumentException e) {
        responseMap.put("error_type", "Incorrect_Argument");
        responseMap.put("error_arg", "index out of bounds for data");
        return adapter.toJson(responseMap);
      }
    } else if (header != null) {
      // searching by header name
      if (!headerRow.isEmpty()) {
        try {
          result = searcher.searchColumn(header);
        } catch (IllegalArgumentException e) {
          responseMap.put("error_type", "Incorrect_Argument");
          responseMap.put("error_arg", "header name not found in data");
          return adapter.toJson(responseMap);
        }
      } else {
        responseMap.put("error_type", "Incorrect_Argument");
        responseMap.put("error_arg", "provided header but data without header");
        return adapter.toJson(responseMap);
      }
    } else {
      // searching all
      result = searcher.searchAll();
    }

    // Response returns okay for loading properly
    responseMap.put("type", "success");
    responseMap.put("data", result); // TODO: fix backslash in result
    return adapter.toJson(responseMap);
  }
}
