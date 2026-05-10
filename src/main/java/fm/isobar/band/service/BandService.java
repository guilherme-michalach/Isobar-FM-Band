package fm.isobar.band.service;

import fm.isobar.band.client.BandsApiClient;
import fm.isobar.band.exception.BandNotFoundException;
import fm.isobar.band.model.Band;
import fm.isobar.band.model.SortOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

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

    public List<Band> getBands(String q, SortOrder sort) {
        var bands = getAllBands().stream()
                .filter(b -> q == null || q.isBlank() || (matchesName(b, q.toLowerCase())))
                .toList();

        return applySorting(bands, sort);
    }

    public Band getBandById(String id) {
        return getAllBands().stream()
                .filter(b -> b.id().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(() -> new BandNotFoundException(id));
    }

    public List<Band> applySorting(List<Band> bands, SortOrder sort) {
        if (sort == null) return bands;
        return bands.stream().sorted(comparatorFor(sort)).toList();
    }

    private Comparator<Band> comparatorFor(SortOrder sort) {
        return switch (sort) {
            case ALPHABETICAL -> Comparator.comparing(Band::name, String.CASE_INSENSITIVE_ORDER);
            case POPULARITY   -> Comparator.comparingLong(Band::numPlays).reversed();
        };
    }

    private boolean matchesName(Band band, String lowerQuery) {
        return band.name() != null && band.name().toLowerCase().contains(lowerQuery);
    }

}
