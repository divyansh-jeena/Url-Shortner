package com.example.url_shortner.Controller;

import com.example.url_shortner.Service.UrlService;
import com.example.url_shortner.dto.UrlRequest;
import com.example.url_shortner.dto.UrlResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/url")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> create(@RequestBody UrlRequest request) {
        return ResponseEntity.ok(urlService.create(request.longUrl()));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortUrl,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();

        String longUrl = urlService.redirect(shortUrl, ip);

        return ResponseEntity
                .status(302)
                .header("Location", longUrl)
                .build();
    }
}