package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

public class LoadCSVHandler implements Route {

  Moshi moshi = new Moshi.Builder().build();
  Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
  Map<String, Object> responseMap = new HashMap<>();
  private String fileName;
  public List<List<String>> data;
  public DataSource dataSource;

  public LoadCSVHandler(DataSource dataSource){
    this.dataSource = dataSource;
  }

  @Override
  public Object handle(Request request, Response response)
      throws IOException, FactoryFailureException {
    this.fileName = request.queryParams("filepath");
    if (this.fileName == null) {
      responseMap.put("error_type", "missing_parameter");
      responseMap.put("error_arg", "empty path name");
      return adapter.toJson(responseMap);
    }
    System.out.println(this.fileName); // TODO: remove this

    List<List<String>> data = this.dataSource.getData();

    // Response returns okay for loading properly
    responseMap.put("type", "success");
    responseMap.put("view", "successful");
    return adapter.toJson(responseMap);
  }

}