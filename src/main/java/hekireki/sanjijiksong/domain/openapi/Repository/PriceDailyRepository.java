package hekireki.sanjijiksong.domain.openapi.Repository;

import hekireki.sanjijiksong.domain.openapi.entity.PriceDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
    SELECT p
    FROM PriceDaily p
    WHERE
        (:keywords) IS NULL OR
        EXISTS (
            SELECT 1
            FROM TrendingKeyword tk
            WHERE p.itemName LIKE CONCAT('%', tk.keyword, '%')
            AND tk.keyword IN :keywords
        )
    ORDER BY p.snapshotDate DESC
   """)
    List<PriceDaily> findLatestByKeywords(@Param("keywords") List<String> keywords);
}
