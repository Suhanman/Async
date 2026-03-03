# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.hellomessagequeue.HelloMessageQueueApplicationTests"

# Clean build
./gradlew clean build
```

## Prerequisites

A local RabbitMQ broker must be running before starting the application:
- Host: `localhost`, Port: `5672`
- Credentials: `guest` / `guest`
- App runs on port `8080`

## Project Architecture

This is a **Spring Boot 4.0.3 / Java 17** learning project demonstrating progressive RabbitMQ messaging patterns. Each step is in its own package under `com.example.hellomessagequeue`. Earlier steps are commented out; only **step4 is active**.

### Step progression (step0 → step4)

| Package | Pattern | Status | Key concept |
|---------|---------|--------|-------------|
| `step0` | Simple Queue | Commented out | Basic `Sender` / `Receiver` via `RabbitTemplate` + `SimpleMessageListenerContainer` |
| `step2` | Work Queue | Commented out | Durable queue, manual ack mode, task duration encoded in message body (`message\|durationMs`) |
| `step3` | Pub/Sub + WebSocket | Commented out | FanoutExchange → single queue → `@RabbitListener` → STOMP `/topic/notifications` |
| `step4` | Multi-queue Pub/Sub + WebSocket | **Active** | FanoutExchange → 3 queues → per-topic STOMP broadcast |

### Active architecture (step4)

```
REST POST /news/api/publish?newsType=X
        or
WebSocket /app/subscribe  (header: newsType)
        ↓
NewsPublisher → RabbitMQ FanoutExchange (newsExchange)
        ↓  broadcast to all 3 queues
javaQueue / springQueue / vueQueue
        ↓  @RabbitListener per queue
NewsSubscriber → SimpMessagingTemplate
        ↓
WebSocket topics: /topic/java, /topic/spring, /topic/vue
        ↓
Browser (SockJS + STOMP.js, Thymeleaf template at /news)
```

**Key classes in step4:**
- `RabbitMQConfig` — declares the `newsExchange` FanoutExchange and binds all 3 queues to it
- `NewsPublisher` — sends to the exchange via `RabbitTemplate.convertAndSend(exchange, routingKey, message)`; routing key is ignored by fanout
- `NewsSubscriber` — one `@RabbitListener` method per queue, forwards to WebSocket via `SimpMessagingTemplate`
- `NewsController` — STOMP `@MessageMapping("/subscribe")` reads `newsType` from the STOMP header
- `NewsRestController` — REST alternative at `POST /news/api/publish`
- `WebSocketConfig` — STOMP over SockJS at `/ws`; broker prefix `/topic`; app prefix `/app`

### Supporting structure
- `HelloController` — root controller, serves `index.html` (home page listing steps)
- `step4/HomeController` — serves `news.html` (the live demo frontend)
- Templates use Thymeleaf; frontend uses CDN-loaded SockJS + STOMP.js

## Extending the project

When adding a new step (e.g., step5), follow the convention:
1. Create a new sub-package `step5/`
2. Comment out the previous active step's `@Configuration` and `@Component` annotations (or comment out the whole file) to avoid bean conflicts — only one `RabbitMQConfig`, `WebSocketConfig`, etc. should be active at a time
3. Add a new `@Controller` that serves the demo page, and wire it in `application.yaml` if needed
