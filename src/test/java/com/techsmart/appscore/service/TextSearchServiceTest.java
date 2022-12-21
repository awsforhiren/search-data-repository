package com.techsmart.appscore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsmart.appscore.modal.SearchResult;
import com.techsmart.appscore.modal.SearchText;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@Tag("UnitTest")
@DisplayName("Text Search Service Unit Tests")
class TextSearchServiceTest {

    TextSearchService textSearchService;

    @BeforeEach
    void init(){
        textSearchService = new TextSearchService();
    }

    @Test
    @DisplayName("Search Text Counting")
    public void whenSearchTextProvided_thencallsearchTextCount_returnMapwithCountedResult() throws IOException {
        Set<String> searchTextSet = Stream.of("Duis,Sed,Donec,Augue,Pellentesque,123".split(","))
                .collect(Collectors.toSet());
        SearchText searchText = SearchText.builder().searchText(searchTextSet).build();
        File file = new File("src/main/resources/sampleparagraph.txt");
        SearchResult searchResult = textSearchService.getSearchResult(searchText, file);
        LinkedHashMap wordCountFilterMap = new ObjectMapper().readValue(searchResult.getCounts(), LinkedHashMap.class);
        assertAll(
                () -> assertEquals(5,wordCountFilterMap.size()),
                () -> assertEquals(16,wordCountFilterMap.get("Sed")),
                () -> assertEquals(8,wordCountFilterMap.get("Donec")),
                () -> assertEquals(6,wordCountFilterMap.get("Pellentesque")),
                () -> assertEquals(11,wordCountFilterMap.get("Duis"))
        );
    }

    @Test
    @DisplayName("Search Top Records")
    public void whenTopRowProvided_thencallTopSearchText_returnTopRowsAsResult() throws IOException {
        File file = new File("src/main/resources/sampleparagraph.txt");
        ByteArrayInputStream byteArrayInputStream = textSearchService.getTopSearchText(3, file);
        BufferedReader br = new BufferedReader(new InputStreamReader(byteArrayInputStream));
        String line = null;
        int count = 0;
        while((line = br.readLine()) != null)
            count++;
        assertEquals(3, count);
    }
}