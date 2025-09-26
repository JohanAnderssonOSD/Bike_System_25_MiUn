package com.bikeshare.lab3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.bikeshare.model.Bike;
import com.bikeshare.model.Station;
import com.bikeshare.model.Station.StationStatus;
import com.bikeshare.model.Bike.BikeType;

public class BikeStationTest {
    Bike bike = new Bike("bikey", Bike.BikeType.STANDARD);
    Station station = new Station("st-1-b", "Station 1-B", "Biblioteksgatan 6B", 63.64, 34.64, 15);

    @Test
    void adding_empty_bike_assert_throws() {
        assertThrows(IllegalArgumentException.class, () -> station.addBike(null));
    }

    @Test
    void adding_bike_to_empty_station_assert_throws() {
        station.deactivate();

        assertThrows(IllegalStateException.class, () -> station.addBike(bike));
    }

    @Test
    void adding_existing_bike_assert_throws() {
        station.addBike(bike);
        assertThrows(IllegalStateException.class, () -> station.addBike(bike));
    }

    @Test
    void adding_bike_to_full_station_assert_throws() {
        for (int index = 0; index < 15; index++) {
            station.addBike(new Bike(index + "", Bike.BikeType.STANDARD));
        }

        assertThrows(IllegalStateException.class, () -> station.addBike(bike));
    }

    @Test
    void unavailable_bike_added_to_station_assert_throws() {
        Bike brokenBike = new Bike("broken", Bike.BikeType.PREMIUM);
        Bike inUseBike = new Bike("in_use", Bike.BikeType.STANDARD);
        Bike maintainanceBike = new Bike("maintainance", Bike.BikeType.ELECTRIC);
        Bike reservedBike = new Bike("reserved", Bike.BikeType.STANDARD);

        brokenBike.markAsBroken();
        inUseBike.startRide();
        maintainanceBike.sendToMaintenance();
        reservedBike.reserve();

        assertThrows(IllegalStateException.class, () -> station.addBike(brokenBike));
        assertThrows(IllegalStateException.class, () -> station.addBike(inUseBike));
        assertThrows(IllegalStateException.class, () -> station.addBike(maintainanceBike));
        assertThrows(IllegalStateException.class, () -> station.addBike(reservedBike));

    }

    @Test
    void cannot_remove_empty_bike_assert_throws() {
        assertThrows(IllegalArgumentException.class, () -> station.removeBike(null));
    }

    @Test
    void cannot_remove_nonexistent_bike_assert_throws() {
        assertThrows(IllegalStateException.class,
                () -> station.removeBike("my_unique_bike_which_cannot_possibly_not_exist"));
    }

    @Test
    void cannot_remove_reserved_bike_assert_throws() {
        bike.reserve();
        assertThrows(IllegalStateException.class, () -> station.removeBike(bike.getBikeId()));
    }

    @Test
    void reserving_empty_bike_assert_throws() {
        assertThrows(IllegalArgumentException.class, () -> station.reserveBike(null));
    }

    @Test
    void reserving_nonexistent_bike_assert_throws() {
        assertThrows(IllegalStateException.class,
                () -> station.reserveBike("my_unique_bike_which_cannot_possibly_not_exist"));
    }

    @Test
    void reserving_already_reserved_bike_assert_throws() {
        bike.reserve();
        assertThrows(IllegalStateException.class, () -> station.reserveBike(bike.getBikeId()));
    }

    @Test
    void get_bike_at_inactive_station_returns_nothing_assert_equals() {
        station.deactivate();

        Bike expected = null;
        Bike actual = station.getAvailableBike(Bike.BikeType.ELECTRIC);

        assertEquals(expected, actual);
    }

    @Test
    void unavailable_preferred_bike_returns_best_available_if_existent_assert_equals() {
        Bike unavailablePreferredBike = new Bike("chronically_unavailable_bike", BikeType.ELECTRIC);
        Bike expected = bike;
        station.addBike(unavailablePreferredBike);
        station.addBike(bike);
        unavailablePreferredBike.startRide();
        Bike actual = station.getAvailableBike(BikeType.ELECTRIC);

        assertEquals(expected, actual);
    }

    @Test
    void unavailable_preferred_bike_returns_nothing_if_no_other_available_assert_equals() {
        Bike unavailableBike = new Bike("unavailable_bike", BikeType.ELECTRIC);
        station.addBike(unavailableBike);
        unavailableBike.startRide();
        Bike actual = station.getAvailableBike(Bike.BikeType.ELECTRIC);
        assertEquals(null, actual);
    }

    @Test
    void get_preferred_bike_type_if_available_assert_equals() {
        // Kolla den som kommer tillbaka är av type.
        Bike preferredBike = new Bike("preferred_bike", BikeType.ELECTRIC);
        station.addBike(preferredBike);
        Bike actual = station.getAvailableBike(Bike.BikeType.ELECTRIC);
        assertEquals(preferredBike, actual);
    }

    /*
     * Create test for this!
     * public List<Bike> getAvailableBikesByType(Bike.BikeType bikeType) {
     * return availableBikes.values().stream()
     * .filter(bike -> bike.getType() == bikeType)
     * .filter(bike -> bike.isAvailable())
     * .filter(bike -> !reservedBikeIds.contains(bike.getBikeId()))
     * .toList();
     * }
     */

    @Test
    void get_available_bikes_by_type() {
        station.addBike(new Bike("1", Bike.BikeType.STANDARD));
        station.addBike(new Bike("2", Bike.BikeType.STANDARD));
        station.addBike(new Bike("3", Bike.BikeType.PREMIUM));
        station.addBike(new Bike("4", Bike.BikeType.ELECTRIC));
        station.addBike(new Bike("5", Bike.BikeType.ELECTRIC));

        var standardBikes = station.getAvailableBikesByType(Bike.BikeType.STANDARD);
        var premiumBikes = station.getAvailableBikesByType(Bike.BikeType.PREMIUM);
        var electricBikes = station.getAvailableBikesByType(Bike.BikeType.ELECTRIC);

        assert (standardBikes.size() == 2);
        assert (premiumBikes.size() == 1);
        assert (electricBikes.size() == 2);

    }

    @Test
    void unavailable_charging_cannot_charge_assert_throws() {
        Bike bike1 = new Bike("b1", Bike.BikeType.ELECTRIC);
        bike1.startRide();
        bike1.endRide(500); // Drains battery to 0%
        station.addBike(bike1);
        station.disableCharging();
        assertThrows(IllegalStateException.class, () -> station.chargeElectricBikes(100));
    }

    @Test
    void charging_station_cannot_charge_non_electric_or_unavailable_bikes_assert_equals() {
        Bike bike1 = new Bike("b1", Bike.BikeType.ELECTRIC);
        Bike bike2 = new Bike("b2", Bike.BikeType.STANDARD);

        station.addBike(bike1);
        station.addBike(bike2);
        station.enableCharging(1);
        bike1.startRide();
        bike1.endRide(50);

        station.chargeElectricBikes(50);
        assertEquals(50, bike1.getBatteryLevel());
        assertEquals(bike2.getBatteryLevel(), -1);
    }

    @Test
    void get_right_amount_of_available_bike_count_assert_equals() {
        int expected = 3;
        station.addBike(new Bike("1", Bike.BikeType.STANDARD));
        station.addBike(new Bike("2", Bike.BikeType.STANDARD));
        station.addBike(new Bike("3", Bike.BikeType.PREMIUM));

        int actual = station.getAvailableBikeCount();

        assertEquals(expected, actual);

    }
}