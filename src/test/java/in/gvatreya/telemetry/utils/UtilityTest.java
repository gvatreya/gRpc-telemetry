package in.gvatreya.telemetry.utils;

import com.google.protobuf.Timestamp;
import in.gvatreya.telemetry.dashboard.Telemetry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void parseTelemetries() throws Exception {
        final URL file = Objects.requireNonNull(UtilityTest.class.getClassLoader().getResource("sample_meterusage.csv"));
        List<Telemetry> telemetries = Utility.parseTelemetries(file);
        assertFalse(telemetries.isEmpty());
        assertEquals(5, telemetries.size());
        Telemetry telemetry = telemetries.get(0);
        assertEquals(55.09f, telemetry.getValue());
        assertEquals("seconds: 1546281900\n", telemetry.getTimestamp().toString());
        telemetry = telemetries.get(1);
        assertEquals(54.64f, telemetry.getValue());
        assertEquals("seconds: 1546282800\n", telemetry.getTimestamp().toString());
        telemetry = telemetries.get(2);
        assertEquals(55.18f, telemetry.getValue());
        assertEquals("seconds: 1546283700\n", telemetry.getTimestamp().toString());
        telemetry = telemetries.get(3);
        assertEquals(56.03f, telemetry.getValue());
        assertEquals("seconds: 1546284600\n", telemetry.getTimestamp().toString());
        telemetry = telemetries.get(4);
        assertEquals(55.45f, telemetry.getValue());
        assertEquals("seconds: 1546285500\n", telemetry.getTimestamp().toString());

        telemetries = Utility.parseTelemetries(Utility.getDefaultFeaturesFile());
        assertEquals(2975, telemetries.size());
    }

    @Test
    void parseTimestamp() throws Exception{
        final Timestamp timestamp = Utility.parseTimestamp("2019-01-31 23:45:00", Utility.DEFAULT_TIMESTAMP_FORMAT);
        assertEquals("seconds: 1548958500\n", timestamp.toString());
    }
}