package in.gvatreya.telemetry.controller;

import in.gvatreya.telemetry.client.TelemetryDashboardClient;
import in.gvatreya.telemetry.dashboard.Telemetry;
import in.gvatreya.telemetry.dto.TelemetryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private static final Logger LOG = Logger.getLogger(TelemetryController.class.getName());

    TelemetryDashboardClient client;

    @Autowired
    public TelemetryController(TelemetryDashboardClient client) {
        this.client = client;
    }

    @GetMapping("/{timestampAsString}")
    @ResponseBody
    public ResponseEntity<TelemetryDto> getTelemetry(@PathVariable("timestampAsString")final String timestampAsString) {

        try {
            final Telemetry one = client.findOne(timestampAsString);
            if(one.hasTimestamp()) {
                return new ResponseEntity<>(TelemetryDto.fromProto(one), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    @ResponseBody
    public ResponseEntity<List<TelemetryDto>> getTelemetry(@RequestParam("start")final String startTimeString, @RequestParam("end")final String endTimeString) {

        try {
            final List<Telemetry> telemetries = client.findTelemetryInRange(startTimeString, endTimeString);
            return new ResponseEntity<>(TelemetryDto.fromProto(telemetries), HttpStatus.OK);
        } catch (ParseException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
