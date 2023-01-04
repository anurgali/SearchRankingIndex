package io.perpetua.searchrankingindex.business;

import io.perpetua.searchrankingindex.dto.Rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Analytics {

    public Analytics(){
        init();
    }

    /**
     * a time series containing the individual ranks for an ASIN, for a certain keyword
     *
     * @param keyword
     * @param asin
     * @return
     */
    public List<Rank> getIndividualRanksByKeyword(String keyword, String asin) {
        List<Rank> result = new ArrayList<>();
        List<Rank> ranks = Utils.getKeywordsToRankMap().get(keyword);
        if (ranks == null) {
            return result;
        }
        result = ranks.stream()
                .filter(rank -> rank.getAsin().equals(asin))
                .collect(Collectors.toList());
        Collections.sort(result); // sorting chronologically
        return result;
    }

    public List<Rank> getAggregatedRanks(By strategy, String criteria){
        List<Rank> result = new ArrayList<>();
        Map<String, List<Rank>> map = null;
        switch (strategy){
            case KEYWORD -> map = Utils.getKeywordsToRankMap();
            case ASIN -> map = Utils.getAsinToRankMap();
        }
        List<Rank> ranks = map.get(criteria);
        if (ranks == null) {
            return result;
        }
        Map<Long, List<Rank>> ranksGrouped = ranks.parallelStream().collect(Collectors.groupingByConcurrent(rank -> rank.getTimestamp()));
        for (Long timestamp : ranksGrouped.keySet()) {
            List<Rank> timestampRanks = ranksGrouped.get(timestamp);
            Rank avgRank = null;
            switch (strategy){
                case KEYWORD -> avgRank =  new Rank(timestamp, "ALL", criteria, -1);
                case ASIN -> avgRank =  new Rank(timestamp, criteria, "ALL", -1);
            }
            for (Rank r: timestampRanks) {
                avgRank.setRank(calculateRollingAVG(avgRank, r));
            }
            result.add(avgRank);
        }
        Collections.sort(result); // sorting chronologically
        return result;
    }

    private static int calculateRollingAVG(Rank avgRank, Rank r) {
        if (avgRank.getRank()==-1){
            avgRank.setRank(r.getRank());
            return r.getRank();
        }
        return (avgRank.getRank() + r.getRank()) / 2;
    }

    private void init(){
        if (Utils.getKeywordsToRankMap().isEmpty()) {
            Utils.downloadFromS3();
            Utils.parseCSV();
        }
    }
}
