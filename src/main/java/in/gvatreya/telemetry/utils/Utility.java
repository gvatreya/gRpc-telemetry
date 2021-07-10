package in.gvatreya.telemetry.utils;

import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import in.gvatreya.telemetry.dashboard.Telemetry;
import in.gvatreya.telemetry.dashboard.TelemetryDatabase;
import in.gvatreya.telemetry.dashboard.TelemetryDatabaseOrBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utility {

    // FIXME: Environment/Properties file
    final static String CSV_FILENAME = "meterusage.1625583807.csv";
    final static String[] CSV_HEADERS = {"time", "meterusage"};
    final static String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd' 'HH:mm:ss";

    /**
     * Gets the default features file from classpath.
     */
    public static URL getDefaultFeaturesFile() {
        return Utility.class.getClassLoader().getResource(CSV_FILENAME);
    }

    /**
     * Parses the CSV input file containing the list of telemetries.
     */
    public static List<Telemetry> parseTelemetries(URL file) throws IOException, NumberFormatException, ParseException {
        final Reader reader = new FileReader(file.getFile());
        Iterable<CSVRecord> csvRecords = CSVFormat.DEFAULT
                .withHeader(CSV_HEADERS)
                .withFirstRecordAsHeader()
                .parse(reader);
        final TelemetryDatabase.Builder telemetryDatabaseBuilder = TelemetryDatabase.newBuilder();
        for (CSVRecord csvRecord : csvRecords) {
            final String timeString = csvRecord.get("time");
            final String meterusageString = csvRecord.get("meterusage");
            final Telemetry telemetry = Telemetry.newBuilder()
                    .setTimestamp(parseTimestamp(timeString, DEFAULT_TIMESTAMP_FORMAT))
                    .setValue(Float.parseFloat(meterusageString))
                    .build();
            telemetryDatabaseBuilder.addTelemetry(telemetry);
        }
        return telemetryDatabaseBuilder.build().getTelemetryList();
    }

    public static Timestamp parseTimestamp(final String timestampAsString, final String format) throws ParseException {
        final Date date = new SimpleDateFormat(format).parse(timestampAsString);
        return Timestamp.newBuilder().setSeconds(date.getTime() / 1000)
                .setNanos((int) ((date.getTime() % 1000) * 1000000)).build();
    }

}
