package fm.isobar.demo.service;

import fm.isobar.demo.client.BandsApiClient;
import fm.isobar.demo.model.Band;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

}
