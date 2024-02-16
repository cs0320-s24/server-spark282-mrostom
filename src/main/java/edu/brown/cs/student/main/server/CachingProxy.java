package edu.brown.cs.student.main.server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import edu.brown.cs.student.main.server.datasources.APIDataSourceInterface;
import edu.brown.cs.student.main.server.handlers.DatasourceException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CachingProxy implements APIDataSourceInterface {

    private final APIDataSourceInterface broadband;
    private final LoadingCache<String, String> cache;

    public CachingProxy(APIDataSourceInterface broadband){
        this.broadband = broadband;
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @NotNull
                    @Override
                    public String load(@NotNull String key) throws Exception {
                        return broadband.operation();
                    }
                });
    }
    @Override
    public List<List<String>> getData(String State, String County) throws IOException, FactoryFailureException, DatasourceException {
        return null; // TODO: What to return here
    }

    @Override
    public static String operation() throws ExecutionException {
        try{
            return cache.get("key"); // TODO: What should be the key
        } catch (Exception e) {
            throw new RuntimeException("Cannot get cache", e);
        }
    }
}
