package in.gvatreya.telemetry.client;

import com.google.protobuf.Timestamp;
import in.gvatreya.telemetry.dashboard.Telemetry;
import in.gvatreya.telemetry.dashboard.TelemetryDashboardGrpc;
import in.gvatreya.telemetry.dashboard.TimePeriod;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

@Service
public class TelemetryDashboardClient {

    private static final Logger logger = Logger.getLogger(TelemetryDashboardClient.class.getName());

    // FIXME: From env or properties file
    private static final String SERVER_URI = "localhost:8980";
    private final static String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd' 'HH:mm:ss";

    private final TelemetryDashboardGrpc.TelemetryDashboardBlockingStub blockingStub;
    private final TelemetryDashboardGrpc.TelemetryDashboardStub asyncStub;

    /**
     * Construct TelemetryDashboardClient for accessing server via channel
     */
    public TelemetryDashboardClient() {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(SERVER_URI)
                .usePlaintext() // Otherwise expects SSL/TLS Security config
                .build();
        this.blockingStub = TelemetryDashboardGrpc.newBlockingStub(channel);
        this.asyncStub = TelemetryDashboardGrpc.newStub(channel);
    }

    public Telemetry findOne(@Nonnull final Timestamp timestamp) {
        logger.info("Fetching telemetry at " + timestamp );

        final TimePeriod timePeriod = TimePeriod.newBuilder().setStart(timestamp).buildPartial();

        final Telemetry telemetryAtSpecificTime = blockingStub.getTelemetryAtSpecificTime(timePeriod);

        logger.fine("Returning " + telemetryAtSpecificTime);

        return telemetryAtSpecificTime;

    }

    //FIXME: Can take another parameter for string format
    public Telemetry findOne(final String timestampAsString) throws ParseException {
        return findOne(parseTimestamp(timestampAsString, DEFAULT_TIMESTAMP_FORMAT));
    }

    private Timestamp parseTimestamp(final String timestampAsString, final String format) throws ParseException {
        final Date date = new SimpleDateFormat(format).parse(timestampAsString);
        return Timestamp.newBuilder().setSeconds(date.getTime() / 1000)
                .setNanos((int) ((date.getTime() % 1000) * 1000000)).build();
    }
}
