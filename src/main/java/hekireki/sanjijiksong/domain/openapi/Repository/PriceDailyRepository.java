package hekireki.sanjijiksong.domain.openapi.Repository;

import hekireki.sanjijiksong.domain.openapi.entity.PriceDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceDailyRepository extends JpaRepository<PriceDaily, Long> {

    // 가격 정보를 가져오는 메서드
    List<PriceDaily> findByCategoryCodeAndItemCodeAndSnapshotDateBetween(
            String categoryCode, String itemCode, LocalDate startDate, LocalDate endDate);

    PriceDaily findTopByItemNameContainingOrderBySnapshotDateDesc(String keyword);
}
