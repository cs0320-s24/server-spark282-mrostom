package edu.brown.cs.student.main.Server.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.DataSource;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
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
    public String state;
    public String county;
    @Override
    public Object handle(Request request, Response response)
            throws IOException, FactoryFailureException {
        this.state = request.queryParams("state");
        this.county = request.queryParams("county");
        if (this.state == null || this.county == null) {
            responseMap.put("error_type", "missing_parameter");
            responseMap.put("error_arg", "empty path name");
            return adapter.toJson(responseMap);
        }

        responseMap.put("type", "success");
        responseMap.put("view", "successful");
        return adapter.toJson(responseMap);
    }
}
