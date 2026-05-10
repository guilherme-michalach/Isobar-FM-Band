package fm.isobar.demo.controller;

import fm.isobar.demo.model.ApiResponse;
import fm.isobar.demo.model.Band;
import fm.isobar.demo.service.BandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(required = false) String genre
    ) {
        List<Band> bands = bandService.getAllBands();

        return ResponseEntity.ok(ApiResponse.ok(bands));
    }

}
