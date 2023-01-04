package io.perpetua.searchrankingindex.api;

import io.perpetua.searchrankingindex.business.Analytics;
import io.perpetua.searchrankingindex.business.By;
import io.perpetua.searchrankingindex.dto.Rank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SmartRankingController {



    @GetMapping ("/individualranks")
    public List<Rank> getIndividualRanks(@RequestParam (value="keyword") String keyword,
                                         @RequestParam (value="asin") String asin){
        Analytics analytics = new Analytics();
        return analytics.getIndividualRanksByKeyword(keyword, asin);
    }

    @GetMapping ("/aggregatedranksbykeyword")
    public List<Rank> getAggregatedRanks(@RequestParam (value="keyword") String keyword){
        Analytics analytics = new Analytics();
        return analytics.getAggregatedRanks(By.KEYWORD, keyword);
    }

    @GetMapping ("/aggregatedranksbyasin")
    public List<Rank> getAggregatedRanksByAsin(@RequestParam (value="asin") String asin){
        Analytics analytics = new Analytics();
        return analytics.getAggregatedRanks(By.ASIN, asin);
    }
}
