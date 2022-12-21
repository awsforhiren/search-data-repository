package com.techsmart.appscore.service;

import com.techsmart.appscore.modal.FileData;
import com.techsmart.appscore.modal.SearchText;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileProcessorService {
    public static Map<String, Long> readFiles(File file) throws IOException {
        Map<String, Long> wordCountMap = Files.lines(file.toPath()) // read all the lines from the file
                .parallel() // parallel processing
                .flatMap(line -> Arrays.stream(line.trim().split(" "))) // split words on space
                .map(word -> word.replaceAll("[^a-zA-Z]", "").toLowerCase().trim()) //convert to lowercase
                .filter(word -> word.length() > 0) // filter only word with length greater than zero
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); //count words by grouping

        return wordCountMap;
    }

    public static Map<String, Long> filterSearchText(SearchText searchText, Map<String, Long> wordCountMap) {
        Map<String, Long> finalWordCountMap = new HashMap<>();

        if(Optional.ofNullable(wordCountMap).isPresent()) {
            Set<String> searchWordSet = searchText.getSearchText().stream()
                    .map(String::toLowerCase) //convert input search data to lowercase
                    .collect(Collectors.toSet());

            Map<String, Long> filterWordMap = wordCountMap.entrySet().stream()
                    .filter(word -> searchWordSet.contains(word.getKey())) //filter based on search text
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))  //sort the by count values
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u, LinkedHashMap::new));

            if (Optional.ofNullable(filterWordMap).isPresent()) {
                searchText.getSearchText().stream().filter(word -> {
                    if (filterWordMap.containsKey(word.toLowerCase())) //build final map using input keys
                        finalWordCountMap.put(word, filterWordMap.get(word.toLowerCase()));
                    return true;
                }).forEach(System.out::println);
            }
        }
        return finalWordCountMap;
    }

    public static List<String> filterCountRows(Map<String, Long> wordCountMap, int rowno) {
        List<String> fileDataList = new LinkedList<>();
        if (Optional.ofNullable(wordCountMap).isPresent()) {
            Map<String, Long> sortedFilteredMap = wordCountMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))  //sort the by count values
                    .limit(rowno) //Filter No of records
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u, LinkedHashMap::new));

            sortedFilteredMap.forEach((k, v) -> {
                FileData fileData = new FileData();
                fileData.setName(k);
                fileData.setCount(v);
                fileDataList.add(fileData.getName() + "|" + fileData.getCount());
            });
        }
        return fileDataList;
    }
    public static ByteArrayInputStream buildCSVFile(List<String> fileDataList) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if(Optional.ofNullable(fileDataList).isPresent()) {
                CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT);
                fileDataList.stream().forEach(str -> {
                    try {
                        csvPrinter.printRecord(str);
                        csvPrinter.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
