# Read Me

This is a simple spring boot application written on Java 17. Importing
to IDE should be straightforward. 
In order to launch it please set the environment variables for accessing AWS
S3: `accessKey` and `secretKey`.

The main class is available at: 
`io.perpetua.searchrankingindex.SearchRankingIndexApplication`

After launching the spring boot app (via IDE) you can test the 
relevant APIs using the following URLs in your browser:

`http://localhost:8080/api/individualranks?keyword=2012 f250 wheel hub&asin=B092SS35LK`

`http://localhost:8080/api/aggregatedranksbykeyword?keyword=2012 f250 wheel hub`

`http://localhost:8080/api/aggregatedranksbyasin?asin=B092SS35LK`

The main logic is in the `Analytics` class. `Utils` class does S3 
accessing and CSV parsing.

Disclaimer: the code, although simple, could be refactored infinitely,
for example if we assume the dataset to be larger than the heap size. 
The intention was to solve the exercise at the first place.   
