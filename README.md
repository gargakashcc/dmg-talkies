# DMG Talkies booking API

A Spring Boot 4 / Java 21 REST API for city-based cinema discovery and seat-level ticket booking. It supports time-bound holds, safe concurrent booking, pricing tiers, discount codes, payment confirmation, cancellation, refunds, and role-based administration.

## Run locally

```bash
./gradlew test
./gradlew bootRun
```

The app runs at `http://localhost:8080`. It uses an in-memory H2 database, so data resets on restart. The H2 console is `http://localhost:8080/h2-console`, using `jdbc:h2:mem:movies`.

On startup, a small demo catalogue is created: Mumbai and Delhi theaters, three upcoming shows, eight seats per show, `WELCOME10`/`MOVIE20` discounts, and 48-hour/24-hour/2-hour refund policies. The seeder skips itself if city data already exists.

All `/api/**` endpoints use HTTP Basic authentication:

| User | Password | Role |
| --- | --- | --- |
| `admin` | `admin` | Admin |
| `customer` | `customer` | Customer |

These are development-only accounts. Production should use persisted, hashed identities and OIDC/OAuth.

## API overview

| Role | Endpoint | Purpose |
| --- | --- | --- |
| Customer/Admin | `GET /api/cities` | List cities |
| Customer/Admin | `GET /api/cities/{cityId}/shows` | List active city shows |
| Customer/Admin | `GET /api/shows/{showId}/seats` | See seat availability |
| Customer/Admin | `POST /api/bookings/hold` | Hold seats for ten minutes |
| Customer/Admin | `POST /api/bookings/{bookingId}/confirm` | Confirm a hold after payment authorization |
| Customer/Admin | `POST /api/bookings/{bookingId}/cancel` | Cancel and calculate refund |
| Customer/Admin | `GET /api/bookings` | Current user's booking history |
| Admin | `POST /api/admin/cities` | Create a city |
| Admin | `POST /api/admin/theaters` | Create a theater |
| Admin | `POST /api/admin/shows` | Create a show and prices |
| Admin | `POST /api/admin/shows/{showId}/seats` | Add a seat to a show layout |
| Admin | `POST /api/admin/discounts` | Create a percentage discount code |
| Admin | `POST /api/admin/refund-policies` | Create a refund rule |

Invalid input returns `400`, unavailable/concurrently-held seats return `409`, unauthenticated calls return `401`, and insufficient roles return `403`. Errors use `{"error":"..."}`.

## Typical booking flow

First an admin creates the catalogue, then a customer holds and confirms seats:

```bash
curl -u admin:admin -X POST localhost:8080/api/admin/cities \
  -H 'Content-Type: application/json' -d '{"name":"Mumbai"}'

curl -u customer:customer -X POST localhost:8080/api/bookings/hold \
  -H 'Content-Type: application/json' \
  -d '{"showId":1,"seats":["A1","A2"],"discountCode":"WELCOME10"}'

curl -u customer:customer -X POST localhost:8080/api/bookings/1/confirm \
  -H 'Content-Type: application/json' -d '{"paymentToken":"gateway-token"}'
```

Example admin payloads:

```json
POST /api/admin/shows
{"theaterId":1,"title":"The Film","startsAt":"2026-10-01T19:30:00","regularPrice":250,"premiumPrice":400,"weekendMarkupPercent":15}
```

```json
POST /api/admin/refund-policies
{"minimumHours":24,"refundPercent":80}
```

## Booking and pricing rules

- Holds expire after ten minutes. A scheduled task releases expired holds, and booking requests also clear expirations before allocating seats.
- Seat rows are pessimistically locked during hold, confirmation, and cancellation. Simultaneous attempts serialize; only one can allocate a seat.
- For example, when 20 customers simultaneously request the final two seats, one hold succeeds and the other 19 receive `409 Conflict`. The integration suite exercises this exact case.
- Regular and premium prices are set per show. Saturday/Sunday apply the configured markup. One valid active percentage discount applies, and total price is captured at hold time.
- A payment token represents successful external authorization. Gateway capture/webhooks are outside this service.
- Cancellation is permitted only for confirmed bookings. The active policy with the highest qualifying `minimumHours` determines the refund percentage; seats are released immediately. Actual gateway refunds are external.
- Confirmation and cancellation notifications run asynchronously and never delay the booking response. The demo notifier logs them.

## Design assumptions and limits

- Seats belong directly to a show, rather than to reusable auditorium templates, to keep layouts independently editable.
- Customer ownership is enforced for confirmation, cancellation, and history. Admins do not receive a cross-customer booking-search endpoint.
- Money uses `BigDecimal`; currency is omitted because none was specified. Add ISO currency before multi-currency support.
- H2 provides zero-setup local persistence. PostgreSQL is included as a runtime driver and is preferred for production, with migrations and credentials supplied by deployment configuration.

## Tests

`./gradlew test` runs the Spring context test and an integration test covering a hold, competing-hold rejection, and confirmation to a booked seat.
