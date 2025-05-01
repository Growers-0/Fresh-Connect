package hekireki.sanjijiksong.domain.following.service;

import hekireki.sanjijiksong.domain.following.dto.FollowingResponse;
import hekireki.sanjijiksong.domain.following.entity.Following;
import hekireki.sanjijiksong.domain.following.repository.FollowingRepository;
import hekireki.sanjijiksong.domain.store.entity.Store;
import hekireki.sanjijiksong.domain.store.repository.StoreRepository;
import hekireki.sanjijiksong.domain.user.entity.User;
import hekireki.sanjijiksong.domain.user.repository.UserRepository;
import hekireki.sanjijiksong.global.common.exception.ErrorCode;
import hekireki.sanjijiksong.global.common.exception.FollowingException;
import hekireki.sanjijiksong.global.common.exception.StoreException;
import hekireki.sanjijiksong.global.common.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowingService {
    private final FollowingRepository followingRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public FollowingResponse followStore(Long userId, Long storeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorCode.STORE_NOT_FOUND));

        if (followingRepository.findByUserAndStore(user, store).isPresent()) {
            throw new FollowingException(ErrorCode.ALREADY_FOLLOWING);
        }

        Following following = Following.builder()
                .user(user)
                .store(store)
                .build();

        Following savedFollowing = followingRepository.save(following);
        return FollowingResponse.from(savedFollowing);
    }

    @Transactional
    public void unfollowStore(Long userId, Long storeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
        
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorCode.STORE_NOT_FOUND));

        Following following = followingRepository.findByUserAndStore(user, store)
                .orElseThrow(() -> new FollowingException(ErrorCode.FOLLOWING_NOT_FOUND));

        followingRepository.delete(following);
    }

    @Transactional(readOnly = true)
    public List<FollowingResponse> getFollowingStores(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

        return followingRepository.findByUser(user).stream()
                .map(FollowingResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long getFollowingCount(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(ErrorCode.STORE_NOT_FOUND));

        return followingRepository.countByStore(store);
    }
} 