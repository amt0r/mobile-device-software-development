package com.example.emergencypower

import com.example.emergencypower.logic.EmergencyFormValidator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EmergencyFormValidatorTest {

    private lateinit var validator: EmergencyFormValidator

    @Before
    fun setUp() {
        validator = EmergencyFormValidator()
    }

    @Test
    fun validateForm_validInputs_returnsSuccess() {
        val result = validator.validateForm(
            email = "engineer@epss.kpi.ua",
            timeoutSecStr = "10",
            batteryCutoffStr = "25",
            pin = "admin123Pass",
            maxLoadStr = "5000"
        )
        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun validateForm_invalidEmail_returnsError() {
        val result = validator.validateForm(
            email = "invalid-email-format",
            timeoutSecStr = "10",
            batteryCutoffStr = "25",
            pin = "admin123Pass",
            maxLoadStr = "5000"
        )
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("email"))
    }

    @Test
    fun validateForm_timeoutOutOfBounds_returnsError() {
        val result = validator.validateForm(
            email = "test@epss.ua",
            timeoutSecStr = "1",
            batteryCutoffStr = "20",
            pin = "valid123",
            maxLoadStr = "3000"
        )
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("timeout"))
    }

    @Test
    fun validateForm_weakPin_returnsError() {
        val result = validator.validateForm(
            email = "test@epss.ua",
            timeoutSecStr = "15",
            batteryCutoffStr = "20",
            pin = "12345",
            maxLoadStr = "3000"
        )
        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("pin"))
    }
}
