package in.gvatreya.telemetry.dashboard;

import com.google.protobuf.Timestamp;
import in.gvatreya.telemetry.utils.Utility;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TelemetryDashboardClient {

    private static final Logger logger = Logger.getLogger(TelemetryDashboardClient.class.getName());

    // FIXME: From env or properties file
    private static final String SERVER_URI = "localhost:8980";

    private final TelemetryDashboardGrpc.TelemetryDashboardBlockingStub blockingStub;
    private final TelemetryDashboardGrpc.TelemetryDashboardStub asyncStub;

    /**
     * Construct TelemetryDashboardClient for accessing server via channel
     * @param channel connection for performing RPCs with server
     */
    public TelemetryDashboardClient(final Channel channel) {
        this.blockingStub = TelemetryDashboardGrpc.newBlockingStub(channel);
        this.asyncStub = TelemetryDashboardGrpc.newStub(channel);
    }

    public Telemetry findOne(@Nonnull final Timestamp timestamp) {
        logger.info("Fetching telemetry at " + new Date(timestamp.getSeconds() + timestamp.getNanos()));

        final TimePeriod timePeriod = TimePeriod.newBuilder().setStart(timestamp).buildPartial();

        final Telemetry telemetryAtSpecificTime = blockingStub.getTelemetryAtSpecificTime(timePeriod);

        logger.fine("Returning " + telemetryAtSpecificTime);

        return telemetryAtSpecificTime;

    }

    public static void main(String[] args) throws ParseException, InterruptedException {

        final ManagedChannel channel = ManagedChannelBuilder.forTarget(SERVER_URI)
                .usePlaintext() // Otherwise expects SSL/TLS Security config
                .build();

        try {

            final TelemetryDashboardClient client = new TelemetryDashboardClient(channel);

            Telemetry telemetry = client.findOne(Utility.parseTimestamp("2019-01-01 00:15:00", Utility.DEFAULT_TIMESTAMP_FORMAT));
            logger.info("Telemetry Returned: " + telemetry);

            telemetry = client.findOne(Timestamp.newBuilder().getDefaultInstanceForType());
            logger.info("Telemetry Returned: " + telemetry);

        } finally {
            System.err.println("Gracefully shutting down channel ...");
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
