FROM eclipse-temurin:17-jre-jammy

ARG PROFILE

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV SPRING_PROFILES_ACTIVE=$PROFILE
ENV JAVA_OPTS="--add-exports java.base/sun.net.www.protocol.https=ALL-UNNAMED"

# 작업 디렉터리 설정
WORKDIR /app

COPY build/libs/kocesReceipt-v1.0.jar ./app.jar

# 포트 노출 (스프링 부트 기본 포트)
EXPOSE 10033

# 진입점
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar"]