package hekireki.sanjijiksong.domain.openapi.Repository;

import hekireki.sanjijiksong.domain.openapi.document.PriceDailyDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDate;
import java.util.List;

public interface PriceDailySearchRepository extends ElasticsearchRepository<PriceDailyDocument, String>{
    List<PriceDailyDocument> findByItemNameContaining(String keyword);

    @Query("""
    {
      "bool": {
        "must": [
          { "match": { "itemName.keyword": "?0" }},
          { "range": { "snapshotDate": { "gte": "?1", "lte": "?2" }}}
        ]
      }
    }
    """)
    List<PriceDailyDocument> findByItemNameKeywordAndSnapshotDateBetween(String itemName, String start, String end);
}
