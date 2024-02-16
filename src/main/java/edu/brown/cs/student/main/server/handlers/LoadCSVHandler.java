package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.datasources.CSVDataSourceInterface;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import edu.brown.cs.student.main.csvtools.FactoryFailureException;

public class LoadCSVHandler implements Route {

  Moshi moshi = new Moshi.Builder().build();
  Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
  Map<String, Object> responseMap = new HashMap<>();
  private String fileName;
  public List<List<String>> data;
  public CSVDataSourceInterface CSVDataSourceInterface;

  public LoadCSVHandler(CSVDataSourceInterface CSVDataSourceInterface) {
    this.CSVDataSourceInterface = CSVDataSourceInterface;
  }

  @Override
  public Object handle(Request request, Response response)
      throws IOException, FactoryFailureException, DatasourceException {
    this.fileName = request.queryParams("filepath");
    this.fileName = request.queryParams("header");
    if (this.fileName == null) {
      responseMap.put("error_type", "missing_parameter");
      responseMap.put("error_arg", "empty path name");
      return adapter.toJson(responseMap);
    }
    // TODO: query whether there is a handler to the file entered
    //   System.out.println(this.fileName); // TODO: remove this
    try {
      this.CSVDataSourceInterface.parseData(fileName, true);
    } catch (IOException e) {
      responseMap.put("error_type", "fileName_error");
      responseMap.put("error_arg", "cannot load file");
    }

    // Response returns okay for loading properly
    responseMap.put("type", "success");
    responseMap.put("view", "successful");
    return adapter.toJson(responseMap);
  }
}
