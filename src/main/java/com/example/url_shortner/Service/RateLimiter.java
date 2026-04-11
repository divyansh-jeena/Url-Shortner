package com.example.url_shortner.Service;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

    @Component
    public class RateLimiter {

        private final Map<String, List<Long>> requestMap = new ConcurrentHashMap<>();

        private static final int LIMIT = 5;
        private static final long WINDOW = 60000;

        public boolean allowRequest(String ip) {

            long now = System.currentTimeMillis();


            requestMap.putIfAbsent(ip, Collections.synchronizedList(new ArrayList<>()));

            List<Long> requests = requestMap.get(ip);

            synchronized (requests) {


                requests.removeIf(time -> now - time > WINDOW);


                if (requests.size() >= LIMIT) {
                    return false;
                }


                requests.add(now);
            }

            return true;
        }
    }

