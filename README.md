# Bands API

A Spring Boot REST API that proxies and serves band data from an external source, with in-memory caching, search, and sorting support.

#### Before running the app, open `src/main/resources/application.yaml` and set the external bands API URL and timeout values to match your environment.

---

## Tech stack

| Layer | Technology |
|---|---|
| Runtime | Java 25 |
| Framework | Spring Boot 4.0.6 |
| HTTP client | RestClient (`spring-boot-starter-restclient`) |
| Cache | Caffeine |
| Docs | SpringDoc OpenAPI 3.0.3 (Swagger UI) |
| Monitoring | Spring Boot Actuator |
| Logging | SLF4J |
| Boilerplate | Lombok |

---

## Prerequisites

- Java 25+
- Maven 3.9+

---

## Configuration

Before running the app, open `src/main/resources/application.yaml` and set the external bands API URL and timeout values to match your environment.

```yaml
bands:
  api:
    url: https://bands-api.vercel.app/api/bands   # required — URL of the external bands endpoint
    connection-timeout: 3000                         # milliseconds
    read-timeout: 5000                               # milliseconds
```

---

## Running the app

```bash
mvn spring-boot:run
```

The app starts on **`http://localhost:8080`** by default.

---

## API endpoints

### `GET /api/bands`

Returns all bands. Both parameters are optional and can be combined.

| Parameter | Type | Description |
|---|---|---|
| `q` | `string` | Case-insensitive partial name filter, e.g. `metal` |
| `sort` | `ALPHABETICAL` \| `POPULARITY` | Sort order: alphabetical (A→Z) or by play count (most played first) |

**Examples:**

```
GET /api/bands
GET /api/bands?sort=ALPHABETICAL
GET /api/bands?sort=POPULARITY
GET /api/bands?q=metal
GET /api/bands?q=metal&sort=ALPHABETICAL
GET /api/bands?q=metal&sort=POPULARITY
```

### `GET /api/bands/{id}`

Returns a single band by ID. Returns `404` if no band with that ID exists.

```
GET /api/bands/1
```

---

## Swagger UI

Interactive API documentation is available at:

```
http://localhost:8080/swagger-ui.html
```

Raw OpenAPI spec (JSON):

```
http://localhost:8080/v3/api-docs
```

> Both are enabled via `application.yaml`:
> ```yaml
> springdoc:
>   api-docs:
>     enabled: true
>   swagger-ui:
>     enabled: true
> ```

---

## Actuator

Health and operational endpoints are exposed at:

| Endpoint | URL |
|---|---|
| All endpoints | `http://localhost:8080/actuator` |
| Health | `http://localhost:8080/actuator/health` |
| Caches | `http://localhost:8080/actuator/caches` |
| Metrics | `http://localhost:8080/actuator/metrics` |
| Info | `http://localhost:8080/actuator/info` |

---

## Postman collection

A ready-to-import Postman collection is included at `bands-api.postman_collection.json`. It covers all endpoint variations:

- All bands (unsorted, alphabetical, by popularity)
- Search by name (with and without sort)
- Search with no results
- Get band by ID (found and not found)
- All Actuator endpoints

**To import:** open Postman → **File → Import** → select the `.json` file.

The collection uses a `baseUrl` variable defaulting to `http://localhost:8080`. Change it in the collection's **Variables** tab if your setup differs.

---

## Running the tests

```bash
mvn test
```

The test suite covers:

- `BandServiceTest` — filtering, sorting, cache behaviour, not-found handling
- `BandControllerTest` — all endpoint variations via `@WebMvcTest` + MockMvc
- `BandsApiClientTest` — HTTP-level tests via `MockRestServiceServer`
