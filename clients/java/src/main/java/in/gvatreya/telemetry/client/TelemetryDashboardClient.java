package in.gvatreya.telemetry.client;

import com.google.protobuf.Timestamp;
import in.gvatreya.telemetry.dashboard.Telemetry;
import in.gvatreya.telemetry.dashboard.TelemetryDashboardGrpc;
import in.gvatreya.telemetry.dashboard.TimePeriod;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

    public List<Telemetry> findTelemetryInRange(@NonNull final String startTimeString, @NonNull final String endTimeString) throws ParseException {
        final Timestamp start = parseTimestamp(startTimeString, DEFAULT_TIMESTAMP_FORMAT);
        final Timestamp end = parseTimestamp(endTimeString, DEFAULT_TIMESTAMP_FORMAT);
        final TimePeriod timePeriod = TimePeriod.newBuilder().setStart(start).setEnd(end).build();
        return findTelemetryInRange(timePeriod);
    }

    public List<Telemetry> findTelemetryInRange(@NonNull final TimePeriod timePeriod) {
        logger.info("Fetching telemetries in range " + timePeriod);
        final List<Telemetry> telemetries = new ArrayList<>();
        final Iterator<Telemetry> telemetriesInRange = blockingStub
                .withDeadline(Deadline.after(1000, TimeUnit.MILLISECONDS))
                .getTelemetriesInRange(timePeriod);
        while (telemetriesInRange.hasNext()) {
            telemetries.add(telemetriesInRange.next());
        }
        return telemetries;
    }
}
