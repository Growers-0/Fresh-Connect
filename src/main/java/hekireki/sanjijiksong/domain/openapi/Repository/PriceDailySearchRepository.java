package hekireki.sanjijiksong.domain.openapi.Repository;

import hekireki.sanjijiksong.domain.openapi.document.PriceDailyDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PriceDailySearchRepository extends ElasticsearchRepository<PriceDailyDocument, String> {
    List<PriceDailyDocument> findByItemNameContaining(String keyword);
}
