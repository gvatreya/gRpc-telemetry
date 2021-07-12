
.Phony: install
install:
	echo "Building and installing Telemetry Server"
	./mvnw clean install package -DskipTests

start:
	echo "Starting Telemetry server on port 8980"
	./mvnw exec:java -Dexec.mainClass=in.gvatreya.telemetry.TelemetryDashboardServer