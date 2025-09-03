<img src="https://file.catchya.online/public/catch-ya.png" width="144" align="right" />

# CatchYa

![Language](https://img.shields.io/badge/language-Java-blue)
![Framework](https://img.shields.io/badge/framework-Spring%20Boot-orange)
![Database](https://img.shields.io/badge/database-PostgreSQL-blue)
![WebSocket](https://img.shields.io/badge/websocket-STOMP-brightgreen)
![Cache](https://img.shields.io/badge/cache-Redis-red)
![Storage](https://img.shields.io/badge/storage-MinIO-teal)

---

**CatchYa** is a production-grade backend designed for interactive social platforms.  
It combines real-time communication, caching, and geospatial intelligence into a scalable foundation.

---

## Overview

- **Real-Time Messaging** — WebSocket (STOMP) with delivery ticks, reactions, and presence
- **Caching Layer** — Redis-powered hot-tail history, unread counters, and last-seen tracking
- **Location-Aware Discovery** — PostGIS + Hibernate Spatial for efficient geospatial queries
- **Scalable Media Handling** — MinIO for object storage and media delivery
- **Authentication & Security** — Spring Security with JWT for robust access control
- **API Documentation** — SpringDoc OpenAPI integration for seamless client adoption

---

## Stack

- **Framework**: Spring Boot 3 (Web, WebFlux, WebSocket)
- **Database**: PostgreSQL 17 + PostGIS
- **Cache**: Redis 7
- **Storage**: MinIO
- **Security**: Spring Security + JWT
- **Docs**: SpringDoc OpenAPI  