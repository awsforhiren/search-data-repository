package com.techsmart.appscore.modal;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SearchText implements Serializable {
    @NonNull
    @NotEmpty
    private Set<String> searchText;
}
