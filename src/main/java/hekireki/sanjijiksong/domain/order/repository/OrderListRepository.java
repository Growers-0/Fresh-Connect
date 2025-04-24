package hekireki.sanjijiksong.domain.order.repository;

import hekireki.sanjijiksong.domain.item.dto.ItemDateStatistics;
import hekireki.sanjijiksong.domain.item.dto.ItemHourStatistics;
import hekireki.sanjijiksong.domain.item.dto.ItemSalesSummaryDTO;
import hekireki.sanjijiksong.domain.order.entity.OrderList;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderListRepository extends JpaRepository<OrderList, Long> {
    List<OrderList> findAllByStoreId(Long storeId);

    @Query("""
    SELECT o FROM OrderList o
    WHERE o.store.id = :storeId
      AND (
        (o.modifiedAt IS NOT NULL AND o.modifiedAt BETWEEN :start AND :end)
        OR (o.modifiedAt IS NULL AND o.createdAt BETWEEN :start AND :end)
      )
""")
    List<OrderList> findAllByStoreIdAndDate(@Param("storeId") Long storeId,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

    @Query("""
    SELECT new hekireki.sanjijiksong.domain.item.dto.ItemSalesSummaryDTO(i.name, SUM(o.countPrice))
    FROM OrderList o
    LEFT JOIN o.item i
    WHERE o.store.id = :storeId
    GROUP BY i.name
    ORDER BY SUM(o.countPrice) DESC
    """)
    List<ItemSalesSummaryDTO> findItemSalesSummaryByStoreId(@Param("storeId") Long storeId);

//페이징
    @Query("""
    SELECT new hekireki.sanjijiksong.domain.item.dto.ItemSalesSummaryDTO(i.name, SUM(o.countPrice))
    FROM OrderList o
    JOIN o.item i
    WHERE o.store.id = :storeId
    GROUP BY i.name
    ORDER BY SUM(o.countPrice) DESC
    """)
    List<ItemSalesSummaryDTO> findItemSalesSummaryByStoreId(@Param("storeId") Long storeId, Pageable pageable);

    @Query("""
    SELECT new hekireki.sanjijiksong.domain.item.dto.ItemDateStatistics(
        CAST(o.createdAt AS date),
        SUM(o.countPrice)
    )
    FROM OrderList o
    WHERE o.store.id = :storeId
      AND (o.createdAt BETWEEN :startDate AND :endDate
        OR o.modifiedAt BETWEEN :startDate AND :endDate)
    GROUP BY CAST(o.createdAt AS date)
    ORDER BY CAST(o.createdAt AS date) ASC
    """)
    List<ItemDateStatistics> findSalesSummaryTotal(Long storeId, LocalDateTime startDate, LocalDateTime endDate);

    @Query(
            value = """
        SELECT 
            HOUR(DATE_ADD(o.created_at, INTERVAL 9 HOUR)) AS hour,
            SUM(o.count_price) AS revenue
        FROM order_list o
        WHERE o.store_id = :storeId
          AND o.created_at BETWEEN :start AND :end
        GROUP BY HOUR(DATE_ADD(o.created_at, INTERVAL 9 HOUR))
        ORDER BY hour
        """,
            nativeQuery = true
    )
    List<ItemHourStatistics> findHourlySalesByStoreIdAndDate(
            @Param("storeId") Long storeId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}

