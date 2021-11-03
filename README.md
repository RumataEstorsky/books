# Finch/Finagle based sample api project

This project create Rest API webserver and serves one endpoit,
which allows you to search book by author and publishing year(s).
Author name is mandatory, year is optional, you can specify more than one year.


## How to run?
1. You need key to access [New York Times API](https://developer.nytimes.com/get-started)
2. You can specify obtained key either in [application.conf](src/main/resources/application.conf) -it's dangerous,
   you do not want to keep secrets under Git!
   Or set it as OS evn variable (for example on your laptop or in docker container evn) 
3. Build docker image locally, `sbt docker:publishLocal`
4. Run it `docker run --network=host --env BOOKS_NYTIMES_API_KEY=<API key obtained on step 2> books:0.1.0-SNAPSHOT -p 8081`
   you also can overwrite port and other parameters defined in `application.conf`
   NB: when you run docker command remember that docker mind order of arguments in command line
5. On your local machine you can check service using
```
curl --request GET --url 'http://localhost:8081/me/books/list?year=2018&year=2017&author=Stephen%20King'
```

## To generate code coverage report

You need to run `sbt clean coverage test coverageReport` 
and for next normal build you need to clean project `sbt clean`


