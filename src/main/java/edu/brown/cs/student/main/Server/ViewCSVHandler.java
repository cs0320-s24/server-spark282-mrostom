package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

public class ViewCSVHandler implements Route {
  DataSource dataSource;

  public ViewCSVHandler(DataSource dataSource) throws IOException, FactoryFailureException {
    this.dataSource = dataSource;
  }

  private final Moshi moshi = new Moshi.Builder().build();
  Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
  Map<String, Object> responseMap = new HashMap<>();

  @Override
  public Object handle(Request request, Response response)
      throws IOException, FactoryFailureException {
    // response returns okay for loading properly
    responseMap.put("type", "success");
    responseMap.put("view", "successful");
    System.out.println(this.dataSource.getData2());
    responseMap.put("data", this.dataSource.getData2());
    // call parser
    return adapter.toJson(responseMap);
  }
}
