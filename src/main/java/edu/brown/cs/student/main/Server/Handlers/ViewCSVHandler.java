package edu.brown.cs.student.main.Server.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.CSVDataSourceInterface;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

public class ViewCSVHandler implements Route {
  CSVDataSourceInterface CSVDataSourceInterface;

  public ViewCSVHandler(CSVDataSourceInterface CSVDataSourceInterface)
      throws IOException, FactoryFailureException {
    this.CSVDataSourceInterface = CSVDataSourceInterface;
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
    System.out.println(this.CSVDataSourceInterface.getData2());
    responseMap.put("data", this.CSVDataSourceInterface.getData2());
    // call parser
    return adapter.toJson(responseMap);
  }
}
