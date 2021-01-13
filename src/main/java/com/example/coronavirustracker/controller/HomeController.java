package com.example.coronavirustracker.controller;

import com.example.coronavirustracker.models.LocationStats;
import com.example.coronavirustracker.servces.CoronavirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller  //Makes it a spring controller
public class HomeController {
    @Autowired
    CoronavirusDataService coronavirusDataService;

    @GetMapping("/")
   public String home(Model model){
       List<LocationStats> allStats = coronavirusDataService.getAllStats();
      int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum(); // taking list of objects, then converting to a stream and mapping to Integers, then sum it up
      int totalReportedNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum();
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalReportedNewCases", totalReportedNewCases);

        return "home";   /// retrieves html template
    }
}
