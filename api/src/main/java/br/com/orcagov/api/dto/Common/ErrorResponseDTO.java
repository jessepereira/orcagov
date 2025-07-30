package br.com.orcagov.api.dto.Common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {

    private String error;
    private String message;
    private List<String> details;
    private String path;
    private Integer status;
    private LocalDateTime timestamp;
}