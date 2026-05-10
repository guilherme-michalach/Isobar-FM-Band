package fm.isobar.band.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BandApiResponse<T>(Instant timestamp, int status, T data) {

    public static <T> BandApiResponse<T> ok(T data) {
        return new BandApiResponse<>(Instant.now(), 200, data);
    }
}
