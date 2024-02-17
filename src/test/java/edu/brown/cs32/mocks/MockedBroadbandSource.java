package edu.brown.cs32.mocks;

import edu.brown.cs.student.main.Server.datasources.APIDataSourceInterface;
import edu.brown.cs.student.main.Server.handlers.DatasourceException;
import java.io.IOException;
import java.util.List;

/**
 * A datasource that never actually calls the NWS API, but always returns a constant weather-data
 * value. This is very useful in testing, and avoiding the costs of real API invocations. The
 * technique is called "mocking", as in "faking".
 */
public class MockedBroadbandSource implements APIDataSourceInterface {
  private final List<List<String>> constantData;

  public MockedBroadbandSource(List<List<String>> constantData) {
    this.constantData = constantData;
  }

  @Override
  public List<List<String>> getData(String State, String County)
      throws IOException, DatasourceException {
    return this.constantData;
  }
}
