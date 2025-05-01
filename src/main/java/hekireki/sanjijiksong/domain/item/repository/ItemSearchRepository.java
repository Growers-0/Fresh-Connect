package hekireki.sanjijiksong.domain.item.repository;

import hekireki.sanjijiksong.domain.item.es.ItemDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ItemSearchRepository extends ElasticsearchRepository<ItemDocument, String> {

    @Query("""
    {
      "bool": {
        "must": [
          { "match": { "name": "?0" } },
          { "term": { "active": true } }
        ]
      }
    }
    """)
    List<ItemDocument> searchByNameOnlyActive(String name);
}
