package hekireki.sanjijiksong.domain.following.repository;

import hekireki.sanjijiksong.domain.following.entity.Following;
import hekireki.sanjijiksong.domain.store.entity.Store;
import hekireki.sanjijiksong.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowingRepository extends JpaRepository<Following, Long> {
    Optional<Following> findByUserAndStore(User user, Store store);
    
    List<Following> findByUser(User user);
    
    List<Following> findByStore(Store store);
    
    @Query("SELECT COUNT(f) FROM Following f WHERE f.store = :store")
    Long countByStore(@Param("store") Store store);
    
    @Query("SELECT COUNT(f) FROM Following f WHERE f.user = :user")
    Long countByUser(@Param("user") User user);
} 