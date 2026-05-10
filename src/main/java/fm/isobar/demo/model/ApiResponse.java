package fm.isobar.demo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(Instant timestamp, int status, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(Instant.now(), 200, data);
    }
}
