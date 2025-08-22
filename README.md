<img src="https://file.catchya.online/public/1755278567155-catch-ya.png" width="144" align="right" hspace="0" />

# CatchYa

![Language](https://img.shields.io/badge/language-Java-blue)
![Framework](https://img.shields.io/badge/framework-Spring%20Boot-orange)
![Database](https://img.shields.io/badge/database-PostgreSQL-blue)
![WebSocket](https://img.shields.io/badge/WebSocket-enabled-blueviolet)
![Project Status](https://img.shields.io/badge/status-active-brightgreen)


**Catch-Ya** is a high-performance social media backend engineered with **Spring Boot** to power modern, interactive applications. This robust platform seamlessly integrates real-time communication with advanced geospatial capabilities, allowing users to connect and discover friends based on their location.

Core Features
-------------

*   **Real-Time Interactivity:** Leverage **WebSockets (STOMP)** for instant, live updates, including notifications for likes, comments, and messages, creating a dynamic user experience.
*   **Location-Based Discovery:** Utilize **PostGIS** and **Hibernate Spatial** to enable precise, efficient geospatial queries, allowing users to find and connect with friends in their vicinity.
*   **Secure Authentication:** Implement a robust security model with **Spring Security** and **JWT (JSON Web Tokens)** to ensure secure user registration and API access.
*   **Scalable Media Handling:** Manage multimedia content with **MinIO**, providing a flexible and scalable solution for storing user-uploaded images and other media files.
*   **Comprehensive API Documentation:** Ensure seamless integration for front-end clients with a fully documented **REST API** and WebSocket endpoints, powered by **SpringDoc OpenAPI (Swagger UI)**.


Technology Stack
----------------

This project is built on a foundation of industry-standard and battle-tested technologies.

### Backend & Core

*   **Framework:** Spring Boot, Spring Data JPA
*   **Security:** Spring Security, JJWT
*   **Validation:** Spring Boot Validation
*   **Search:** Hibernate Spatial, JTS
*   **API Docs:** SpringDoc OpenAPI


### Data & Services

*   **Database:** PostgreSQL with PostGIS
*   **Media Storage:** MinIO
*   **Real-Time & Chat System:** Spring WebSocket & WebFlux
*   **OTP Service:** SMS POH (Myanmar Local Service)
*   **HTTP Client:** OkHttp


Project Overview
----------------

Catch-Ya is a modern backend service designed to be the engine for a new generation of social applications. By combining the powerful, reactive capabilities of **Spring Boot** with specialized services like **PostGIS** and **MinIO**, this project provides a complete and production-ready solution for building features like:

*   **Geo-fenced events or friend suggestions**
*   **Live activity feeds and presence indicators**
*   **Secure and verifiable user registration workflows**


This backend is ready to support mobile and web clients, providing a scalable, secure, and feature-rich foundation for an engaging social experience.