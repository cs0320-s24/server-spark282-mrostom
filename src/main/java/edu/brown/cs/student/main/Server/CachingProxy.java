package edu.brown.cs.student.main.Server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.Server.datasources.APIDataSourceInterface;
import edu.brown.cs.student.main.Server.handlers.DatasourceException;
import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class CachingProxy implements APIDataSourceInterface {

  private final LoadingCache<String, String> cache;
  private final APIDataSourceInterface broadband;

  public CachingProxy(APIDataSourceInterface broadband) {
    this.broadband = broadband;
    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(1)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(
                new CacheLoader<String, String>() {
                  @NotNull
                  @Override
                  public String load(@NotNull String key) throws Exception {
                    return broadband.getData();
                  }
                });
  }

  @Override
  public List<List<String>> getData(String State, String County)
      throws IOException, DatasourceException {
    return null; // TODO: What to return here
  }

  @Override
  public String operation() throws ExecutionException {

    try {
      return cache.get("key"); // TODO: What should be the key
    } catch (Exception e) {
      throw new RuntimeException("Cannot get cache", e);
    }
  }
}
