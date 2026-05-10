package fm.isobar.demo.controller;

import fm.isobar.demo.model.ApiResponse;
import fm.isobar.demo.model.Band;
import fm.isobar.demo.model.SortOrder;
import fm.isobar.demo.service.BandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bands")
public class BandController {

    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Band>>> getBands(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) SortOrder sort) {

        List<Band> bands = (q != null && !q.isBlank())
                ? bandService.getBands(q, sort)
                : bandService.getAllBands();

        return ResponseEntity.ok(ApiResponse.ok(bands));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Band>> getBandById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(bandService.getBandById(id)));
    }

}
