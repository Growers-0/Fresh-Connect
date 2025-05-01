package hekireki.sanjijiksong.domain.chating.dto;

import hekireki.sanjijiksong.domain.chating.entity.Chat;
import hekireki.sanjijiksong.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageSaveDto {
    private User sender;
    private Chat chat;
}
