package com.example.url_shortner.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private  Long id;
    private String shortUrl;
    @Column(columnDefinition = "TEXT")
    private  String longUrl;
    private  LocalDateTime createdAt;
    private  LocalDateTime expiryTime;
    private  int clickCount;



}
