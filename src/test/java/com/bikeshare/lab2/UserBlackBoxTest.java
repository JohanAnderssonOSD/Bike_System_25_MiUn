package com.bikeshare.lab2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.bikeshare.model.User;

/**
 * Lab 2 Template: Black Box Testing for User class
 * 
 * 
 * 
 * TODO for students:
 * - Challenge 2.1: Add Equivalence Partitioning tests for email validation,
 * name, telephone number (With GenAI help), and fund addition
 */
/* - Challenge 2.2: Add Boundary Value Analysis tests for fund addition */
/*
 * - Challenge 2.3: Add Decision Table tests for phone number validation
 * - Optional Challenge 2.4: Add error scenario tests
 */

// This test is just an example to get you started. You will need to add more
// tests as per the challenges.
@DisplayName("Verify name handling in User class")
class UserBlackBoxTest {

    @Test
    @DisplayName("Should store and retrieve user names correctly")
    void shouldStoreAndRetrieveUserNamesCorrectly() {
        // Arrange - Set up test data
        String expectedFirstName = "John";
        String expectedLastName = "Doe";
        String validEmail = "john.doe@example.com";
        String validPersonnummer = "901101-1237"; // Valid Swedish personnummer

        // Act - Execute the method under test
        User user = new User(validPersonnummer, validEmail, expectedFirstName, expectedLastName);
        String actualFirstName = user.getFirstName();
        String actualLastName = user.getLastName();
        String actualFullName = user.getFullName();

        // Assert - Verify the expected outcome
        assertNotNull(user, "User should be created successfully");
        assertEquals(expectedFirstName, actualFirstName, "First name should match");
        assertEquals(expectedLastName, actualLastName, "Last name should match");
        assertEquals("John Doe", actualFullName, "Full name should be formatted correctly");
    }

    // // TODO: Challenge 2.1 - Add Equivalence Partitioning tests for email
    // validation
    // // Hint: Test valid emails (user@domain.com) and invalid emails (missing @,
    // empty, etc.)

    User user = new User("901101-1237", "user@domain.com", "John", "Doe");

    @ParameterizedTest
    @ValueSource(strings = { "user @domain.com", "userdomain.com", "@domain", "user@domain", " ", "", "@", ".com",
            "domain", "user@@domain.com" })
    void invalidEmailsGetCaughtInIllegalArgumentExceptionAssertThrows(String email) {
        assertThrows(IllegalArgumentException.class, () -> user.setEmail(email));
    }
    
    // @ParameterizedTest
    // @ValueSource(strings = { "", " ", "A", "ThisNameIsWayTooLongToBeAValidFirstNameBecauseItExceedsFiftyCharacters",
    //         "Berit44", })
    // void invalidNamesGetCaughtInIllegalArgumentExceptionAssertThrows(String name) {
    //     assertThrows(IllegalArgumentException.class, () -> user.setFirstName(name));
    //     assertThrows(IllegalArgumentException.class, () -> user.setLastName(name));
    // }

    // TODO: Challenge 2.3 - Add Decision Table tests for phone number validation
    // Hint: Test Swedish phone formats (+46701234567, 0701234567) and invalid
    // formats
    @ParameterizedTest
    @ValueSource(strings = { "070123456", "07012345678", "46701234567", "+4670123456", "+467012345678",
            "1234567890", "070-12345670", "07A1234567", "0701234B67", "070123456C", "070123 567" })
    void invalidPhoneNumbersThrowIllegalArgumentException(String phoneNumber) {
        assertThrows(IllegalArgumentException.class, () -> user.setPhoneNumber(phoneNumber),
                String.format("%s does not get caught.", phoneNumber));
        
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "" })
    void phoneNumberDoesNotGetVerifiedIfNullOrEmptyAssertEquals(String phoneNumber) {
        user.setPhoneNumber(phoneNumber);

        boolean expected = false;
        boolean actual = user.isPhoneVerified();

        assertEquals(expected, actual);
    }

    // TODO: Challenge 2.2 - Add Boundary Value Analysis tests for fund addition
    // Hint: Test minimum (0.01), maximum (1000.00), and invalid amounts (0,
    // negative, > 1000)
    @ParameterizedTest
    @ValueSource(doubles = { 0.0, -10.0, 1000.01, 2000.00 })
    void invalidFundAdditionsGetCaughtInIllegalArgumentExceptionAssertThrows(double funds) {
        assertThrows(IllegalArgumentException.class, () -> user.addFunds(funds),
                String.format("Adding %.2f does not get caught.", funds));
    }

    // This could result in problems if more cases were added. Then we might tyr
    // adding i valid amount(ex. 5000)
    // but the total would exceed 20000

    // TODO: Challenge 2.4 - Add error scenario tests
    // Hint: Test insufficient balance, invalid inputs, state violations
    // @Test
    // void addingFundsWhichExceedBalanceLimitThrowsAndDoesNotAffectBalance() {
    //     double expected = 2000;
    //         user.addFunds(expected/2);// add 20,000SEK to account balance.
    //         user.addFunds(expected/2);
    //     assertThrows(IllegalArgumentException.class, () -> {
    //         user.addFunds(0.10); // exceed 20,000SEK balance limit.
    //     });

    //     double actual = user.getAccountBalance();

    //     assertEquals(expected, actual);
    // }
}
