FROM eclipse-temurin:17-jre-jammy

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# JVM 옵션 (시크릿/접속정보는 baked-in 하지 않음. 런타임에 docker run -e 로 주입)
# 필수 환경변수: SPRING_PROFILES_ACTIVE, DB_PASSWORD
ENV JAVA_OPTS="--add-exports java.base/sun.net.www.protocol.https=ALL-UNNAMED"

# 작업 디렉터리 설정
WORKDIR /app

COPY build/libs/koces-receipt-v1.0.jar ./app.jar

# 포트 노출 (KOCES TCP 수신 포트)
EXPOSE 10033

# 진입점
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar"]