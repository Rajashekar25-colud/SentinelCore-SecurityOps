@echo off
echo ========================================================================
echo SentinelCore SecureOps - Starting Backend Server with SMTP Configuration
echo ========================================================================
set SMTP_HOST=smtp.gmail.com
set SMTP_PORT=587
set SMTP_USERNAME=prashanthkavuri019@gmail.com
if "%SMTP_PASSWORD%"=="" set SMTP_PASSWORD=SET_YOUR_SMTP_APP_PASSWORD
echo SMTP environment variables set successfully.
echo Launching Spring Boot...
call mvnw.cmd spring-boot:run
