package com.example.coronavirustracker.servces;


import com.example.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

//Service which calls the data and loads it

@Service   //Makes it a Spring service
public class CoronavirusDataService {
    private static String Virus_Data_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    //Method to make a HTTP call to the URL
    //After creating an instance of the class, using post-construct it will execute this method
    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")            //schedules the run of a method on a daily basis or rate,in this instance every second
    public void fetchVirusData() throws IOException, InterruptedException {
         List<LocationStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
       HttpRequest request =  HttpRequest.newBuilder()
                .uri(URI.create(Virus_Data_URL))   // get URI
               .build();                            //then build
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString()); //taking the body and returning as a String
        StringReader csvBodyReader = new StringReader(httpResponse.body());
//        System.out.println(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStat =  new LocationStats(); //initialising object for Location stats
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            int latestCases =  Integer.parseInt(record.get(record.size()-1));
            int previousDayCases =  Integer.parseInt(record.get(record.size()-2));
            locationStat.setLatestTotalCases(latestCases); //converting strings in the columns to int
            locationStat.setDiffFromPreviousDay(latestCases-previousDayCases);
//            System.out.println(locationStat);
            newStats.add(locationStat);

        }
        this.allStats = newStats;

    }
}
