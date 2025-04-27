package hekireki.sanjijiksong.domain.order.repository;

import hekireki.sanjijiksong.domain.order.entity.Order;
import hekireki.sanjijiksong.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByUser(User user, Pageable pageable);
    
    @Query("SELECT o FROM Order o JOIN FETCH o.orderLists ol JOIN FETCH ol.item JOIN FETCH ol.store WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT DISTINCT o FROM Order o WHERE o.user = :user")
    Page<Order> findAllByUserPaged(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.orderLists ol JOIN FETCH ol.item JOIN FETCH ol.store WHERE o.id IN :ids")
    List<Order> findByIdInWithDetails(@Param("ids") List<Long> ids);
}
