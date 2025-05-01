package hekireki.sanjijiksong.domain.store.repository;

import hekireki.sanjijiksong.domain.store.entity.Store;
import hekireki.sanjijiksong.domain.store.dto.StoreProjection;
import hekireki.sanjijiksong.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store,Long> {
    boolean existsByUser(User user);
    List<Store> findByNameContainingAndActiveTrue(String keyword);
    Page<Store> findByActiveTrue(Pageable pageable);
    
    // 카운트 쿼리와 데이터 조회 쿼리 분리
    @Query(value = "SELECT s FROM Store s WHERE s.active = true",
           countQuery = "SELECT COUNT(s) FROM Store s WHERE s.active = true")
    Page<Store> findAllActiveStoresWithCountOptimization(Pageable pageable);
    
    // N+1 문제 방지를 위한 fetch join 추가
    @Query("SELECT s FROM Store s JOIN FETCH s.user WHERE s.id = :storeId AND s.active = true")
    Optional<Store> findByIdWithUserFetchJoin(@Param("storeId") Long storeId);
    
    // 프로젝션 사용 - 필요한 필드만 조회
    @Query("SELECT new hekireki.sanjijiksong.domain.store.dto.StoreProjection(s.id, s.name, s.address, s.description, s.image, s.active) " +
           "FROM Store s WHERE s.active = true")
    List<StoreProjection> findAllActiveStoresWithProjection();
    
    // 검색에도 프로젝션 적용
    @Query("SELECT new hekireki.sanjijiksong.domain.store.dto.StoreProjection(s.id, s.name, s.address, s.description, s.image, s.active) " +
           "FROM Store s WHERE s.name LIKE %:keyword% AND s.active = true")
    List<StoreProjection> findByNameContainingAndActiveTrueWithProjection(@Param("keyword") String keyword);
    
    Store findByUserId(Long id);
}
