# Docker Setup for Portfolio Backend

This document provides instructions for running the Portfolio Backend application using Docker.

## Prerequisites

- Docker installed on your system
- Docker Compose installed
- `.env` file with required environment variables

## Quick Start

### Using Docker Compose (Recommended)

1. Build and run the application:

```bash
docker compose up --build
```

2. The application will be available at `http://localhost:8080`

3. To run in detached mode:

```bash
docker compose up -d --build
```

4. To stop the application:

```bash
docker compose down
```

### Using Docker directly

1. Build the Docker image:

```bash
docker build -t portfolio-backend .
```

2. Run the container:

```bash
docker run -d \
  --name portfolio-backend \
  -p 8080:8080 \
  --env-file .env \
  portfolio-backend
```

3. Stop the container:

```bash
docker stop portfolio-backend
docker rm portfolio-backend
```

## Available Endpoints

Once the application is running, you can access:

- **Health Check**: `http://localhost:8080/q/health`
- **Application Endpoints**: Based on your ChatController implementation

## Docker Images

The application uses a multi-stage build process:

1. **Build Stage**: Uses OpenJDK 21 to compile the Quarkus application
2. **Runtime Stage**: Creates a minimal runtime image with only the necessary artifacts

## Troubleshooting

### Health Check Issues

If the health check fails, ensure:

- The application is properly started
- Port 8080 is not blocked
- Environment variables are correctly set

### Build Issues

If the Docker build fails:

- Ensure you have sufficient disk space
- Check that all required files are present
- Verify your internet connection for dependency downloads

## Development

For development with hot reload, you might want to mount the source code:

```bash
docker run -d \
  --name portfolio-backend-dev \
  -p 8080:8080 \
  -v $(pwd)/src:/app/src \
  --env-file .env \
  portfolio-backend
```

## Production Considerations

For production deployment:

1. Use specific image tags instead of `latest`
2. Set appropriate resource limits
3. Configure proper logging
4. Use secrets management for sensitive environment variables
5. Set up monitoring and alerting

## Logs

To view application logs:

```bash
# Using docker-compose
docker-compose logs -f portfolio-backend

# Using docker directly
docker logs -f portfolio-backend
```
