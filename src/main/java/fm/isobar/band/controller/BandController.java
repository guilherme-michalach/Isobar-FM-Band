package fm.isobar.band.controller;

import fm.isobar.band.model.BandApiResponse;
import fm.isobar.band.model.Band;
import fm.isobar.band.model.SortOrder;
import fm.isobar.band.service.BandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bands")
@Tag(name = "Bands", description = "Browse, search and retrieve band information")
public class BandController {

    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @Operation(
            summary = "List or search bands",
            description = "Returns all bands. Optionally filter by name with `q` and/or sort the results with `sort`. " +
                    "If neither parameter is provided the full unsorted list is returned."
    )
    @ApiResponse(responseCode = "200", description = "Bands retrieved successfully")
    @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(schema = @Schema(hidden = true)))
    @GetMapping
    public ResponseEntity<BandApiResponse<List<Band>>> getBands(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) SortOrder sort) {

        List<Band> bands = (q != null && !q.isBlank() || sort != null)
                ? bandService.getBands(q, sort)
                : bandService.getAllBands();

        return ResponseEntity.ok(BandApiResponse.ok(bands));
    }

    @Operation(
            summary = "Get band by ID",
            description = "Returns a single band matching the given ID. The lookup is case-insensitive."
    )
    @ApiResponse(responseCode = "200", description = "Band found")
    @ApiResponse(responseCode = "404", description = "No band with the given ID exists", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(schema = @Schema(hidden = true)))
    @GetMapping("/{id}")
    public ResponseEntity<BandApiResponse<Band>> getBandById(@PathVariable String id) {
        return ResponseEntity.ok(BandApiResponse.ok(bandService.getBandById(id)));
    }

}
