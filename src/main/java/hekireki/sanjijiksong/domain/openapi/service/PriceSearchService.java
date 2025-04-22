package hekireki.sanjijiksong.domain.openapi.service;

import hekireki.sanjijiksong.domain.openapi.Repository.PriceDailySearchRepository;
import hekireki.sanjijiksong.domain.openapi.document.PriceDailyDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceSearchService {
    private final PriceDailySearchRepository searchRepository;

    public List<PriceDailyDocument> searchByItemName(String keyword) {
        return searchRepository.findByItemNameContaining(keyword);
    }
}

