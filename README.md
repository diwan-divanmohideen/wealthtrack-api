# WealthTrack API

A personal investment and account management REST API built with Java 17 and Spring Boot 3.

## Prerequisites

Before running the application, make sure you have the following installed:

- **Java 17** — [Download here](https://adoptium.net/)
- **Git** — to clone the repo

You do not need to install Gradle separately — the project includes the Gradle wrapper (`gradlew`).

---

## Running the Application

**1. Clone the repository**

```bash
git clone https://github.com/YOUR-USERNAME/wealthtrack-api.git
cd wealthtrack-api
```

**2. Start the application**

On Mac/Linux:
```bash
./gradlew bootRun
```

On Windows:
```bash
gradlew.bat bootRun
```

**3. Confirm it is running**

You should see this line in the terminal output:
```
Started WealthtrackApiApplication in X.XXX seconds
```

The API is now running at: `http://localhost:8080`

> The application uses an H2 in-memory database. No database setup is required — it starts fresh every time you run it.

---

## Using the API

### Base URL

```
http://localhost:8080/api/v1
```

### Accounts

| Action | Method | URL |
|---|---|---|
| Create an account | POST | `/api/v1/accounts` |
| Get all accounts | GET | `/api/v1/accounts` |
| Get a single account | GET | `/api/v1/accounts/{id}` |
| Get account balance | GET | `/api/v1/accounts/{id}/balance` |
| Delete an account | DELETE | `/api/v1/accounts/{id}` |

### Contributions & Withdrawals

| Action | Method | URL |
|---|---|---|
| Make a contribution | POST | `/api/v1/accounts/{id}/contributions` |
| View contribution history | GET | `/api/v1/accounts/{id}/contributions` |
| Make a withdrawal | POST | `/api/v1/accounts/{id}/withdrawals` |

### Example: Create an Account

```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My Roth IRA",
    "ownerName": "Jane Doe",
    "ownerAge": 35,
    "type": "RETIREMENT",
    "initialBalance": 1000.00
  }'
```

### Example: Make a Contribution

```bash
curl -X POST http://localhost:8080/api/v1/accounts/1/contributions \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 500.00,
    "description": "Monthly contribution"
  }'
```

### Account Types

| Type | Description |
|---|---|
| `RETIREMENT` | Annual contribution limit applies ($23,000; $30,500 if age 50+) |
| `BROKERAGE` | No contribution limit |

---

## Swagger UI

The API includes interactive documentation powered by Swagger UI. It lets you explore and test all endpoints directly in your browser — no curl or Postman required.

**1. Open Swagger UI**

With the application running, go to:

```
http://localhost:8080/swagger-ui.html
```

**2. How to use it**

- Click on any endpoint to expand it
- Click **Try it out** to enable editing
- Fill in the request body or parameters
- Click **Execute** to send the request
- The response (status code + body) appears below

**3. View the raw OpenAPI spec**

```
http://localhost:8080/v3/api-docs
```

---

## Error Responses

All errors return a consistent JSON format:

```json
{
  "code": "ACCOUNT_NOT_FOUND",
  "message": "Account with id 5 was not found",
  "timestamp": "2026-06-04T10:15:30Z"
}
```

| Code | HTTP Status | Meaning |
|---|---|---|
| `ACCOUNT_NOT_FOUND` | 404 | Account ID does not exist |
| `INSUFFICIENT_FUNDS` | 409 | Withdrawal amount exceeds balance |
| `CONTRIBUTION_LIMIT_EXCEEDED` | 409 | Annual contribution limit reached |
| `VALIDATION_FAILED` | 400 | Invalid request body |
| `INTERNAL_ERROR` | 500 | Unexpected server error |
