# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot application for monitoring escape room reservation availability and sending notifications to users when desired time slots become available. The service crawls escape room booking websites, compares availability against user preferences, and sends real-time alerts.

**Tech Stack**: Java 17, Spring Boot 3.5.9, Spring Data JPA, Spring Web, H2 Database (development), Lombok

**Base Package**: `kr.co.escape.api`

## Common Commands

### Build and Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "ClassName"

# Run a single test method
./gradlew test --tests "ClassName.methodName"

# Clean build
./gradlew clean build
```

### Development
```bash
# Check for dependency updates
./gradlew dependencyUpdates

# Generate project reports
./gradlew check
```

## Architecture

### System Components

**Crawling System**: Scheduled crawlers extract reservation availability from various escape room booking platforms. Each crawler should handle dynamic web pages (JavaScript-rendered content) and respect rate limits (5-30 minute intervals).

**User Alert Management**: Users configure alerts specifying desired themes, date ranges, time slots, number of people, and notification preferences. The system matches new availability against these criteria.

**Notification Engine**: When availability matching user criteria is detected, notifications are sent via configured channels (email, Kakao, push). Includes deduplication logic to prevent repeated alerts for the same time slot.

**Data Comparison Engine**: Compares newly crawled availability data against previous state to detect changes. Uses Redis for caching and preventing duplicate notifications.

### Database Schema

Key entities:
- **users**: User accounts with notification preferences
- **themes**: Escape room theme metadata (cafe name, theme name, branch, difficulty, genre, duration, price)
- **user_alerts**: User-configured alert criteria (theme, date range, preferred times, people count, active status)
- **availability**: Crawled reservation slots (theme, date, time slot, availability status, crawl timestamp)
- **notifications**: Notification history (user, alert, availability, sent timestamp, type, status)

### Key Design Considerations

**Crawling Strategy**:
- Use Selenium/Playwright for JavaScript-heavy sites, Jsoup for static HTML
- Implement retry logic for failed crawls with exponential backoff
- Respect robots.txt and implement User-Agent rotation if needed
- Handle CAPTCHA scenarios gracefully

**Performance**:
- Use Spring WebFlux for async/reactive crawling operations
- Cache frequently accessed data in Redis
- Batch database operations where possible
- Consider multi-threading for parallel site crawling

**Notification Deduplication**:
- Redis stores sent notification keys (user_id + theme_id + datetime)
- TTL-based expiration for notification tracking
- Prevent same slot notification within configurable window

**Extensibility**:
- Abstract crawler interface for easy addition of new booking platforms
- Plugin-style notification channels
- Configurable crawling schedules per site

## Development Guidelines

**Entity Relationships**: Use JPA bidirectional relationships carefully. User ↔ UserAlert and Theme ↔ Availability are key relationships. Consider fetch strategies (LAZY vs EAGER) for performance.

**Scheduler Configuration**: Use `@Scheduled` annotations for crawlers. Configure cron expressions in application.properties for different crawling intervals per site.

**Error Handling**: Crawling failures should log errors but not crash the application. Implement circuit breaker pattern for repeatedly failing sites.

**Testing**: Write integration tests for crawlers using mocked HTTP responses. Test notification logic with test channels before production deployment.

## Configuration

Application properties should include:
- Database connection settings (MariaDB/MySQL for production, H2 for dev)
- Redis configuration for caching
- SMTP settings for email notifications
- Kakao API credentials
- Crawler user-agent and timeout settings
- Rate limiting parameters

## Legal and Ethical Considerations

**Crawling Compliance**:
- Always check robots.txt before crawling
- Implement rate limiting to avoid server overload
- Do not circumvent access controls
- Verify each site's terms of service permit crawling

**Data Privacy**:
- Encrypt user passwords using BCrypt
- Secure storage of notification preferences
- HTTPS required for all external communications
- Handle user data per GDPR/local privacy laws