# AI-Assisted Text Classification API

A production-ready Spring Boot backend REST service that classifies input text into categories (**Complaint**, **Query**, **Feedback**, or **Other**) with a confidence score using an AI model via **OpenRouter API** (`openai/gpt-4o-mini`).

Built with **Java 17+**, **Spring Boot 4**, **Lombok**, and **Java Records**.

---

## 📋 Table of Contents
- [Features](#-features)
- [Architecture & Tech Stack](#-architecture--tech-stack)
- [Classification Categories](#-classification-categories)
- [How AI is Used](#-how-ai-is-used)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Configuration & Environment Setup](#-configuration--environment-setup)
- [Building & Running the Application](#-building--running-the-application)
- [API Documentation](#-api-documentation)
  - [1. Classify Text](#1-classify-text)
  - [2. Health Check](#2-health-check)
- [Error Handling](#-error-handling)
- [Testing & Postman Collection](#-testing--postman-collection)

---

## ✨ Features
- **Single REST Endpoint**: Clean `POST /api/v1/classify` endpoint accepting text payloads.
- **AI-Powered Categorization**: Leverages OpenRouter API with structured JSON output instructions.
- **Record DTOs & Lombok**: All Data Transfer Objects are implemented as immutable Java `record` types; configuration uses Lombok `@Getter` / `@Setter`.
- **Zero Hardcoded Configuration**: Configured strictly via `application.properties` with environment variable overrides.
- **Confidence Scoring & Safe Fallbacks**: Normalizes AI response and maps fallback confidence (e.g. `0.85`) if the AI omits confidence.
- **Comprehensive Error Handling**: Centralized exception handler returning consistent JSON errors for validation issues, invalid JSON, or external AI API errors.

---

## 🛠 Architecture & Tech Stack
- **Language**: Java 17+ (tested on Java 25)
- **Framework**: Spring Boot 4.1.1 (Spring MVC, Spring Validation)
- **HTTP Client**: Spring `RestClient` (non-blocking, modern synchronous HTTP client)
- **Boilerplate Reduction**: Project Lombok (`@Getter`, `@Setter`, `@RequiredArgsConstructor`, `@Slf4j`)
- **Data Modeling**: Java Records (`record ClassificationRequest`, `record ClassificationResponse`, etc.)
- **Serialization**: Jackson (`ObjectMapper`, annotations)
- **Testing**: JUnit 5, Mockito, Spring MockMvc

---

## 🏷 Classification Categories

| Category | Description | Example |
| :--- | :--- | :--- |
| **`Complaint`** | Reports dissatisfaction, bugs, defects, downtime, or service issues | *"My transaction failed and money got deducted."* |
| **`Query`** | Requests information, pricing, support documentation, or guidance | *"Do you provide student discounts?"* |
| **`Feedback`** | Expresses praise, impressions, or constructive suggestions | *"The new dark mode design is incredible!"* |
| **`Other`** | Casual greetings, chit-chat, spam, or text not matching above | *"Good morning team, have a great day!"* |

---

## 🤖 How AI is Used

1. **Client Request**: The client sends a text payload to `POST /api/v1/classify`.
2. **Prompt Engineering**: The service constructs a focused prompt with explicit classification definitions and schema constraints:
   - System instructions define each category and require strict JSON output (`json_object` format).
   - The user's input string is attached as the user message.
3. **OpenRouter AI Completion**: The payload is sent to `https://openrouter.ai/api/v1/chat/completions` using the configured model (default: `openai/gpt-4o-mini`).
4. **Resilient Parsing & Mapping**:
   - The JSON content is extracted and parsed into internal DTOs.
   - The category is checked and normalized into one of `Complaint`, `Query`, `Feedback`, or `Other`.
   - If the AI returns confidence, it is clamped to `[0.0, 1.0]`. If the AI model does not return a confidence score, a standard fallback mapping (`0.85`) is applied.
5. **Structured Response**: A clean `ClassificationResponse` record is returned to the client.

---

## 📁 Project Structure

```text
demo/
├── .mvn/wrapper/                  # Maven wrapper binaries
├── postman_collection.json        # Postman test collection
├── .env.example                   # Sample environment configuration
├── mvnw / mvnw.cmd                # Maven wrapper script
├── pom.xml                        # Maven dependencies & build plugins
├── README.md                      # Complete documentation
└── src/
    ├── main/
    │   ├── java/com/example/demo/
    │   │   ├── DemoApplication.java
    │   │   ├── config/
    │   │   │   └── OpenRouterConfig.java           # OpenRouter configuration & RestClient bean
    │   │   ├── controller/
    │   │   │   └── TextClassificationController.java # REST endpoints
    │   │   ├── dto/
    │   │   │   ├── ClassificationRequest.java      # Java record with validation
    │   │   │   ├── ClassificationResponse.java     # Java record response
    │   │   │   ├── ErrorResponse.java              # Java record error response
    │   │   │   └── openrouter/
    │   │   │       ├── OpenRouterChatRequest.java  # OpenRouter request record
    │   │   │       ├── OpenRouterChatResponse.java # OpenRouter response record
    │   │   │       └── ModelClassificationResult.java # Raw AI JSON result record
    │   │   ├── exception/
    │   │   │   ├── GlobalExceptionHandler.java     # Controller advice for errors
    │   │   │   └── OpenRouterApiException.java     # Custom runtime exception
    │   │   └── service/
    │   │       ├── ClassificationService.java      # Interface
    │   │       └── impl/
    │   │           └── OpenRouterClassificationService.java # OpenRouter implementation
    │   └── resources/
    │       └── application.properties              # Properties configuration
    └── test/
        └── java/com/example/demo/
            ├── DemoApplicationTests.java
            └── controller/
                └── TextClassificationControllerTest.java # MockMvc unit tests
```

---

## ⚙️ Configuration & Environment Setup

All configuration properties are loaded from `src/main/resources/application.properties` and can be overridden using environment variables:

| Property | Environment Variable | Default Value | Description |
| :--- | :--- | :--- | :--- |
| `server.port` | `PORT` | `8080` | Web server port |
| `openrouter.api.key` | `OPENROUTER_API_KEY` | *(Set via env/properties)* | OpenRouter API Secret Key |
| `openrouter.model` | `OPENROUTER_MODEL` | `openai/gpt-4o-mini` | OpenRouter model identifier |
| `openrouter.base-url` | `OPENROUTER_BASE_URL` | `https://openrouter.ai/api/v1` | OpenRouter Base URL |
| `openrouter.timeout` | `OPENROUTER_TIMEOUT` | `30` | Timeout in seconds |

### Setting Environment Variables (Optional)

```bash
export OPENROUTER_API_KEY="sk-or-v1-xxxxxxxxxxxxxxxxxxxx"
export OPENROUTER_MODEL="openai/gpt-4o-mini"
export OPENROUTER_BASE_URL="https://openrouter.ai/api/v1"
export OPENROUTER_TIMEOUT=30
```

---

## 🚀 Building & Running the Application

### 1. Build the Application
```bash
./mvnw clean package -DskipTests
```

### 2. Run the Application
```bash
./mvnw spring-boot:run
```
Or run the packaged JAR directly:
```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`.

---

## 📡 API Documentation

### 1. Classify Text
Classifies arbitrary input text into one of the four categories.

- **Method**: `POST`
- **Path**: `/api/v1/classify`
- **Headers**: `Content-Type: application/json`

#### Request Body
```json
{
  "text": "The delivery was delayed by three days and the item arrived broken."
}
```

#### Successful Response (`200 OK`)
```json
{
  "category": "Complaint",
  "confidence": 0.98,
  "text": "The delivery was delayed by three days and the item arrived broken."
}
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/api/v1/classify \
  -H "Content-Type: application/json" \
  -d '{
    "text": "How do I upgrade to the enterprise subscription plan?"
  }'
```

---

### 2. Health Check
Verifies service availability.

- **Method**: `GET`
- **Path**: `/api/v1/health`

#### Successful Response (`200 OK`)
```text
AI Text Classification API is running
```

---

## 🛡 Error Handling

All error responses adhere to a consistent JSON structure:

### 1. Validation Error (`400 Bad Request`)
When input text is empty or missing:
```bash
curl -X POST http://localhost:8080/api/v1/classify \
  -H "Content-Type: application/json" \
  -d '{"text": ""}'
```
Response:
```json
{
  "timestamp": "2026-09-20T10:15:30.123456Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for request",
  "details": [
    "Text cannot be blank"
  ]
}
```

### 2. Upstream AI Failure (`502 Bad Gateway`)
Returned if OpenRouter is unreachable or returns an error.
```json
{
  "timestamp": "2026-09-20T10:15:30.123456Z",
  "status": 502,
  "error": "Bad Gateway",
  "message": "AI service temporarily unavailable or failed: ..."
}
```

---

## 🧪 Testing & Postman Collection

### Running Automated Tests
```bash
./mvnw test
```

### Postman Collection
Import [`postman_collection.json`](./postman_collection.json) into Postman to test all endpoints:
- `Health Check`
- `Classify - Complaint`
- `Classify - Query`
- `Classify - Feedback`
- `Classify - Other`
- `Classify - Validation Error (Empty Text)`
