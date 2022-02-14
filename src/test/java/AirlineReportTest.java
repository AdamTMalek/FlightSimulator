import com.github.adamtmalek.flightsimulator.models.*;
import com.github.adamtmalek.flightsimulator.models.io.AirlineReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class AirlineReportTest {

    @Test
    void givenPopulatedListThenMembersAreSummed() {
        var flights = new ArrayList<Flight>();
        flights.add(new Flight("",
                new Aeroplane("", "", "", ""),
                new Airport("", "", "", ""),
                new Airport("", "", "", ""),
                ZonedDateTime.now(),
                new ArrayList<ControlTower>(),
                new Kilometre(1),
                Duration.ofHours(2),
                new GramsPerKilometre(3)
        ));

        flights.add(new Flight("",
                new Aeroplane("", "", "", ""),
                new Airport("", "", "", ""),
                new Airport("", "", "", ""),
                ZonedDateTime.now(),
                new ArrayList<ControlTower>(),
                new Kilometre(1),
                Duration.ofHours(2),
                new GramsPerKilometre(3)
        ));

        flights.add(new Flight("",
                new Aeroplane("", "", "", ""),
                new Airport("", "", "", ""),
                new Airport("", "", "", ""),
                ZonedDateTime.now(),
                new ArrayList<ControlTower>(),
                new Kilometre(1),
                Duration.ofHours(2),
                new GramsPerKilometre(3)
        ));

        final var airlineReport = new AirlineReport(flights);
        Assertions.assertEquals(3, airlineReport.totalFlights());
        Assertions.assertEquals(9, airlineReport.estimatedCO2Emissions().gramsPerKilometre());
        Assertions.assertEquals(3, airlineReport.totalDistanceTravelled().kilometre());
    }

    @Test
    void givenEmptyAirlinesListThenMembersAreSetTo0() {
        var flights = new ArrayList<Flight>();
        final var airlineReport = new AirlineReport(flights);
        Assertions.assertEquals(0, airlineReport.totalFlights());
        Assertions.assertEquals(0, airlineReport.estimatedCO2Emissions().gramsPerKilometre());
        Assertions.assertEquals(0, airlineReport.totalDistanceTravelled().kilometre());
    }
}
