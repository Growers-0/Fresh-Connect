package hekireki.sanjijiksong.domain.openapi.Repository;

import hekireki.sanjijiksong.domain.openapi.document.PriceDailyDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDate;
import java.util.List;

public interface PriceDailySearchRepository extends ElasticsearchRepository<PriceDailyDocument, String>{
    List<PriceDailyDocument> findByItemNameContaining(String keyword);

    List<PriceDailyDocument> findByItemNameContainingAndSnapshotDateBetween(String itemName, LocalDate startDate, LocalDate endDate);

    List<PriceDailyDocument> findTopByItemNameContainingOrderBySnapshotDateDesc(String keyword, Pageable pageable);

}
