package in.gvatreya.telemetry;

import in.gvatreya.telemetry.dashboard.Telemetry;
import in.gvatreya.telemetry.service.TelemetryDashboardService;
import in.gvatreya.telemetry.utils.Utility;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TelemetryDashboardServer {

    private static final Logger logger = Logger.getLogger(TelemetryDashboardServer.class.getName());

    // FIXME: fetch from env or a properties file
    private final int port;
    private final Server server;

    /**
     * Initializes the server on the given port.
     * This also populates the data required for the server to operate,
     * by reading the default telemetry CSV file.
     * @param port the port to listen on
     * @throws IOException on file handling.
     * @throws ParseException when unable to parse the CSV.
     */
    public TelemetryDashboardServer(int port) throws IOException, ParseException {
        this(ServerBuilder.forPort(port), port, Utility.parseTelemetries(Utility.getDefaultFeaturesFile()));
    }

    /**
     * Helper method that creates a TelemetryDashboard Server using
     * the serverBuilder and pre-populated telemetry data.
     * @param serverBuilder Server for listening for and dispatching
     *                      incoming calls.
     * @param port the port to listen on.
     * @param telemetries collection to pre-populate the data required
     *                    for server operations.
     */
    private TelemetryDashboardServer(ServerBuilder<?> serverBuilder, int port, Collection<Telemetry> telemetries) {
        this.port = port;
        this.server = serverBuilder.addService(new TelemetryDashboardService(telemetries)).build();
    }

    /**
     * Server's lifecycle method that helps to start the gRPC server
     * @throws IOException if unable to bind to port
     */
    public void start() throws IOException {
        logger.info("Starting gRPC server ...");
        server.start();
        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(getShutDownHookThread());
    }

    /**
     * Server's lifecycle method that gracefully shuts down the gRPC server
     * @throws InterruptedException
     */
    public void stop() throws InterruptedException {
        if(server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private Thread getShutDownHookThread() {
        return new Thread() {
            @Override
            public void run() {
                System.err.println("*** Shutting down the gRPC server since JVM is shutting down ...");
                try {
                    TelemetryDashboardServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** Server shut down");
            }
        };
    }

    public static void main(String[] args) throws Exception {
        final TelemetryDashboardServer server = new TelemetryDashboardServer(8980);
        server.start();
        server.blockUntilShutdown();
    }

}
