# BrainDump Backend

https://braindump-frontend.onrender.com/

BrainDump Backend är serverdelen till BrainDump, en applikation för att samla, organisera och kategorisera idéer. Backend hanterar bland annat tavlor (*boards*), idéer och deltagare samt kommunicerar med databasen och AI-tjänsten för automatisk "nyckelords-kategorisering".

Backend är byggd i Java med Quarkus och exponerar ett REST API som används av Angular-frontend.

## Funktioner

- Skapa och hämta tavlor (*boards*).
- Hantera deltagare på tavlor.
- Skapa, hämta och radera idéer.
- Hämta idéer kopplade till en specifik tavla.
- Automatisk kategorisering av idéer med hjälp av AI.
- Lagring av information i PostgreSQL.
- REST API för kommunikation med frontend.

## Tekniker

- **Java 21** – programmeringsspråk.
- **Quarkus 3.40.1** – backendramverk.
- **Maven** – byggverktyg och beroendehantering.
- **PostgreSQL** – relationsdatabas.
- **Hibernate ORM / Panache** – databasinteraktion.
- **LangChain4j och OpenAI** – AI-baserad kategorisering av idéer.
- **Render** – hosting och driftsättning av backend.
- **Neon** - Databas host

## Kategorisering av idéer med AI

Backend använder LangChain4j och OpenAI för att automatiskt kategorisera idéer när de skapas.

Följande kategorier används i applikationen:

| Kategori | Beskrivning |
|---|---|
| `NEW_IDEA` | Nya idéer och förslag |
| `IMPROVEMENT` | Förbättringar |
| `PROBLEM_SOLVING` | Problemlösning |
| `PROCESS_AND_WAYS_OF_WORKING` | Processer och arbetssätt |
| `ACTIVITY_AND_PROJECT` | Aktiviteter och projekt |
| `CONTENT_AND_COMMUNICATION` | Innehåll och kommunikation |
| `OTHER` | Övriga idéer |


## REST API

Backend exponerar REST-endpoints som används av frontend.

### Tavlor – Boards

| Metod | Endpoint | Funktion |
|---|---|---|
| `GET` | `/boards` | Hämtar alla tavlor |
| `GET` | `/boards/{id}` | Hämtar en specifik tavla |
| `POST` | `/boards/create` | Skapar en ny tavla |
| `POST` | `/boards/{boardId}/participants` | Lägger till en deltagare på en tavla |

### Idéer – Ideas

| Metod | Endpoint | Funktion |
|---|---|---|
| `GET` | `/ideas` | Hämtar idéer |
| `GET` | `/ideas/board/{boardId}` | Hämtar idéer för en specifik tavla |
| `POST` | `/ideas/create` | Skapar en ny idé |
| `DELETE` | `/ideas/{id}` | Raderar en specifik idé |
| `DELETE` | `/ideas/board/{boardId}` | Raderar idéer kopplade till en tavla |

## Konfiguration

Konfigurationen finns i:

```text
src/main/resources/application.properties
```

### CORS

Exempel på konfiguration för lokal utveckling:

```properties
quarkus.http.cors.enabled=true
quarkus.http.cors.origins=http://localhost:4200
quarkus.http.cors.methods=GET,POST,PUT,DELETE,OPTIONS
quarkus.http.cors.headers=accept,authorization,content-type,x-requested-with
```

I produktion behöver frontendens publicerade URL tillåtas i stället för, eller utöver, den lokala utvecklingsadressen. Tillåt endast de domäner som faktiskt ska använda API:t.

### OpenAI

API-nyckeln konfigureras via en miljövariabel:

```properties
quarkus.langchain4j.openai.api-key=${OPENAI_API_KEY}
```

Miljövariabeln `OPENAI_API_KEY` måste finnas i den miljö där backend körs.

### PostgreSQL

Backend använder PostgreSQL för att lagra applikationens data. I produktion måste databasanslutningen konfigureras med rätt databas-URL, användarnamn och lösenord.

Exempel på konfiguration för produktion:

```properties
quarkus.datasource.db-kind=postgresql
%prod.quarkus.datasource.jdbc.url=${DB_URL}
%prod.quarkus.datasource.username=${DB_USERNAME}
%prod.quarkus.datasource.password=${DB_PASSWORD}
```

Miljövariablerna `URL_DB`, `USERNAME_DB` och `PASSWORD_DB` måste konfigureras i Render och peka på rätt databas. Anpassa inställningarna efter projektets befintliga konfiguration.

## Köra projektet lokalt

### Förutsättningar

- Java 21
- Maven eller projektets Maven Wrapper
- PostgreSQL, om projektet inte använder en separat utvecklingsdatabas
- En OpenAI API-nyckel om AI-funktionerna ska användas

### 1. Klona repot

```bash
git clone https://github.com/Twitty0502/braindump_backend.git
cd braindump_backend
```

### 2. Konfigurera miljövariabler

Sätt `OPENAI_API_KEY` i din lokala utvecklingsmiljö. Om projektet använder en separat PostgreSQL-databas behöver även databasinställningarna konfigureras.

Exempel:
```bash
export OPENAI_API_KEY="din-api-nyckel"
```

Använd din riktiga API-nyckel lokalt och dela den inte i källkod eller loggar.

### 3. Starta backend

Med Maven Wrapper:

```bash
./mvnw quarkus:dev
```

Quarkus kör normalt utvecklingsservern på:

```text
http://localhost:8080
```

Den faktiska adressen kan ändras genom projektets konfiguration.


### Miljövariabler i Render

Konfigurera de miljövariabler som backend behöver, exempelvis:

| Variabel | Användning |
|---|---|
| `OPENAI_API_KEY` | OpenAI API-nyckel |
| `URL_DB` | JDBC-anslutningsadress till PostgreSQL |
| `USERNAME_DB` | Databasens användarnamn |
| `PASSWORD_DB` | Databasens lösenord |

Variablerna ovan måste stämma överens med namnen i `application.properties`. Om projektet använder andra variabelnamn ska de befintliga namnen behållas eller konfigurationen uppdateras konsekvent.


## Vidare utveckling

Möjliga framtida förbättringar är:

- Automatiserade tester för REST-endpoints.
- Dokumentation av API:t med OpenAPI/Swagger.
- Automatiserade databasmigrationer.
- Förbättrad felhantering och loggning.
- Tydligare separering mellan utvecklings- och produktionskonfiguration.


## Diverse länkar till använda guider:

- Hibernate ORM ([guide](https://quarkus.io/guides/hibernate-orm)): Object-relational mapping with JPA/Hibernate for relational database access
- Hibernate Validator ([guide](https://quarkus.io/guides/validation)): Bean validation using Hibernate Validator and Jakarta Validation annotations
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Generate OpenAPI schemas and serve Swagger UI for REST API documentation
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
- WebSockets ([guide](https://quarkus.io/guides/websockets)): WebSocket communication channel support
- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplified JPA/Hibernate data access layer with active record and repository patterns
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC


