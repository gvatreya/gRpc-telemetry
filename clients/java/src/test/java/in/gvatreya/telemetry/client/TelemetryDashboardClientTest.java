package in.gvatreya.telemetry.client;

import com.google.protobuf.Timestamp;
import in.gvatreya.telemetry.dashboard.Telemetry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TelemetryDashboardClientTest {

    @Test
    public void clientRunner() throws Exception {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8980")
                .usePlaintext() // Otherwise expects SSL/TLS Security config
                .build();

        try {

            final TelemetryDashboardClient client = new TelemetryDashboardClient();

            Telemetry telemetry = client.findOne(parseTimestamp("2019-01-01 00:15:00", "yyyy-MM-dd' 'HH:mm:ss"));
            System.out.println("Telemetry Returned: " + telemetry);

            telemetry = client.findOne(Timestamp.newBuilder().getDefaultInstanceForType());
            System.out.println("Telemetry Returned: " + telemetry);

            telemetry = client.findOne(Timestamp.newBuilder().build());
            System.out.println("Telemetry Returned: " + telemetry);

        } finally {
            System.err.println("Gracefully shutting down channel ...");
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    private Timestamp parseTimestamp(final String timestampAsString, final String format) throws ParseException {
        final Date date = new SimpleDateFormat(format).parse(timestampAsString);
        return Timestamp.newBuilder().setSeconds(date.getTime() / 1000)
                .setNanos((int) ((date.getTime() % 1000) * 1000000)).build();
    }

}