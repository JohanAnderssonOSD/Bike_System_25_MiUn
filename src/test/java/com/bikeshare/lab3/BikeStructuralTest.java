package com.bikeshare.lab3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.bikeshare.model.Bike;
import com.bikeshare.model.Bike.BikeStatus;

public class BikeStructuralTest {
    Bike bike = new Bike("Standard", Bike.BikeType.STANDARD);

    @Test
    void reserved_bike_cannot_be_reserved_again_assert_throws() {
        bike.reserve();
        var status = bike.getStatus();

        // assertTrue(status.equals(BikeStatus.RESERVED));
        assertThrows(IllegalStateException.class, () -> bike.reserve());
    }

    @Test
    void bike_in_use_cannot_be_reserved_assert_throws() {

        bike.startRide();
        var status = bike.getStatus();

        // assertTrue(status.equals(BikeStatus.IN_USE));
        assertThrows(IllegalStateException.class, () -> bike.reserve());
    }

    @Test
    void broken_bike_cannot_be_startRided_assert_true() {
        bike.markAsBroken();
        var status = bike.getStatus();

        assertTrue(status.equals(BikeStatus.BROKEN));
        assertThrows(IllegalStateException.class, () -> bike.startRide());
    }

    @Test
    void bike_in_use_cannot_be_startRided_assert_true() {
        bike.startRide();
        var status = bike.getStatus();

        assertTrue(status.equals(BikeStatus.IN_USE));
        assertThrows(IllegalStateException.class, () -> bike.startRide());
    }

    @Test
    void bike_in_maintenance_cannot_be_startRided_assert_true() {
        bike.sendToMaintenance();
        var status = bike.getStatus();

        assertTrue(status.equals(BikeStatus.MAINTENANCE));
        assertThrows(IllegalStateException.class, () -> bike.startRide());
    }

    @Test
    void electric_bike_under_10_percent_charge_cannot_start_ride_assert_throws() {
        Bike electricBike = new Bike("electro", Bike.BikeType.ELECTRIC);

        // Simulate a 46km long ride, draining a total of 92%
        electricBike.startRide();
        electricBike.endRide(46);

        assertThrows(IllegalStateException.class, () -> electricBike.startRide());
    }

    @Test
    void bike_not_in_use_cannot_end_ride_assert_throws() {
        bike.startRide();
        bike.markAsBroken();

        assertThrows(IllegalStateException.class, () -> bike.endRide(50));

    }

    @Test
    void bike_cannot_end_ride_if_distance_less_than_0_assert_throws() {
        bike.startRide();

        assertThrows(IllegalArgumentException.class, () -> bike.endRide(-1));
    }

    @Test
    void bike_cannot_be_sent_to_maintaince_if_in_use_assert_throws() {
        bike.startRide();

        assertThrows(IllegalStateException.class, () -> bike.sendToMaintenance());
    }

    @Test
    void bike_cannot_complete_maintainence_if_status_not_maintainance_assert_throws() {
        bike.startRide();

        assertThrows(IllegalStateException.class, () -> bike.completeMaintenance());
    }

    @Test
    void bike_marked_as_broken_sets_status_to_broken_and_needs_maintainance_true_assert_equals_true() {
        var expectedStatus = Bike.BikeStatus.BROKEN;
        bike.markAsBroken();

        boolean actualMaintainance = bike.needsMaintenance();
        var actualStatus = bike.getStatus();

        assertTrue(actualMaintainance);
        assertEquals(expectedStatus, actualStatus);
    }

    @Test
    void bike_cannot_charge_battery_when_bike_type_not_electric_assert_throws() {
        
        assertFalse(bike.getType().equals(Bike.BikeType.ELECTRIC));
        assertThrows(IllegalStateException.class, () -> bike.chargeBattery(10));
    }

    @Test
    void bike_cannnot_charge_if_outside_threshold_assert_throws() {
        Bike electrBike = new Bike("electro", Bike.BikeType.ELECTRIC);
        assertThrows(IllegalArgumentException.class, () -> electrBike.chargeBattery(-1));
        assertThrows(IllegalArgumentException.class, () -> electrBike.chargeBattery(101));
    }

    @Test
    void bike_battery_level_cannot_exceed_100_assert_true() {
        Bike electricBike = new Bike("electro", Bike.BikeType.ELECTRIC);
        electricBike.chargeBattery(0.1);

        assertTrue(electricBike.getBatteryLevel() == 100);
    }
}