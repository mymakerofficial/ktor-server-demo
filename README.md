# ktor-server-demo
This project is a proof of concept and educational project to learn the Ktor framework to build a media server.

### Features
- Creating users
- Authentication using JWT's
- Uploading media files
- **Automatic preview generation for images and videos!**
- Requesting uploaded files and previews securely

## How to run
- create the database container
```
docker-compose up
```
- create the database model using the SQL found in `src/main/resources/db/migration/changelog.sql`

- run the server
```bash
./gradlew run
```

## Documentation
run the server and go to
[http://127.0.0.1:8080/swagger-ui](http://127.0.0.1:8080/swagger-ui)
