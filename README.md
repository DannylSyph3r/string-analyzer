# String Analyzer API

A RESTful API that analyzes strings and stores their computed properties.

## Tech Stack

- Java 21
- Spring Boot 3.5.6
- H2 Database (in-memory)
- Maven

## Getting Started

```bash
mvn clean install
mvn spring-boot:run
```

API runs on `http://localhost:8080`

## Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

## API Endpoints

### POST `/strings` - Analyze String
```bash
curl -X POST http://localhost:8080/strings \
  -H "Content-Type: application/json" \
  -d '{"value": "hello world"}'
```

### GET `/strings/{value}` - Get String by Value
```bash
curl -X GET http://localhost:8080/strings/hello%20world
```

### GET `/strings` - Get All with Filters
```bash
curl -X GET "http://localhost:8080/strings?is_palindrome=true&min_length=5"
```

**Query Parameters:** `is_palindrome`, `min_length`, `max_length`, `word_count`, `contains_character`

### GET `/strings/filter-by-natural-language` - Natural Language Filter
```bash
curl -X GET "http://localhost:8080/strings/filter-by-natural-language?query=all%20single%20word%20palindromic%20strings"
```

**Example Queries:**
- `"single word palindromic strings"` → word_count=1, is_palindrome=true
- `"strings longer than 10 characters"` → min_length=11
- `"strings containing the letter z"` → contains_character=z

### DELETE `/strings/{value}` - Delete String
```bash
curl -X DELETE http://localhost:8080/strings/hello%20world
```

## String Properties

- **length** - Total count of all characters (including spaces, punctuation)
- **is_palindrome** - Case-insensitive palindrome check (includes spaces and punctuation)
- **unique_characters** - Distinct characters (case-sensitive)
- **word_count** - Number of words
- **sha256_hash** - SHA-256 hash (hex) serving as unique identifier
- **character_frequency_map** - Character occurrence counts (case-sensitive)