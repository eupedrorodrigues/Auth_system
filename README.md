# Authentication API

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

This project is an API built using **Java, Java Spring, PostgreSQL, JWT, Mockito, JUnit H2 as the database.**

## Table of Contents

- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Database](#database)
- [Contributing](#contributing)

## Installation

1. Clone the repository:

```bash
git clone https://github.com/Sayonarakeroll/Auth_system.git
```

2. Install dependencies with Maven
3. Install Postman or Insomnia to test the methods: POST and GET

## Usage

1. Start the application with Maven
2. The API will be accessible at http://localhost:8080

## API Endpoints

The API provides the following endpoints:

```markdown
POST /auth/register - Register a new user into the App

POST /auth/login - Login into the App

GET /auth/users - List all users
```

## Database

The project utilizes [PostgreSQL](https://www.postgresql.org/) and [H2 Database](https://www.h2database.com/html/tutorial.html) as the database.
