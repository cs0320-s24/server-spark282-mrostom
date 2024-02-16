package edu.brown.cs.student.main.server.handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import edu.brown.cs.student.main.csvtools.Search;
import edu.brown.cs.student.main.server.datasources.CSVDataSourceInterface;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {

  Moshi moshi = new Moshi.Builder().build();
  Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
  JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
  Map<String, Object> responseMap = new HashMap<>();
  private String searchType;
  public CSVDataSourceInterface CSVDataSourceInterface;
  public String value;
  public String header;
  public String indexString;
  public int index;
  public List<String> headerRow;
  public List<List<String>> result;

  public SearchCSVHandler(CSVDataSourceInterface CSVDataSourceInterface) {
    this.CSVDataSourceInterface = CSVDataSourceInterface;
  }

  @Override
  public Object handle(Request request, Response response)
      throws IOException, FactoryFailureException {
    this.headerRow = this.CSVDataSourceInterface.getHeaderRow();
    this.indexString = request.queryParams("index"); // tODO: handle non integer
    this.header = request.queryParams("header");
    this.value = request.queryParams("value");
    List<List<String>> data = this.CSVDataSourceInterface.getData();
    Search searcher = new Search(this.value, data, this.headerRow);
    if (this.value == null) {
      responseMap.put("error_type", "missing_parameter");
      responseMap.put("error_arg", "empty path name");
      return adapter.toJson(responseMap);
    }

    if (this.indexString != null) {

      this.index = Integer.parseInt(this.indexString);
      this.result = searcher.searchIndex(this.index);
    } else if (this.header != null) {
      this.result = searcher.searchColumn(this.header);
    } else {
      this.result = searcher.searchAll();
    }

    System.out.println(this.result);

    // Response returns okay for loading properly
    responseMap.put("type", "success");
    responseMap.put("view", "successful");
    return adapter.toJson(responseMap);
  }
}
