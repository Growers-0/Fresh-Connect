package hekireki.sanjijiksong.domain.order.repository;

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
}

