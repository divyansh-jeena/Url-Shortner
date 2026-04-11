package com.example.url_shortner.Service;

import com.example.url_shortner.Repository.UrlRepository;
import com.example.url_shortner.dto.UrlResponse;
import com.example.url_shortner.entity.Url;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final RateLimiter rateLimiter;
    private final RedisTemplate<String, String> redisTemplate;

    public String generateShortUrl() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
    public UrlResponse create(String longUrl) {

        if (longUrl == null || longUrl.isEmpty()) {
            throw new RuntimeException("Invalid URL");
        }

        String shortUrl = generateShortUrl();

        while (urlRepository.existsByShortUrl(shortUrl)) {
            shortUrl = generateShortUrl();
        }

        Url url = Url.builder()
                .longUrl(longUrl)
                .shortUrl(shortUrl)
                .createdAt(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusDays(7))
                .clickCount(0)
                .build();

        urlRepository.save(url);

        return new UrlResponse(shortUrl, longUrl);
    }
    public String redirect(String shortUrl, String ip) {

        if (!rateLimiter.allowRequest(ip)) {
            throw new RuntimeException("Too many requests");
        }

        if (shortUrl == null || shortUrl.isEmpty()) {
            throw new IllegalArgumentException("Invalid URL");
        }

        String longUrl = getLongUrl(shortUrl);
        incrementClick(shortUrl);

        return longUrl;
    }
    public String getLongUrl(String shortUrl) {

        String key = "url:" + shortUrl;

        String cachedUrl = redisTemplate.opsForValue().get(key);
        if (cachedUrl != null) {
            return cachedUrl;
        }


        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        LocalDateTime now = LocalDateTime.now();
        if (url.getExpiryTime() != null &&
                url.getExpiryTime().isBefore(now)) {
            throw new RuntimeException("Link expired");
        }
        long ttl = Duration.between(now, url.getExpiryTime()).toSeconds();

        if (ttl <= 0) {
            throw new RuntimeException("Link expired");
        }
        redisTemplate.opsForValue().set(
                key,
                url.getLongUrl(),
                ttl,
                TimeUnit.SECONDS
        );

        return url.getLongUrl();
    }


    public void incrementClick(String shortUrl) {

        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
    }
}