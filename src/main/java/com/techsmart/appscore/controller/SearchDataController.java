package com.techsmart.appscore.controller;

import com.techsmart.appscore.modal.SearchResult;
import com.techsmart.appscore.modal.SearchText;
import com.techsmart.appscore.service.TextSearchService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Optional;

@RestController
@RequestMapping("/counter-api/")
public class SearchDataController {
    private final TextSearchService textSearchService;

    @Value("${filename}")
    private final String filename = null;
    
    @Autowired
    public SearchDataController(TextSearchService textSearchService) {
        this.textSearchService = textSearchService;
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchTextCount(@RequestBody SearchText searchText) {
        Optional<SearchResult> searchResult = Optional.ofNullable(textSearchService.searchTextCount(searchText));
        return searchResult.isPresent()
                ? ResponseEntity.status(HttpStatus.OK).body(searchResult)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No search text count found.");
    }

    @GetMapping("/top/{rowcount}")
    public ResponseEntity<?> searchTextCount(@PathVariable("rowcount") @NonNull @Positive @Valid Integer rowcount) {
        InputStreamResource inputStreamResource = new InputStreamResource(textSearchService.topSearchText(rowcount));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(inputStreamResource);
    }
}
