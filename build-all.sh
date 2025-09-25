#!/usr/bin/env bash
set -e  # stop on first error

echo "📦 Building contracts-api..."
cd contracts-api
mvn clean install -DskipTests
cd ..

echo "🚀 Building tms (service + db)..."
cd tms
mvn clean install -DskipTests
cd ..

echo "📊 Building tms-sb-admin (optional)..."
cd tms-sb-admin
mvn clean install -DskipTests || echo "⚠️ tms-sb-admin skipped"
cd ..

echo "🖥️ Building frontend..."
cd frontend
npm install
npm run codegen
npm run build
cd ..

echo "✅ All projects built successfully!"
