package edu.brown.cs.student.main.Server.Handlers;

import edu.brown.cs.student.main.Server.DataSource;
import src.main.java.edu.brown.cs.student.main.CSVParser.FactoryFailureException;

import java.io.IOException;
import java.util.List;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okio.Buffer;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class BroadbandDataSource implements DataSource {


    private static StateResponse resolveStateID(double lat, double lon) throws DatasourceException {
        try {
            URL requestURL = new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
            HttpURLConnection clientConnection = connect(requestURL);
            Moshi moshi = new Moshi.Builder().build();

            // NOTE WELL: THE TYPES GIVEN HERE WOULD VARY ANYTIME THE RESPONSE TYPE VARIES
            JsonAdapter<StateResponse> adapter = moshi.adapter(StateResponse.class).nonNull();
            // NOTE: important! pattern for handling the input stream
            StateResponse body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
            clientConnection.disconnect();
            if(body == null || body.state_id == null || body.State == null)
                throw new DatasourceException("Malformed response from NWS");
            return body;
        } catch(IOException e) {
            throw new DatasourceException(e.getMessage());
        }
    }
    @Override
    public List<List<String>> getData(String fileName) throws IOException, FactoryFailureException, DatasourceException {
        URL requestURL = new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
        HttpURLConnection clientConnection = connect(requestURL);
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ForecastResponse> adapter = moshi.adapter(ForecastResponse.class).nonNull();
        ForecastResponse body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

        clientConnection.disconnect();
    }

    @Override
    public List<List<String>> getData2() {
        return null;
    }

    public List<String> getHeaderRow() {
        return null;
    }


    private static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
        URLConnection urlConnection = requestURL.openConnection();
        if(! (urlConnection instanceof HttpURLConnection))
            throw new DatasourceException("unexpected: result of connection wasn't HTTP");
        HttpURLConnection clientConnection = (HttpURLConnection) urlConnection;
        clientConnection.connect(); // GET
        if(clientConnection.getResponseCode() != 200)
            throw new DatasourceException("unexpected: API connection not success status "+clientConnection.getResponseMessage());
        return clientConnection;
    }


    public record StateResponse(String State, String state_id){};
}
