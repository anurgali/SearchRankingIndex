package io.perpetua.searchrankingindex.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Rank implements Comparable<Rank>{

    private long timestamp;
    private String asin;
    private String keyword;
    private int rank;


    @Override
    public int compareTo(Rank rank1) {
        if(timestamp==rank1.timestamp)
            return 0;
        else if(timestamp>rank1.timestamp)
            return 1;
        else
            return -1;
    }
}

