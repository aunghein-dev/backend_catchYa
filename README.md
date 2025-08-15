<img src="https://file.catchya.online/public/1755278567155-catch-ya.png" width="144" align="right" hspace="0" />

# Catch-Ya Social Media Project

![Language](https://img.shields.io/badge/language-Java-blue)
![Framework](https://img.shields.io/badge/framework-Spring%20Boot-orange)
![Database](https://img.shields.io/badge/database-PostgreSQL-blue)
![WebSocket](https://img.shields.io/badge/WebSocket-enabled-blueviolet)
![Project Status](https://img.shields.io/badge/status-active-brightgreen)


**Catch-Ya** is an active social media application built using Spring Boot. It supports real-time notifications via WebSockets, geospatial friend searching with PostGIS, and media storage using MinIO. Users can find friends nearby and get live updates on activities.

## Technologies & Tools

- **Backend:** Spring Boot, Spring Data JPA
- **WebSockets:** Real-time notifications using STOMP/WebSocket
- **Database:** PostgreSQL + PostGIS for geospatial queries
- **Security:** Spring Security + JWT Authentication
- **File Storage:** MinIO for media handling
- **OTP Service:** SMS POH (Myanmar Local Service)
- **Validation:** Spring Boot Validation
- **Search:** Find friends nearby using Hibernate Spatial & JTS
- **Documentation:** Swagger UI / SpringDoc OpenAPI
- **Other Libraries:** Lombok, OkHttp, JJWT

## Overview

**Catch-Ya** is a modern social media backend project built with **Spring Boot**, **WebSocket** notifications, and **PostgreSQL/PostGIS** for spatial queries. The project allows users to:

- Connect and send/receive real-time notifications via WebSockets.
- Search and find nearby friends using location-based queries.
- Manage user authentication with JWT and Spring Security.
- Store multimedia content (images) using MinIO or other storage solutions.

This backend serves as the foundation for mobile and web clients to build interactive, location-aware social features.


## Features

- User registration and authentication with JWT
- Real-time notifications (likes, messages, etc.) via WebSockets
- Search for nearby friends using PostGIS
- Media uploads (images) stored in MinIO
- REST APIs and WebSocket endpoints documented with Swagger

## Dependencies

- Spring Boot 3.5.4
- Spring Security
- Spring WebSocket & WebFlux
- PostgreSQL 42.7.5
- Hibernate Spatial 6.2.7
- JTS Core 1.19.0
- MinIO 8.5.17
- jjwt 0.12.6
- OkHttp 2.7.5 / Okio 3.4.0
- SpringDoc OpenAPI Starter WebMVC UI 2.1.0


