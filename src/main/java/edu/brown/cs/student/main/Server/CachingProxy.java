package edu.brown.cs.student.main.Server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.Server.datasources.APIDataSourceInterface;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

public class CachingProxy implements APIDataSourceInterface {

  private final LoadingCache<String, List<List<String>>> cache;

  public CachingProxy(APIDataSourceInterface broadband) {
    this.cache =
        CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .recordStats()
            .build(
                new CacheLoader<>() {
                  @NotNull
                  public List<List<String>> load(@NotNull String key) throws Exception {
                    String[] parts = key.split(":"); // Split key to extract State and County
                    String state = parts[0];
                    String county = parts[1];
                    return broadband.getData(state, county);
                  }
                });
  }

  @Override
  public List<List<String>> getData(String State, String County) {
    String key = State + ":" + County; // Create key based on state and county
    System.out.println(cache.stats());
    return cache.getUnchecked(key);
  }
}
