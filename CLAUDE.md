# WealthTrack API — Agent Instructions

## Project Overview
WealthTrack is a Spring Boot REST API for personal investment 
and account management. It supports multiple account types 
(BROKERAGE, RETIREMENT, HSA, SAVINGS) with contribution 
tracking, balance management, and transaction history.

This is an enterprise-grade Java application demonstrating
clean architecture, SOLID principles, and Gang of Four design
patterns in a real financial domain context.

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Data JPA (H2 in-memory for dev)
- SpringDoc OpenAPI 3.0 (Swagger UI)
- Gradle build
- JUnit 5 + Mockito
- Lombok

## Architecture — Non-Negotiable Rules

### Package Structure
Every class must live in exactly one of these packages:
-`controller` -> REST layer only, zero business logic
-`service`    -> All business logic lives here, nowhere else
-`repository` -> Spring Data JPA interfaces only
- `model`     -> Domain entities using Builder pattern.
- `dto`       -> Request/Response objects, never expose entities directly
- `strategy`    → Strategy pattern implementations
- `factory`     → Factory pattern implementations  
- `exception`   → Custom exceptions + GlobalExceptionHandler
- `config`      → Spring configuration classes
- `enums`       → All enums (AccountType, ContributionType etc)

### Layering Rules
- Controllers call Services only — never repositories directly
- Services call Repositories — never other controllers
- DTOs cross the API boundary — entities never leave the service layer
- No @Autowired field injection — constructor injection only

## Coding Standards

### Java Style
- Java 17 features encouraged: records for DTOs, switch expressions
- Lombok: @Builder, @Getter, @Slf4j — no @Data on entities
- All fields private, final where possible
- No magic numbers — use named constants or enums
- Streams and lambdas preferred over imperative loops

### Naming Conventions
- Controllers: `AccountController`, `ContributionController`
- Services: `AccountService`, `AccountServiceImpl`
- Repositories: `AccountRepository`
- DTOs: `AccountRequest`, `AccountResponse`, `ContributionRequest`
- Exceptions: `AccountNotFoundException`, `InsufficientFundsException`

### Exception Handling Rules
- Never return null — throw a named exception instead
- Every custom exception extends RuntimeException
- GlobalExceptionHandler catches all exceptions
- Standard error response format always:
```json
{
  "code": "ACCOUNT_NOT_FOUND",
  "message": "Account with id 123 was not found",
  "timestamp": "2026-05-30T10:15:30Z"
}
```

### Strategy Pattern
Use for contribution calculations. Never use if-else chains
for business rule variations — always a Strategy.

### Factory Pattern
ContributionStrategyFactory selects the correct strategy
based on AccountType and contributor age. The controller
and service never instantiate strategies directly.

### Builder Pattern
All domain model classes (Account, Contribution, Transaction)
must use Lombok @Builder. Never expose public constructors
with more than 2 parameters.

### Observer Pattern (Spring Events)
Use Spring ApplicationEvent for side effects:
- ContributionMadeEvent → triggers audit log entry
- AccountCreatedEvent → triggers welcome notification log
Never call audit/notification logic directly from service.

## API Contract Rules

### URL Structure
/api/v1/{resource}/{id}/{sub-resource}

Examples:
- POST   /api/v1/accounts
- GET    /api/v1/accounts/{id}
- GET    /api/v1/accounts/{id}/balance
- POST   /api/v1/accounts/{id}/contributions
- GET    /api/v1/accounts/{id}/contributions
- DELETE /api/v1/accounts/{id}

### Swagger Annotations — Required on Every Endpoint
Every controller method must have:
- @Operation(summary = "...", description = "...")
- @ApiResponse for every possible HTTP status code
- @Parameter on every path/query variable
- @Schema on every DTO class

### HTTP Status Codes — Use Correctly
- 200 OK          → successful GET, successful DELETE
- 201 Created     → successful POST that creates a resource
- 400 Bad Request → validation failure
- 404 Not Found   → resource does not exist
- 409 Conflict    → duplicate resource / business rule violation
- 500 Server Error → unexpected exception (never expose stack trace)

## Testing Requirements
- Minimum one unit test class per Service class
- Use Mockito to mock all repository dependencies
- Test method naming: methodName_scenario_expectedResult
  Example: createAccount_withValidRequest_returnsCreatedAccount
- Test happy path + at least one failure path per method
- No Spring context in unit tests — pure JUnit 5 + Mockito

## What Good Looks Like
A well-built feature in this project has:
✓ DTO for request and response (not raw entities)
✓ Swagger @Operation and @ApiResponse annotations
✓ Constructor injection in service
✓ Strategy/Factory pattern if business rules vary
✓ Custom exception with meaningful message
✓ At least one unit test covering happy path
✓ Conventional git commit message

## Java Streams
Use Java Streams extensively where applicable:
- Collection filtering, mapping, reducing
- Finding elements (findFirst, findAny)
- Aggregations (sum, average, count, min, max)
- Grouping and partitioning (Collectors.groupingBy, partitioningBy)
- Converting between collection types
- Chaining multiple operations in a single pipeline