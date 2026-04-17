# 🎮 Twitch Recommendation Server
 
A full-stack real-time video recommendation application that surfaces live Twitch streams, clips, and videos tailored to each user based on their favorites history and collaborative filtering across users.
 
> ⚠️ **Note:** Cloud deployment (AWS App Runner / RDS) is currently being revamped. Local deployment is fully functional.
 
---
 
## 📸 Screenshots
 
| Browse Streams | Favorites Panel | Search Games |
|---|---|---|
| Browse live IRL streams by category | Save favorites and get personalized picks | Search games by keyword |
 
*(See `/screenshots` folder for full-resolution images)*
 
---
 
## ✨ Features
 
- **Browse** top live streams and VODs by game category (IRL, Just Chatting, FPS, etc.)
- **Search** games and filter streams by keyword via the ADV search
- **User accounts** — register, log in, and log out
- **Favorites** — star streams or videos; access them anytime from the Favorites panel
- **Personalized recommendations** — collaborative filtering suggests content based on what users with similar favorites are watching
- **Caffeine in-memory caching** — reduces redundant Twitch API calls and lowers response latency
---
 
## 🛠 Tech Stack
 
| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot, Spring Security (OAuth2), OpenFeign |
| Frontend | React, Vite |
| Database | MySQL |
| Caching | Spring Boot + Caffeine |
| Containerization | Docker, Docker Compose |
| Cloud (WIP) | AWS App Runner, AWS RDS |
 
---
 
## 📁 Project Structure
 
```
TwitchRecommendationServer/   ← this repo (backend, branch: backend)
├── src/
├── application.yml.example   ← Spring Boot config template
├── docker-compose.yml.example ← MySQL local container template
├── build.gradle
└── ...
 
frontend/                     ← separate repo (React + Vite)
```
 
---
 
## ⚙️ Local Setup
 
### Prerequisites
 
- Java 17+
- Node.js 18+ & npm
- Docker Desktop (for MySQL)
- A [Twitch Developer Application](https://dev.twitch.tv/console/apps) (to get a Client ID and Client Secret)
---
 
### 1. Clone the repositories
 
```bash
# Backend
git clone -b backend https://github.com/strawoari/TwitchRecommendationServer.git
cd TwitchRecommendationServer
 
# Frontend (in a separate directory)
git clone -b frontend https://github.com/strawoari/TwitchRecommendationServer.git twitch-frontend
```
 
---
 
### 2. Start MySQL with Docker
 
Copy the example Docker Compose file and start the container:
 
```bash
cp docker-compose.yml.example docker-compose.yml
docker compose up -d
```
 
The container exposes MySQL on **port 3307** locally (mapped from 3306 inside the container). The database named `database` will be created automatically. Update the credentials in `docker-compose.yml` if desired.
 
---
 
### 3. Configure the backend
 
Copy the example config and fill in your credentials:
 
```bash
cp application.yml.example src/main/resources/application.yml
```
 
Edit `application.yml` and replace the placeholder values:
 
```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          twitch-api:
            default-request-headers:
              Client-Id: "YOUR_TWITCH_CLIENT_ID"   # ← replace
  security:
    oauth2:
      client:
        registration:
          twitch:
            client-id: "YOUR_TWITCH_CLIENT_ID"     # ← replace
            client-secret: "YOUR_TWITCH_CLIENT_SECRET" # ← replace
  datasource:
    url: jdbc:mysql://localhost:3307/twitch?createDatabaseIfNotExist=true
    username: root        # ← match docker-compose
    password: password    # ← match docker-compose
```
 
The database schema is initialized automatically on first startup via `classpath:database-init.sql`.
 
---
 
### 4. Run the backend
 
```bash
./gradlew bootRun
```
 
The server starts on **http://localhost:8080** by default.
 
---
 
### 5. Run the frontend
 
```bash
cd twitch-frontend
npm install
npm run dev
```
 
The UI is available at **http://localhost:5173**.
 
---
 
## 🔑 Twitch API Credentials
 
1. Go to [https://dev.twitch.tv/console/apps](https://dev.twitch.tv/console/apps)
2. Click **Register Your Application**
3. Set OAuth Redirect URL to `http://localhost`
4. Copy your **Client ID** and generate a **Client Secret**
5. Paste them into `application.yml` as shown above
---
 
## 🧠 How Recommendations Work
 
When a logged-in user opens the app, the backend:
 
1. Queries the user's favorited streams/videos from MySQL.
2. Finds other users who share at least one favorite in common (collaborative filtering).
3. Collects the favorites of those similar users that the current user hasn't seen yet.
4. Returns the top results, ranked by overlap frequency.
Results are cached with Caffeine (1-minute TTL) to avoid redundant DB and API calls.
 
---
 
## 📈 Performance Notes
 
- **~8% reduction in query latency** via targeted MySQL indexes on high-frequency lookup columns (user ID, game ID, item type).
- **~30% reduction in API response latency** via Spring Boot + Caffeine in-memory caching of Twitch API responses.
---
 
## 🚧 Cloud Deployment (WIP)
 
The application was previously deployed to:
 
- **AWS App Runner** — containerized Spring Boot backend
- **AWS RDS (MySQL)** — managed database
This cloud infrastructure is currently being revamped. Local deployment via Docker + Gradle is the recommended path for now.
