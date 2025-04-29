package hekireki.sanjijiksong.domain.chating.repository;

import hekireki.sanjijiksong.domain.chating.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
}
