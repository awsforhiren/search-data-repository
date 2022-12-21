package com.techsmart.appscore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsmart.appscore.modal.SearchResult;
import com.techsmart.appscore.modal.SearchText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TextSearchService {
    @Value("classpath:sampleparagraph.txt")
    Resource resource;
    private final FileProcessorService fileProcessorService;

    @Autowired
    public TextSearchService(FileProcessorService fileProcessorService) {
        this.fileProcessorService = fileProcessorService;
    }

    public SearchResult searchTextCount(SearchText searchText) {
        return getSearchResult(searchText, getResource());
    }
    public ByteArrayInputStream topSearchText(int rowno) {
        ByteArrayInputStream byteArrayInputStream = getTopSearchText(rowno,getResource());
        return byteArrayInputStream;
    }

    public SearchResult getSearchResult(SearchText searchText, File file) {
        SearchResult searchResult = null;
        try {
            Map<String, Long> wordCountMap = fileProcessorService.readFiles(file);
            Map<String, Long> wordCountFilterMap = fileProcessorService.filterSearchText(searchText, wordCountMap);
            String wordCountJsonData = new ObjectMapper().writeValueAsString(wordCountFilterMap);
            searchResult = new SearchResult(wordCountJsonData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return searchResult;
    }
    public ByteArrayInputStream getTopSearchText(int rowno, File file) {
        List<String> fileDataList  = null;
        ByteArrayInputStream byteArrayInputStream;
        try {
            Map<String, Long> wordCountMap = fileProcessorService.readFiles(file);
            fileDataList = fileProcessorService.filterCountRows(wordCountMap, rowno);
            byteArrayInputStream = fileProcessorService.buildCSVFile(fileDataList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayInputStream;
    }
    private File getResource() {
        File file = null;
        try {
            file  = resource.getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
}
