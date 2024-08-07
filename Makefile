# shell colors
COLOUR_GREEN=\033[0;32m
COLOUR_RED=\033[0;31m
COLOUR_BLUE=\033[0;34m
COLOUR_END=\033[0m

java-app-build: # Builds the JAR from the Java source code.
	@echo "$(COLOUR_GREEN)Building the JAR file ...$(COLOUR_END)"
	mvn -f service/pom.xml package org.apache.maven.plugins:maven-shade-plugin:shade

run-jar: # Uses Java to run the app and collect the config files. While running, execute some tests to ensure all necessary classes will be executed and registered on config files.
	java -agentlib:native-image-agent=config-merge-dir=./service/config -jar service/target/sparkjava-graalvm*.jar

run-collecting-config: # Run tests while running Java app to collect config file.
	podman compose -f sql/pg-compose.yaml up -d; 
	sleep 5; \
	make run-jar & \
	sleep 5; \
	./executar-teste-para-config.sh; \
	bash -c 'kill $(pgrep -n java);' \
	podman compose -f sql/pg-compose.yaml down

generate-native-app: # With GraalVM's native-image, generate a native app from the JAR file
	native-image \
	--verbose \
	-H:ConfigurationFileDirectories=./service/config  \
    --enable-http \
	--no-fallback \
    --initialize-at-build-time=org.eclipse.jetty,org.slf4j,javax.servlet,org.zoomba-lang,com.zaxxer \
	-march=native \
    -jar service/target/sparkjava-graalvm-service-1.2.0.jar native-app

run-tests: # Start enviroment and execute load tests
	@echo "$(COLOUR_GREEN)	Starting the environment ...$(COLOUR_END)"
	podman compose -f docker-compose.yml up --detach
	@echo "$(COLOUR_GREEN)	Execute load tests ...$(COLOUR_END)"
	./executar-teste-local.sh
	podman compose -f docker-compose.yml down

lint-dockerfile: # Uses hadolint as a Dockerfile Linter
	@echo "$(COLOUR_GREEN)	Execute hadolint to lint the Dockerfile ...$(COLOUR_END)"
	podman run --rm -v ./Dockerfile:/Dockerfile hadolint/hadolint hadolint /Dockerfile
#: #########################################
#: ############ Help - Makefile ############
#: #########################################

help: # list all Makefile commands
	@echo "$(COLOUR_BLUE)These are all the avalaible commands ...$(COLOUR_END)"
	@echo ""
	@grep ': #' Makefile