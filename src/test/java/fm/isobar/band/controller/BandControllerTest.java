package fm.isobar.band.controller;

import fm.isobar.band.exception.BandNotFoundException;
import fm.isobar.band.model.Band;
import fm.isobar.band.model.SortOrder;
import fm.isobar.band.service.BandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BandController.class)
class BandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BandService bandService;

    private final Band metallica = new Band("1", "Metallica", null, "Metal", "Thrash legends", 5_000_000L, List.of("Master of Puppets"));
    private final Band beatles   = new Band("2", "The Beatles", null, "Rock", "From Liverpool", 9_000_000L, List.of("Abbey Road"));

    @Nested
    @DisplayName("GET /api/bands")
    class GetBands {

        @Test
        @DisplayName("returns all bands when no query param is present")
        void returnsAllBandsWithoutQuery() throws Exception {
            when(bandService.getAllBands()).thenReturn(List.of(metallica, beatles));

            mockMvc.perform(get("/api/bands").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data", hasSize(2)))
                    .andExpect(jsonPath("$.data[0].id").value("1"))
                    .andExpect(jsonPath("$.data[1].id").value("2"));

            verify(bandService).getAllBands();
            verify(bandService, never()).getBands(any(), any());
        }

        @Test
        @DisplayName("delegates to getBands() when query param 'q' is provided")
        void delegatesToGetBandsWhenQueryPresent() throws Exception {
            when(bandService.getBands("metal", null)).thenReturn(List.of(metallica));

            mockMvc.perform(get("/api/bands").param("q", "metal").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].name").value("Metallica"));

            verify(bandService).getBands("metal", null);
            verify(bandService, never()).getAllBands();
        }

        @Test
        @DisplayName("passes sort param through to getBands()")
        void passesSortParamToService() throws Exception {
            when(bandService.getBands("metal", SortOrder.ALPHABETICAL)).thenReturn(List.of(metallica));

            mockMvc.perform(get("/api/bands")
                            .param("q", "metal")
                            .param("sort", "ALPHABETICAL")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].name").value("Metallica"));

            verify(bandService).getBands("metal", SortOrder.ALPHABETICAL);
        }

        @Test
        @DisplayName("treats a blank 'q' param the same as no query (calls getAllBands)")
        void treatsBlankQueryAsAbsent() throws Exception {
            when(bandService.getAllBands()).thenReturn(List.of(metallica, beatles));

            mockMvc.perform(get("/api/bands").param("q", "   ").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(2)));

            verify(bandService).getAllBands();
            verify(bandService, never()).getBands(any(), any());
        }

        @Test
        @DisplayName("response envelope contains timestamp and status 200")
        void responseEnvelopeShape() throws Exception {
            when(bandService.getAllBands()).thenReturn(List.of());

            mockMvc.perform(get("/api/bands").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/bands/{id}")
    class GetBandById {

        @Test
        @DisplayName("returns the band when it exists")
        void returnsBandWhenFound() throws Exception {
            when(bandService.getBandById("1")).thenReturn(metallica);

            mockMvc.perform(get("/api/bands/1").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.id").value("1"))
                    .andExpect(jsonPath("$.data.name").value("Metallica"))
                    .andExpect(jsonPath("$.data.genre").value("Metal"));
        }

        @Test
        @DisplayName("returns 404 when band is not found")
        void returns404WhenBandNotFound() throws Exception {
            when(bandService.getBandById("999")).thenThrow(new BandNotFoundException("999"));

            mockMvc.perform(get("/api/bands/999").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Band not found with id: 999"));
        }
    }
}