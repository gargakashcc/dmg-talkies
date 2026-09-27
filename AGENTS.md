# Contributor guide

## Project

DMG Talkies is a Spring Boot 4 / Java 21 movie-ticket booking API. It uses Spring MVC, Spring Security, JPA, Bean Validation, and an in-memory H2 database for local development.

## Commands

```bash
./gradlew test
./gradlew bootRun
```

Run the full test suite after Java or configuration changes. The application starts on port 8080; H2 is available at `/h2-console` with `jdbc:h2:mem:movies`.

## Code map

- `Domain.java` — JPA entities and booking/seat status enums.
- `Repositories.java` — Spring Data repositories, including the locked seat query.
- `BookingService.java` — transactional hold, confirmation, cancellation, expiry, pricing, and notifications.
- `Controllers.java` — customer/admin REST endpoints, request DTOs, and API error mapping.
- `Security.java` — development Basic-auth users and endpoint role rules.
- `DemoData.java` — idempotent development catalogue, discount, and refund-policy seeder.
- `src/test` — Spring integration tests for the booking flow.

## Non-negotiable booking invariants

- Never allocate a seat unless its `ShowSeat` row is locked in the same transaction. `SeatRepo.lock(...)` is the concurrency boundary.
- A held seat may be confirmed only by the booking that owns its hold, before its expiry timestamp.
- Releasing a hold or cancelling a booking must clear `holdBookingId` and `holdUntil` as well as restoring `AVAILABLE` status.
- Persist the calculated booking total at hold time; do not recalculate it after confirmation.
- Keep notifications asynchronous so external delivery cannot delay booking completion.

## Scope and style

- Keep the existing package-level entity model unless a new requirement needs broader reuse.
- Validate all request payloads at the controller boundary and return client-safe error messages.
- Keep admin routes under `/api/admin/**` and customer-facing routes under `/api/**`; update security rules with any new route.
- Do not expose payment gateway integration, real delivery providers, or production credentials in this repository. The payment token and log-backed notifier are intentional boundaries.
- Document any user-visible API or business-rule change in `README.md`.
