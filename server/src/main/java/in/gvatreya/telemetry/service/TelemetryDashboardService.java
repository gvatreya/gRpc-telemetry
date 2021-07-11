package in.gvatreya.telemetry.service;

import com.google.protobuf.Timestamp;
import in.gvatreya.telemetry.dashboard.Telemetry;
import in.gvatreya.telemetry.dashboard.TelemetryDashboardGrpc;
import in.gvatreya.telemetry.dashboard.TimePeriod;
import io.grpc.stub.StreamObserver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public class TelemetryDashboardService extends TelemetryDashboardGrpc.TelemetryDashboardImplBase {

    private final Collection<Telemetry> telemetries;

    // Initialize the telemetry data
    public TelemetryDashboardService(final Collection<Telemetry> telemetries) {
        this.telemetries = telemetries;
    }

    /**
     * Return the {@link Telemetry} at the request {@link TimePeriod}
     * @param request the TimePeriod for which we need the telemetry.
     *                The start attribute is used and the end attribute
     *                is ignored.
     * @param responseObserver the observer that will receive the
     *                         telemetry at the required time.
     */
    @Override
    public void getTelemetryAtSpecificTime(TimePeriod request, StreamObserver<Telemetry> responseObserver) {
        // Null check not required since TimePeriod returns a Default instance
        responseObserver.onNext(findOne(request.getStart()));
        responseObserver.onCompleted();
    }

    @Nullable
    private Telemetry findOne(@Nonnull final Timestamp timestamp) {
        final Optional<Telemetry> first = telemetries.stream()
                .filter(telemetry -> telemetry.getTimestamp().equals(timestamp))
                .findFirst();
        return first.orElse(null);
    }
}
