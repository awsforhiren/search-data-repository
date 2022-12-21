package com.techsmart.appscore.modal;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SearchResult implements Serializable {
    String counts;
}
