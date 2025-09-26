.PHONY: all contracts tms admin frontend run-backend run-frontend docker-up docker-down clean

# ===== Build =====

all: contracts tms admin frontend

contracts:
	cd contracts-api && mvn clean install -DskipTests

tms:
	cd tms && mvn clean install -DskipTests

admin:
	cd tms-sb-admin && mvn clean install -DskipTests || echo "⚠️ tms-sb-admin skipped"

frontend:
	cd frontend && pnpm install && pnpm run build

# ===== Run =====

run-backend:
	cd tms/tms-service && mvn spring-boot:run -Dspring-boot.run.profiles=dev

run-frontend:
	cd frontend && pnpm start

# ===== Infra =====

docker-up:
	cd infra && docker compose up -d

docker-down:
	cd infra && docker compose down

# ===== Clean =====

clean:
	cd contracts-api && mvn clean
	cd tms && mvn clean
	cd tms-sb-admin && mvn clean || true
	cd frontend && rm -rf node_modules dist
