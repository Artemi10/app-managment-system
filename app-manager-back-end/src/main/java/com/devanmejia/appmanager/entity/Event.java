package com.devanmejia.appmanager.entity;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Optional;

@Entity
@Table(name = "events")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "extra_information")
    private String extraInformation;
    @Column(name = "creation_time")
    private OffsetDateTime creationTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private App app;

    public Optional<String> getExtraInformation() {
        return Optional.ofNullable(extraInformation);
    }
}
