package com.example.url_shortner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Builder
@Table(name = "urls")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalUrl;

    @Column(unique = true)
    private String shortUrl;

    private Long clickCount = 0L;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime expiryTime;




}
