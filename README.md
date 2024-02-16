> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details
Project Name: Server
Project Description: 
A server that handles requests to load data from either a CSV file or from the American Community Survey.
Team Members: Michael Rostom, Sun Joo Park
cs-login: mrostom, spark282
Link to Repo: https://github.com/cs0320-s24/server-spark282-mrostom

# Design Choices
### CSV Tools
- The `CSVParser` class uses a strategy pattern - it returns a generic object `T`, which was a `List<List<String>>` for this project.
- The `Search` class contains methods that enables search based on the given index, header (column name), or none (search all).


### Server
- The server has 4 end points: 'loadcsv', 'viewcsv', 'searchcsv', and 'broadband'. 
- We used data source interfaces (`CSVDataSourceInterface` for CSV data and `APIDataSourceInterface` for ACS data) so that all necessary handlers had access to important methods like `getData()`. 
- Through dependency injection, handlers were able to share necessary information in the instance of a data source to retrieve data.

### Caching
- As a proxy class, `CachingProxy` wraps `APIDataSourceInterface` and uses Guava to cache.
- It takes in an `APIDataSourceInterface` and reduces the amount of API requests that `getData()` makes, by returning the cached data in the class.

To change the caching expiry time, you can change the variable at the top of server to what you want. Making it 0 if you don't want anything to be cached.
### Other
- To resolve the issue of the query having spaces in between words, we made a design choice to use underscores (`_`) as spaces in the queries and then remove them later in the handlers.

# Errors/Bugs
### Backslashes in Json response: 
When the user tries to view a data file loaded via 'loadcsv' or data from the api, the JSON response includes `\` to escape quotation marks in the data.
This is happening because the JSON thinks the data is a String rather than a List<List<String>>
# Tests

# How to
### CSV
To use the server with a CSV file, run the server and then send a query to the endpoint 'loadcsv', the query should contain:
- **filepath parameter**: file path to the file you want to use.
- **header parameter**: indicates whether the file includes a header line. "true" indicates that it does. "false" if it doesn't.
  If not header parameter is provided, it defaults to "true".

Next, you can use either viewcsv' or 'searchcsv' depending on your need. Attempting to use either of these endpoints without calling loadcsv will cause the server to return an error
'viewcsv' needs no parameters are needed. The server will return the loaded file.
'searchcsv' needs:
- **value parameter**: this is the value that you are searching for.
  To specify the type of search, provide:
- **index parameter**: provide this with a valid index number to perform the search by index on the data.
- **header parameter**: provide this with a valid header name to perform the search by header name on the data.
  if neither of these parameters are provided, the search will be performed on all the dataset.
  If both index and header parameter is provided, the search will be performed on the index number.

### ACS API
To use the server with the ACS API, run the server and then send a query to the endpoint 'broadband', the query should contain:
- **state parameter**: the state that you want to search for.
- **county parameter**: the county that you want to search for.

If you want to change the caching expiry time, you can change the variable at the top of server to what you want. Making it 0 if you don't want anything to be cached.
