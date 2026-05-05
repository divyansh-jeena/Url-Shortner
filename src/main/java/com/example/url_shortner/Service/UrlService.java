package com.example.url_shortner.Service;

import com.example.url_shortner.Repository.UrlRepository;
import com.example.url_shortner.dto.UrlResponse;
import com.example.url_shortner.entity.Url;
import com.example.url_shortner.util.Base62Util;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimiter rateLimiter;


    public UrlResponse create(String longUrl) {

        if (longUrl == null || longUrl.isBlank()) {
            throw new RuntimeException("Invalid URL");
        }


        Url url = new Url();
        url.setOriginalUrl(longUrl);
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiryTime(LocalDateTime.now().plusDays(7));

        Url saved = urlRepository.save(url);


        String shortCode = Base62Util.encode(saved.getId());


        saved.setShortUrl(shortCode);
        urlRepository.save(saved);

        return new UrlResponse(shortCode, longUrl);
    }


    public String redirect(String shortUrl, String ip) {

        if (!rateLimiter.allowRequest(ip)) {
            throw new RuntimeException("Too many requests");
        }

        String longUrl = getLongUrl(shortUrl);


        incrementClick(shortUrl);

        return longUrl;
    }


    public String getLongUrl(String shortUrl) {

        String key = "url:" + shortUrl;

        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }
        Url url = urlRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        LocalDateTime now = LocalDateTime.now();

        if (url.getExpiryTime() != null && url.getExpiryTime().isBefore(now)) {
            throw new RuntimeException("Link expired");
        }

        String longUrl = url.getOriginalUrl();


        if (url.getExpiryTime() != null) {
            long ttl = Duration.between(now, url.getExpiryTime()).toSeconds();

            if (ttl > 0) {
                redisTemplate.opsForValue().set(key, longUrl, ttl, TimeUnit.SECONDS);
            }
        }

        return longUrl;
    }


    public void incrementClick(String shortUrl) {
        String key = "click:" + shortUrl;
        redisTemplate.opsForValue().increment(key);
    }

    @Scheduled(fixedRate = 60000)
    public void syncClicks() {

        Set<String> keys = redisTemplate.keys("click:*");

        // Bug fix: keys() can return null, causing NPE
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {

            String shortUrl = key.replace("click:", "");
            String countStr = redisTemplate.opsForValue().get(key);

            if (countStr == null) continue;

            long count = Long.parseLong(countStr);

            urlRepository.findByShortUrl(shortUrl).ifPresent(url -> {
                url.setClickCount(url.getClickCount() + count);
                urlRepository.save(url);
            });

            redisTemplate.delete(key);
        }
    }
}