package com.devanmejia.appmanager.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

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
    @Column(name = "time")
    private Timestamp time;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private App app;
}
