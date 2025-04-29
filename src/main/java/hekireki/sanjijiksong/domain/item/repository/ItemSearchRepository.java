package hekireki.sanjijiksong.domain.item.repository;

import hekireki.sanjijiksong.domain.item.es.ItemDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ItemSearchRepository extends ElasticsearchRepository<ItemDocument, String> {

    List<ItemDocument> findByNameContainingOrDescriptionContaining(String name, String desc);
}
