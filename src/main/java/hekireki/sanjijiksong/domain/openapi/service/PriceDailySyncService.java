    package hekireki.sanjijiksong.domain.openapi.service;

    import hekireki.sanjijiksong.domain.openapi.Repository.PriceDailyRepository;
    import hekireki.sanjijiksong.domain.openapi.Repository.PriceDailySearchRepository;
    import hekireki.sanjijiksong.domain.openapi.document.PriceDailyDocument;
    import hekireki.sanjijiksong.domain.openapi.entity.PriceDaily;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.scheduling.annotation.Async;
    import org.springframework.stereotype.Service;

    import java.util.List;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class PriceDailySyncService {

        private final PriceDailyRepository priceDailyRepository;
        private final PriceDailySearchRepository priceDailySearchRepository;

        private static final int PAGE_SIZE = 1000; // 한 번에 처리할 페이지 크기

        /**
         * 모든 가격 정보를 Elasticsearch로 동기화하는 메서드 (페이징 처리)
         */
        @Async
        public void syncAllToElasticsearch() {
            int page = 0;
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            List<PriceDaily> priceDailyList;

            do {
                priceDailyList = priceDailyRepository.findAll(pageable).getContent();
                if (!priceDailyList.isEmpty()) {
                    List<PriceDailyDocument> documents = priceDailyList.stream()
                            .map(PriceDailyDocument::from)
                            .toList();

                    priceDailySearchRepository.saveAll(documents);
                    log.info("✅ {}건 Elasticsearch에 저장됨", documents.size());
                }
                page++;
                pageable = PageRequest.of(page, PAGE_SIZE);
            } while (!priceDailyList.isEmpty());

            log.info("✅ 동기화 완료: 모든 데이터 Elasticsearch에 저장됨");
        }
    }
