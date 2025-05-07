package hekireki.sanjijiksong.global.security.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import hekireki.sanjijiksong.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Jackson을 위한 기본 생성자
    @JsonCreator
    public CustomUserDetails() {
        this.user = new User();  // 빈 User 객체 생성
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority(){
            @Override
            public String getAuthority() {
                return "ROLE_" + user.getRole().name();
            }
        });
        return collection;
    }

    public User getUser() {
        return this.user;
    }

    public UUID getUid() {return this.user.getUid(); }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
