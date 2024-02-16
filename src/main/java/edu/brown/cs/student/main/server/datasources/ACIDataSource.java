package edu.brown.cs.student.main.server.datasources;

import edu.brown.cs.student.main.csvtools.FactoryFailureException;
import edu.brown.cs.student.main.server.handlers.DatasourceException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ACIDataSource implements APIDataSourceInterface {
    @Override
    public List<List<String>> getData(String State, String County) throws IOException, FactoryFailureException, DatasourceException {
        return null;
    }

    @Override
    public String operation() throws ExecutionException {
        return null;
    }
}
