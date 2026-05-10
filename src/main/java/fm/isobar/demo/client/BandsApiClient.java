package fm.isobar.demo.client;

import fm.isobar.demo.model.Band;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class BandsApiClient {

    private final RestTemplate restTemplate;


    @Value("${bands.api.url}")
    private String bandsApiUrl;


    public BandsApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Performs a GET request to the bands api endpoint and deserializes the JSON array.
     *
     * @return list of all bands
     * @throws RestClientException if the request fails for any reason
     */
    public List<Band> fetchAllBands() {
        log.info("Fetching all bands from url: {}", bandsApiUrl);
        try {
            ResponseEntity<List<Band>> response = restTemplate.exchange(
                    bandsApiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            List<Band> bands = response.getBody();

            log.info("Received a total of {} bands from the api", bands != null ? bands.size() : 0);

            return bands;
        } catch (RestClientException e) {
            throw new RestClientException("Failed to fetch bands from " + bandsApiUrl, e);
        }
    }

}
