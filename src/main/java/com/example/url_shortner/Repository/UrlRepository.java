package com.example.url_shortner.Repository;

import com.example.url_shortner.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;


@Repository
public interface UrlRepository extends JpaRepository<Url,Long> {


    boolean existsByShortUrl(String shorturl);

    Optional<Url> findByShortUrl(String shortUrl);
}