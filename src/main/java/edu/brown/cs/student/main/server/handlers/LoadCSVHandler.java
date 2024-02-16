package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import edu.brown.cs.student.main.server.datasources.CSVDataSourceInterface;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;


/**
 * LoadCSVHandler which handles requests to the loadcsv handler. It will load and parse the file
 */
public class LoadCSVHandler implements Route {


  // configure moshi to read JSON response
  Moshi moshi = new Moshi.Builder().build();
  Map<String, Object> responseMap = new HashMap<>();
  Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

  private final CSVDataSourceInterface CSVDataSource;

  /**
   * Constructor for the loadcsvHandler.
   *
   * @param CSVDataSource DataSource that implements the interface
   */
  public LoadCSVHandler(CSVDataSourceInterface CSVDataSource) {
    this.CSVDataSource = CSVDataSource;
  }

  /**
   * Will handle requests from server and parse the file.
   *
   * @param request  a spark Request
   * @param response a spark response
   * @return a responseMap with the response of the handler: type=success if the loading and parsing
   * is successful. error_type=* when there is an error
   */
  @Override
  public Object handle(Request request, Response response) {
    String fileName;
    boolean hasHeader;
    fileName = request.queryParams("filepath");
    hasHeader = Boolean.parseBoolean(request.queryParams("header"));
    if (fileName == null) {
      responseMap.put("error_type", "missing_parameter");
      responseMap.put("error_arg", "empty path name");
      return adapter.toJson(responseMap);
    }
    try {
      this.CSVDataSource.parseData(fileName, hasHeader);

      // Response returns okay for loading properly
      responseMap.put("type", "success");
      responseMap.put("view", "successful");
    } catch (IOException e) {
      responseMap.put("error_type", "fileName_error");
      responseMap.put("error_arg", "cannot load file");
    } catch (FactoryFailureException e) {
      responseMap.put("error_type", "data_error");
      responseMap.put("error_arg", "malformed data");
    }
    return adapter.toJson(responseMap);

  }
}
