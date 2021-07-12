package in.gvatreya.telemetry.service;

import com.google.protobuf.Timestamp;
import in.gvatreya.telemetry.dashboard.Telemetry;
import in.gvatreya.telemetry.dashboard.TelemetryDashboardGrpc;
import in.gvatreya.telemetry.dashboard.TimePeriod;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

public class TelemetryDashboardService extends TelemetryDashboardGrpc.TelemetryDashboardImplBase {

    private static final Logger logger = Logger.getLogger(TelemetryDashboardService.class.getName());

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
        logger.info("Received request for telemetry at time: " + request.getStart());

        // Null check not required since TimePeriod returns a Default instance
        // Also, the hasStart() is always true? since a default instance is available
        // always - Adding the if...else to exhibit Error Status
        final Telemetry one = findOne(request.getStart());
        if(request.hasStart()) {
            responseObserver.onNext(one);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                    .withDescription("Request does not contain the time period")
                    .asRuntimeException()
            );
        }
    }

    @Nullable
    private Telemetry findOne(@Nonnull final Timestamp timestamp) {
        final Optional<Telemetry> first = telemetries.stream()
                .filter(telemetry -> telemetry.getTimestamp().equals(timestamp))
                .findFirst();
        return first.orElse(null);
    }

    @Override
    public void getTelemetriesInRange(TimePeriod request, StreamObserver<Telemetry> responseObserver) {

        logger.info("Received request for telemetry in timerange: " + request);

        // To check Deadlines
        final Context context = Context.current();

        /*
         * This method can actually use Java stream filters to filter objects in the time range,
         * returning a List / repeated message (proto).
         * Instead, to experiment with message stream, I will use a for loop
         */

        for (Telemetry telemetry : telemetries) {

            // Check if Deadline has passed?
            if(!context.isCancelled()) {
                if (telemetry.getTimestamp().getSeconds() >= request.getStart().getSeconds() &&
                        telemetry.getTimestamp().getSeconds() < request.getEnd().getSeconds()) {
                    responseObserver.onNext(telemetry);
                }
            } else {
                return;
            }
        }

        responseObserver.onCompleted();

    }
}
