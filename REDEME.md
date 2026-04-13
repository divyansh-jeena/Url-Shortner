# URL Shortener (High-Performance Backend)

A high-performance URL shortening service designed for fast redirection, scalability, and efficient request handling under load.

## 🚀 Key Features
- Generates unique short URLs with fast HTTP redirection
- Redis caching layer to reduce database load and improve response time (~50% improvement under repeated requests)
- Sliding window rate limiting to prevent abuse
- Configurable 7-day expiration with automated cleanup
- Thread-safe handling of concurrent requests
- Analytics tracking for URL usage

## ⚙️ System Design
- Cache-first approach using Redis for frequently accessed URLs
- Database fallback for persistent storage
- Efficient lookup and redirection flow for minimal latency

## 🛠 Tech Stack
Java, Spring Boot, MySQL, Redis, REST APIs

## 📂 Architecture
Controller → Service → Repository → Database + Cache Layer

## 🎯 Purpose
Built to demonstrate scalable backend design, caching strategies, rate limiting, and concurrent request handling in real-world systems.

## 🚀 Future Improvements
- Custom aliases for short URLs
- Advanced analytics dashboard
- Distributed deployment
- Enhanced security and monitoring  
