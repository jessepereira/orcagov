package br.com.orcagov.api.dto.Common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponseDTO<T> {

    private List<T> content;
    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
    private Integer size;
    private Boolean hasNext;
    private Boolean hasPrevious;
    private Boolean isFirst;
    private Boolean isLast;
}