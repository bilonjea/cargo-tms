@echo off
setlocal

echo 📦 Building contracts-api...
cd contracts-api
call mvn clean install -DskipTests
cd ..

echo 🚀 Building tms (service + db)...
cd tms
call mvn clean install -DskipTests
cd ..

echo 📊 Building tms-sb-admin (optional)...
cd tms-sb-admin
call mvn clean install -DskipTests
cd ..

echo 🖥️ Building frontend...
cd frontend
call pnpm install
call pnpm run codegen
call pnpm run build
cd ..

echo ✅ All projects built successfully!
endlocal
pause
