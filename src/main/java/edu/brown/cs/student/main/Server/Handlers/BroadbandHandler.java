package edu.brown.cs.student.main.Server.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.APIDataSourceInterface;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

public class BroadbandHandler implements Route {

  Moshi moshi = new Moshi.Builder().build();
  Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
  Map<String, Object> responseMap = new HashMap<>();
  public APIDataSourceInterface APIDataSourceInterface;

  public BroadbandHandler(APIDataSourceInterface APIDataSourceInterface) {
    this.APIDataSourceInterface = APIDataSourceInterface;
  }

  public String state;
  public String county;

  @Override
  public Object handle(Request request, Response response)
      throws IOException, FactoryFailureException, DatasourceException {
    this.state = request.queryParams("state").replaceAll("_", " ");
    this.county = request.queryParams("county").replaceAll("_", " ");
    if (this.state == null || this.county == null) {
      responseMap.put("error_type", "missing_parameter");
      responseMap.put("error_arg", "empty path name");
      return adapter.toJson(responseMap);
    }

    APIDataSourceInterface.getData(this.state, this.county);

    responseMap.put("type", "success");
    responseMap.put("view", "successful");
    return adapter.toJson(responseMap);
  }
}
