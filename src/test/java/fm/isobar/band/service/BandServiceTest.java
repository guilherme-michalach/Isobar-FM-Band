package fm.isobar.band.service;

import fm.isobar.band.client.BandsApiClient;
import fm.isobar.band.exception.BandNotFoundException;
import fm.isobar.band.model.Band;
import fm.isobar.band.model.SortOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BandServiceTest {

    @MockitoBean
    private BandsApiClient bandsApiClient;

    @Autowired
    private BandService bandService;

    private List<Band> sampleBands;

    @BeforeEach
    void setUp() {
        sampleBands = List.of(
                new Band("1", "Metallica",    null, "Metal",       "Thrash legends",    5_000_000L, List.of("Master of Puppets")),
                new Band("2", "The Beatles",  null, "Rock",        "From Liverpool",    9_000_000L, List.of("Abbey Road")),
                new Band("3", "Radiohead",    null, "Alternative", "Art rock masters",  3_000_000L, List.of("OK Computer")),
                new Band("4", "Metal Church", null, "Metal",       "Classic US metal",  1_000_000L, List.of("The Dark"))
        );
        when(bandsApiClient.fetchAllBands()).thenReturn(sampleBands);
    }

    @Nested
    @DisplayName("getAllBands()")
    class GetAllBands {

        @Test
        @DisplayName("returns all bands from the API client")
        void returnsAllBands() {
            var result = bandService.getAllBands();

            assertThat(result).hasSize(4).containsExactlyElementsOf(sampleBands);
            verify(bandsApiClient, times(1)).fetchAllBands();
        }
    }

    @Nested
    @DisplayName("getBands(query, sort)")
    class GetBands {

        @Test
        @DisplayName("filters bands by name – case-insensitive partial match")
        void filtersByNameCaseInsensitive() {
            var result = bandService.getBands("metal", null);

            assertThat(result)
                    .extracting(Band::name)
                    .containsExactlyInAnyOrder("Metallica", "Metal Church");
        }

        @Test
        @DisplayName("returns empty list when no bands match the query")
        void returnsEmptyListWhenNoMatch() {
            var result = bandService.getBands("zzznomatch", null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns all bands when query is null")
        void returnsAllBandsForNullQuery() {
            var result = bandService.getBands(null, null);

            assertThat(result).hasSize(4);
        }

        @Test
        @DisplayName("returns all bands when query is blank")
        void returnsAllBandsForBlankQuery() {
            var result = bandService.getBands("   ", null);

            assertThat(result).hasSize(4);
        }

        @Test
        @DisplayName("sorts filtered results alphabetically")
        void sortsFilteredResultsAlphabetically() {
            var result = bandService.getBands("metal", SortOrder.ALPHABETICAL);

            assertThat(result)
                    .extracting(Band::name)
                    .containsExactly("Metal Church", "Metallica");
        }

        @Test
        @DisplayName("sorts filtered results by popularity (most played first)")
        void sortsFilteredResultsByPopularity() {
            var result = bandService.getBands("metal", SortOrder.POPULARITY);

            assertThat(result)
                    .extracting(Band::name)
                    .containsExactly("Metallica", "Metal Church");
        }
    }

    @Nested
    @DisplayName("getBandById(id)")
    class GetBandById {

        @Test
        @DisplayName("returns the band when a matching id is found")
        void returnsBandWhenFound() {
            var result = bandService.getBandById("1");

            assertThat(result.name()).isEqualTo("Metallica");
        }

        @Test
        @DisplayName("lookup is case-insensitive")
        void lookupIsCaseInsensitive() {
            var result = bandService.getBandById("2");

            assertThat(result.name()).isEqualTo("The Beatles");
        }

        @Test
        @DisplayName("throws BandNotFoundException when id is not found")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> bandService.getBandById("999"))
                    .isInstanceOf(BandNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("applySorting(bands, sort)")
    class ApplySorting {

        @Test
        @DisplayName("returns original list when sort is null")
        void returnsOriginalListWhenSortIsNull() {
            var result = bandService.applySorting(sampleBands, null);

            assertThat(result).containsExactlyElementsOf(sampleBands);
        }

        @Test
        @DisplayName("sorts all bands alphabetically ignoring case")
        void sortsAlphabeticallyIgnoringCase() {
            var result = bandService.applySorting(sampleBands, SortOrder.ALPHABETICAL);

            assertThat(result)
                    .extracting(Band::name)
                    .containsExactly("Metal Church", "Metallica", "Radiohead", "The Beatles");
        }

        @Test
        @DisplayName("sorts all bands by numPlays descending")
        void sortsByPopularityDescending() {
            var result = bandService.applySorting(sampleBands, SortOrder.POPULARITY);

            assertThat(result)
                    .extracting(Band::numPlays)
                    .containsExactly(9_000_000L, 5_000_000L, 3_000_000L, 1_000_000L);
        }

        @Test
        @DisplayName("returns empty list when input is empty")
        void handlesEmptyList() {
            var result = bandService.applySorting(List.of(), SortOrder.ALPHABETICAL);

            assertThat(result).isEmpty();
        }
    }
}