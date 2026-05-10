package fm.isobar.demo.service;

import fm.isobar.demo.client.BandsApiClient;
import fm.isobar.demo.model.Band;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BandService {

    private final BandsApiClient bandsApiClient;

    public BandService(BandsApiClient bandsApiClient) {
        this.bandsApiClient = bandsApiClient;
    }

    public List<Band> getAllBands() {
        return bandsApiClient.fetchAllBands();
    }

}
