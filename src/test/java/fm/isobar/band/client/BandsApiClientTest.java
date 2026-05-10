package fm.isobar.band.client;

import fm.isobar.band.config.AppConfig;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(components = BandsApiClient.class, includeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AppConfig.class))
@TestPropertySource(properties = {
        "bands.api.url=https://bands-api.vercel.app/api/bands",
        "bands.api.connection-timeout=1000",
        "bands.api.read-timeout=1000"
})
class BandsApiClientTest {

    @Autowired
    private BandsApiClient bandsApiClient;

    @Autowired
    private MockRestServiceServer server;

    @Nested
    @DisplayName("fetchAllBands()")
    class FetchAllBands {

        @Test
        @DisplayName("returns the deserialized list on a successful response")
        void returnsDeserializedBandsOnSuccess() {
            server.expect(requestTo("https://bands-api.vercel.app/api/bands"))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess(
                            """
                            [
                              {"id":"1","name":"Metallica","genre":"Metal","biography":"Thrash legends","numPlays":5000000,"albums":[]},
                              {"id":"2","name":"The Beatles","genre":"Rock","biography":"From Liverpool","numPlays":9000000,"albums":[]}
                            ]
                            """,
                            MediaType.APPLICATION_JSON));

            var result = bandsApiClient.fetchAllBands();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("Metallica");
            assertThat(result.get(1).name()).isEqualTo("The Beatles");
            server.verify();
        }

        @Test
        @DisplayName("returns an empty list when the API responds with an empty array")
        void returnsEmptyListWhenApiReturnsEmptyArray() {
            server.expect(requestTo("https://bands-api.vercel.app/api/bands"))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

            var result = bandsApiClient.fetchAllBands();

            assertThat(result).isEmpty();
            server.verify();
        }

        @Test
        @DisplayName("wraps server errors in a RestClientException")
        void wrapsServerErrorInRestClientException() {
            server.expect(requestTo("https://bands-api.vercel.app/api/bands"))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withServerError());

            assertThatThrownBy(() -> bandsApiClient.fetchAllBands())
                    .isInstanceOf(RestClientException.class)
                    .hasMessageContaining("Failed to fetch bands from https://bands-api.vercel.app/api/bands");
        }
    }
}