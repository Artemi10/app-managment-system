package com.devanmejia.appmanager.entity.user;

import com.devanmejia.appmanager.entity.App;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "users")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "authority")
    private Authority authority;
    @Column(name = "reset_token")
    private String resetToken;
    @Column(name = "oauth_enter_token")
    private String oauthEnterToken;
    @Column(name = "refresh_token")
    private String refreshToken;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<App> apps;

    public Optional<String> getResetToken() {
        return Optional.ofNullable(resetToken);
    }

    public Optional<String> getOauthEnterToken() {
        return Optional.ofNullable(oauthEnterToken);
    }
}
