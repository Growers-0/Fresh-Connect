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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowingServiceTest {

    @Mock
    private FollowingRepository followingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private FollowingService followingService;

    private User user;
    private Store store;
    private Following following;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        store = Store.builder()
                .id(1L)
                .name("테스트 가게")
                .active(true)
                .build();

        following = Following.builder()
                .user(user)
                .store(store)
                .build();
        ReflectionTestUtils.setField(following, "id", 1L);
    }

    @Nested
    @DisplayName("가게 팔로우")
    class FollowStore {
        @Test
        @DisplayName("가게 팔로우 성공")
        void followStore_Success() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
            when(followingRepository.findByUserAndStore(user, store)).thenReturn(Optional.empty());
            when(followingRepository.save(any(Following.class))).thenReturn(following);

            // when
            FollowingResponse response = followingService.followStore(1L, 1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.userId()).isEqualTo(1L);
            assertThat(response.store().id()).isEqualTo(1L);
            verify(followingRepository).save(any(Following.class));
        }

        @Test
        @DisplayName("가게 팔로우 실패 - 이미 팔로우 중")
        void followStore_Fails_WhenAlreadyFollowing() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
            when(followingRepository.findByUserAndStore(user, store)).thenReturn(Optional.of(following));

            // when & then
            assertThatThrownBy(() -> followingService.followStore(1L, 1L))
                    .isInstanceOf(FollowingException.class)
                    .hasMessage(ErrorCode.ALREADY_FOLLOWING.getMessage());
        }

        @Test
        @DisplayName("가게 팔로우 실패 - 사용자 없음")
        void followStore_Fails_WhenUserNotFound() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followingService.followStore(1L, 1L))
                    .isInstanceOf(UserException.class)
                    .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("가게 팔로우 실패 - 가게 없음")
        void followStore_Fails_WhenStoreNotFound() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(storeRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followingService.followStore(1L, 1L))
                    .isInstanceOf(StoreException.class)
                    .hasMessage(ErrorCode.STORE_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("가게 언팔로우")
    class UnfollowStore {
        @Test
        @DisplayName("가게 언팔로우 성공")
        void unfollowStore_Success() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
            when(followingRepository.findByUserAndStore(user, store)).thenReturn(Optional.of(following));

            // when
            followingService.unfollowStore(1L, 1L);

            // then
            verify(followingRepository).delete(following);
        }

        @Test
        @DisplayName("가게 언팔로우 실패 - 팔로우 중이 아님")
        void unfollowStore_Fails_WhenNotFollowing() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
            when(followingRepository.findByUserAndStore(user, store)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followingService.unfollowStore(1L, 1L))
                    .isInstanceOf(FollowingException.class)
                    .hasMessage(ErrorCode.FOLLOWING_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("팔로우 중인 가게 목록 조회")
    class GetFollowingStores {
        @Test
        @DisplayName("팔로우 중인 가게 목록 조회 성공")
        void getFollowingStores_Success() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(followingRepository.findByUser(user)).thenReturn(List.of(following));

            // when
            List<FollowingResponse> responses = followingService.getFollowingStores(1L);

            // then
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0)).isNotNull();
            assertThat(responses.get(0).userId()).isEqualTo(1L);
            assertThat(responses.get(0).store().id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("팔로우 중인 가게 목록 조회 실패 - 사용자 없음")
        void getFollowingStores_Fails_WhenUserNotFound() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followingService.getFollowingStores(1L))
                    .isInstanceOf(UserException.class)
                    .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("가게 팔로워 수 조회")
    class GetFollowingCount {
        @Test
        @DisplayName("가게 팔로워 수 조회 성공")
        void getFollowingCount_Success() {
            // given
            when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
            when(followingRepository.countByStore(store)).thenReturn(5L);

            // when
            Long count = followingService.getFollowingCount(1L);

            // then
            assertThat(count).isEqualTo(5L);
        }

        @Test
        @DisplayName("가게 팔로워 수 조회 실패 - 가게 없음")
        void getFollowingCount_Fails_WhenStoreNotFound() {
            // given
            when(storeRepository.findById(1L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> followingService.getFollowingCount(1L))
                    .isInstanceOf(StoreException.class)
                    .hasMessage(ErrorCode.STORE_NOT_FOUND.getMessage());
        }
    }
} 