package in.gvatreya.telemetry.dto;

import com.google.protobuf.Timestamp;
import in.gvatreya.telemetry.dashboard.Telemetry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//FIXME: Is there a better way using ProtoBuf's inbuilt methods? R&D required
// One way to use https://github.com/yidongnan/grpc-spring-boot-starter
// Sticking to vanilla implementations
// Returning Telemetry results in Cyclic Jackson error
public class TelemetryDto {

    private Date timestamp;
    private float value;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TelemetryDto{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }

    public static TelemetryDto fromProto(final Telemetry telemetry) {
        final Timestamp ts = telemetry.getTimestamp();
        final Date date = new Date(ts.getSeconds() * 1000 + (ts.getNanos() / 1000));
        final TelemetryDto telemetryDto = new TelemetryDto();
        telemetryDto.setTimestamp(date);
        telemetryDto.setValue(telemetry.getValue());
        return telemetryDto;
    }

    public static List<TelemetryDto> fromProto(final List<Telemetry> telemetries) {
        final List<TelemetryDto> dtos = new ArrayList<>(telemetries.size());
        for (Telemetry telemetry : telemetries) {
            dtos.add(fromProto(telemetry));
        }
        return dtos;
    }

}
