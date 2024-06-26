# builder image
FROM ghcr.io/graalvm/graalvm-community:21.0.2-ol9-20240116 AS builder
WORKDIR /build
COPY ./service/pom.xml /build
COPY ./service/src /build/src
COPY ./service/config /build/config

RUN curl -q -fsSL -o apache-maven.tar.gz https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz \
    && tar xzvf apache-maven.tar.gz \
    && apache-maven-3.9.6/bin/mvn -f pom.xml package org.apache.maven.plugins:maven-shade-plugin:shade

RUN native-image \
    --verbose \
    --enable-http \
    --no-fallback \
    -march=native \
    --initialize-at-build-time=org.eclipse.jetty,org.slf4j,javax.servlet,org.zoomba-lang,com.zaxxer \
    -H:ConfigurationFileDirectories=./config  \
    -jar ./target/sparkjava-graalvm-service-1.5.1.jar \
    -o native-sparkjava-graalvm-service-app

# The deployment image
FROM docker.io/library/alpine:20240315
RUN apk add --no-cache gcompat=1.1.0-r4
WORKDIR /app
COPY --from=builder /build/native-sparkjava-graalvm-service-app native
EXPOSE 8080
ENTRYPOINT ["/app/native"]
