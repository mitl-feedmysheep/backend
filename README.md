# 🙏 IntoTheHeaven

> A platform for managing prayer topics and life sharing in church small groups.

---

## 📚 Philosophy

> _"A record of community's walk with God."_

- What have we prayed for as a group?
- What themes keep reappearing?
- How has God answered over time?

This is not just a utility — it's a **spiritual reflection tool**.

---

## 🎯 Purpose

**IntoTheHeaven** exists to help small groups in churches record, organize, and reflect on what was shared and prayed in each meeting.

Unlike sermon-based journaling tools, this platform focuses on **group-centered spiritual history**.

---

## 👥 Target Users

### 🟦 Group Leaders

- Organize and lead small group meetings
- Record each member's sharing and prayer topics
- Track spiritual growth over time

### 🟨 Regular Members

- Participate in meetings
- Look back on what they shared or prayed
- Prefer minimal, focused interaction

---

## 🧩 Core Features (MVP)

- Login via email/password
- View past group meetings
- Create new meetings
- Record member attendance, sharing, and prayer topics

---

## 🧱 Tech Stack

| Layer    | Stack                                   |
| -------- | --------------------------------------- |
| Frontend | React Native (TypeScript)               |
| Backend  | Spring Boot (Java 17+)                  |
| Database | MySQL (UUID as `CHAR(36)`)              |
| Auth     | JWT                                     |
| Infra    | Vercel (FE), HomeServer(Later AWS) (BE) |
| Docs     | Swagger via SpringDoc (auto-generated)  |

---

## 📐 Architecture

- Hexagonal Clean Architecture
- Focus on RESTful
- Incorporates principles of Domain-Driven Design (DDD).

---

## 🏁 Getting Started

### Local Setup

1.  **Set up the local test database:**

    - Install Docker.
    - Pull the MySQL 8.0 image:
      ```bash
      docker pull mysql:8.0
      ```
    - Run the MySQL container in the background:
      ```bash
      docker run --name into_the_heaven -e MYSQL_ROOT_PASSWORD=intotheheaven -e MYSQL_DATABASE=intotheheaven -d -p 3306:3306 mysql:8.0
      ```

2.  **Run the server:**

    - Activate the `local` profile in your IDE (e.g., IntelliJ) to use `application-local.yml`.

3.  **Initial Data Seeding:**
    - This feature is under development.

---

## 🤝 Collaboration Tools

- **SwaggerUI**: For API documentation and communication between Frontend and Backend.
- **ERD**: The database schema is available on
  - `into-the-heaven.erd.json`
  - Expanded version: [ERDCloud](https://www.erdcloud.com/d/7PhCjKPXwjPcS5uiP).
- **Notion**: For project documentation.

- @deprecated
  - **Slack**: For team communication.
  - **Google Meet/Zoom**: For weekly meetings.

---

## ✨ Code Style

- We follow the [Google Java Style Guide](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml).

---

## 🔐 API Logic

### Token Refresh Flow

1.  Upon successful login, the server issues an `accessToken` only.
2.  When the `accessToken` expires, the user is asked to re-login by being redirected to the login page.

---

## 🧪 Testing

- Unit and integration tests will focus on the core domain logic.

---

## 📚 Documentation

### 🎨 Design & Architecture
- [Technical Strategy](https://shimmering-jacket-601.notion.site/Technical-Strategy-21b022064a508095a6a0f85e8280bfef?source=copy_link)
- [Architecture](https://github.com/mitl-feedmysheep/backend/blob/main/.cursor/rules/convention.mdc)

### 📋 Policies
- [Policy](https://shimmering-jacket-601.notion.site/Policy-21d022064a50804191a4e0c7586dddc6?source=copy_link)

### 🔌 API References
- [API Lists](https://shimmering-jacket-601.notion.site/API-Lists-21d022064a5080e493e2cc9c3adb6250?source=copy_link)

### 🌱 Development Resources
- [Seed Data](https://shimmering-jacket-601.notion.site/Seed-Data-234022064a5080bfbc2acd82a5e92df0?source=copy_link)

### 🏗️ Infrastructure
- [Infrastructure](https://shimmering-jacket-601.notion.site/Infrastructure-234022064a5080dfb67cce33547b7f12?source=copy_link)