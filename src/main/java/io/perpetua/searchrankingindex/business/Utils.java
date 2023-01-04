package io.perpetua.searchrankingindex.business;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import io.perpetua.searchrankingindex.dto.Rank;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Utils {
    //establish connection to S3, download CSV, parse it and store in Rank objects map

    private static Map<String, List<Rank>> keywordsToRankMap = Collections.synchronizedMap(new HashMap<>());
    private static Map<String, List<Rank>> asinToRankMap = Collections.synchronizedMap(new HashMap<>());
    private static String[] HEADERS = { "timestamp", "asin", "keyword", "rank"};
    private static char delimeter = ';';


    public static void downloadFromS3(){
        File csv = new File("src/main/resources/case-keywords.csv");
        if (csv.exists()){
            return;
        }
        String accessKey=System.getenv("accessKey");
        String secretKey=System.getenv("secretKey");
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.EU_CENTRAL_1)
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(accessKey, secretKey))
                    )
                    .build();
            GetObjectRequest request = new GetObjectRequest("sellics-casestudy-organic", "public/case-keywords.csv");
            ObjectMetadata metadata = s3Client.getObject(request, csv);
            System.out.println("downloaded bytes: " + metadata.getContentLength());
        } catch (AmazonServiceException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void parseCSV() {
        try {
            Reader in = new FileReader("src/main/resources/case-keywords.csv");
            CSVFormat format = CSVFormat.newFormat(delimeter);
            Iterable<CSVRecord> records = CSVFormat.Builder.create(format).setHeader(HEADERS).setSkipHeaderRecord(true).build().parse(in);
            for (CSVRecord record : records) {
                String t = record.get("timestamp");
                String asin = record.get("asin");
                String keyword = record.get("keyword");
                String r = record.get("rank");
                Long timestamp = Long.parseLong(t);
                Rank rank = new Rank(timestamp, asin, keyword, Integer.parseInt(r));
                if (keywordsToRankMap.get(keyword) == null) {
                    List<Rank> list = new ArrayList<>();
                    list.add(rank);
                    keywordsToRankMap.put(keyword, list);
                } else {
                    keywordsToRankMap.get(keyword).add(rank);
                }
                if (asinToRankMap.get(asin) == null) {
                    List<Rank> list = new ArrayList<>();
                    list.add(rank);
                    asinToRankMap.put(asin, list);
                } else {
                    asinToRankMap.get(asin).add(rank);
                }
            }
            System.out.println("Completed parsing");
        } catch (IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static Map<String, List<Rank>> getKeywordsToRankMap() {
        return keywordsToRankMap;
    }

    public static Map<String, List<Rank>> getAsinToRankMap() {
        return asinToRankMap;
    }
}
