# CineMan JSP application

CineMan implements the UC-03 counter ticket-sale flow and UC-04 membership registration flow using JSP, Java 8-compatible JDBC code, and MySQL 8. The implementation uses the diagram class suffix `5` consistently.

## Requirements

- JDK 8 or later and Maven 3.9+
- Apache Tomcat 9
- MySQL 8 with permission to create/alter the `cineman` database

## Database setup

For a new database, run these scripts in order:

1. `database/schema.sql`
2. `database/sampledata.sql` (optional demo data)

For a database created from the supplied schema, review `database/upgrade-existing.sql` and run it once before loading sample data. The upgrade script alters existing tables; back up the database and verify the statements against the installed schema first. The application never executes schema changes automatically.

The scripts create the database if needed and do not drop it. Database credentials are configured with JVM properties `cineman.dbUrl`, `cineman.dbUser`, and `cineman.dbPassword`, or environment variables `CINEMAN_DB_URL`, `CINEMAN_DB_USER`, and `CINEMAN_DB_PASSWORD`. Defaults target `jdbc:mysql://localhost:3306/cineman`, user `root`, and an empty password.

## Use-case mapping

- **UC-03 counter sales:** `MovieDAO` and `ShowtimeDAO` provide movie/showtime selection; `SeatShowtimeDAO` locks seats and reads their state; `InvoiceDAO` saves the invoice, tickets, seat status, voucher use, and membership spending in one transaction. The matching employee JSP flow is under `WebContent/nvbh/`.
- **UC-04 registration:** `MembershipDAO` creates a pending customer with an OTP and activates the account after successful verification. The matching registration pages are under `WebContent/kh/`.
- Model classes use the agreed suffix `5` consistently (for example `Movie5`, `Customer5`, and `Invoice5`).

## Build and deploy

Run `mvn test package` to compile the application and create `target/cineman.war`. Deploy the WAR to Tomcat 9 and visit `/cineman/`.

The JUnit database integration tests are rollback-safe and run only when explicitly enabled. Set the JVM property with `mvn -Dcineman.integrationTests=true test` and configure the database before running; load the schema and sample data first. Without the opt-in property, the integration tests are skipped.

## Demo logins

After loading `sampledata.sql`:

| Role | Username | Password |
| --- | --- | --- |
| Counter employee | `nvbh` | `nvbh12345` |
| Gold member | `thanhvienvang` | `khach12345` |
| Diamond member | `thanhviendiamond` | `khach12345` |

Counter invoices for walk-in guests leave the customer reference empty (`tblCustomerid = NULL`); no guest customer account is seeded.

## Business assumptions and demo limitations

The source material does not define detailed prices, tiers, or OTP policy. These values are centralized in `src/model/Constants.java` and are demonstrative: 10-minute seat locks, 5-minute OTP expiry, a five-attempt OTP limit, Silver/Gold/Diamond annual spending thresholds of 0/5,000,000/15,000,000 VND, tier discounts of 0/5/10%, and demo surcharges for premium seats, room formats, and peak hours. Adjust these values to match the cinema's approved business rules.

The OTP is shown in the registration flow for demonstration; production delivery requires an email/SMS provider. QR payment is a manual confirmation demo, not a bank or payment-gateway integration. Ticket QR content is derived from the ticket id and is not a cryptographically signed credential.

Membership uses `tblCustomer.trangThai` and the current-year `tblMemberYearlySpending` relation because the supplied model has no separate membership-activation table. `PENDING_OTP` transitions to `ACTIVE` after OTP confirmation; exceeding the attempt limit changes the registration to `EXPIRED`.
