package fm.isobar.demo.service;

import fm.isobar.demo.client.BandsApiClient;
import fm.isobar.demo.exception.BandNotFoundException;
import fm.isobar.demo.model.Band;
import fm.isobar.demo.model.SortOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BandService {

    private static final String CACHE_NAME = "bands";

    private final BandsApiClient bandsApiClient;

    public BandService(BandsApiClient bandsApiClient) {
        this.bandsApiClient = bandsApiClient;
    }

    @Cacheable(value = CACHE_NAME, key = "'allBands'")
    public List<Band> getAllBands() {
        return bandsApiClient.fetchAllBands();
    }

    public List<Band> getSortedBands(SortOrder sort) {
        Comparator<Band> comparator = switch (sort) {
            case POPULARITY -> Comparator.comparing(Band::numPlays).reversed();
            case ALPHABETICAL -> Comparator.comparing(Band::name, String.CASE_INSENSITIVE_ORDER);
        };

        return getAllBands()
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    };

    public Band getBandById(String id) {
        return getAllBands().stream()
                .filter(b -> b.id().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(() -> new BandNotFoundException(id));
    }

}
