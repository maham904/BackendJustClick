package com.suffolk.library_management.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;


@Builder
public record ApiResponse<T>(@JsonProperty("status") int status, @JsonProperty("message") String message,
                          @JsonProperty("data") T data) {
}
