package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSVParser.CSVParser;
import edu.brown.cs.student.main.CSVParser.CreateCensusData;
import edu.brown.cs.student.main.CSVParser.CreatorFromRow;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

/**
 * Handler class for the soup ordering API endpoint.
 *
 * <p>This endpoint is similar to the endpoint(s) you'll need to create for Sprint 2. It takes a
 * basic GET request with no Json body, and returns a Json object in reply. The responses are more
 * complex, but this should serve as a reference.
 */
// TODO 2: Check out this Handler. What does it do right now? How is the menu formed (deserialized)?
public class LoadcsvHandler implements Route {

  private final Moshi moshi = new Moshi.Builder().build();
  Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);

  JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
  Map<String, Object> responseMap = new HashMap<>();
  String fileName;

  @Override
  public Object handle(Request request, Response response)
      throws IOException, FactoryFailureException {
    this.fileName = request.queryParams("filepath");
    if (this.fileName == null) {
      responseMap.put("error_type", "missing_parameter");
      responseMap.put("error_arg", "empty path name");
      return adapter.toJson(responseMap);
    }
    System.out.println(this.fileName);
    // call parser
    Reader reader = new FileReader(this.fileName);
    CreatorFromRow creator = new CreateCensusData();
    CSVParser<List<String>> parser = new CSVParser<>(reader, creator);
    List<List<String>> data = parser.parse(true);
    System.out.println(data);

    // response returns okay for loading properly
    responseMap.put("type", "success");
    // call parser
    return adapter.toJson(responseMap);
  }
}
