package com.example.smartcity;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.smartcity.tools.ConvertTime;

public class IsTimeOverlappingTest {
    @Test
    public void testIsTimeOverlapping_FullOverlap() {
        // Slot from 9:00 to 10:00 and event from 9:00 to 10:00 should overlap
        boolean result = ConvertTime.isTimeOverlapping(9.0f / 24, 10.0f / 24, 9.0f / 24, 10.0f / 24);
        assertTrue(result);
    }

    @Test
    public void testIsTimeOverlapping_PartialOverlap() {
        // Slot from 9:00 to 10:00 and event from 9:30 to 10:30 should overlap
        boolean result = ConvertTime.isTimeOverlapping(9.5f / 24, 10.5f / 24, 9.0f / 24, 10.0f / 24);
        assertTrue(result);
    }

    @Test
    public void testIsTimeOverlapping_NoOverlap() {
        // Slot from 9:00 to 10:00 and event from 10:30 to 11:30 should not overlap
        boolean result = ConvertTime.isTimeOverlapping(10.5f / 24, 11.5f / 24, 9.0f / 24, 10.0f / 24);
        assertFalse(result);
    }

    @Test
    public void testIsTimeOverlapping_EventEnclosesSlot() {
        // Event from 8:00 to 11:00 encloses the slot from 9:00 to 10:00, so it should overlap
        boolean result = ConvertTime.isTimeOverlapping(8.0f / 24, 11.0f / 24, 9.0f / 24, 10.0f / 24);
        assertTrue(result);
    }

    @Test
    public void testIsTimeOverlapping_SlotEnclosesEvent() {
        // Slot from 8:00 to 12:00 encloses the event from 9:00 to 10:00, so it should overlap
        boolean result = ConvertTime.isTimeOverlapping(9.0f / 24, 10.0f / 24, 8.0f / 24, 12.0f / 24);
        assertTrue(result);
    }

    @Test
    public void testIsTimeOverlapping_StartEqualsEnd() {
        // Slot from 9:00 to 10:00 and event from 10:00 to 11:00 should not overlap (end time matches start time)
        boolean result = ConvertTime.isTimeOverlapping(10.0f / 24, 11.0f / 24, 9.0f / 24, 10.0f / 24);
        assertFalse(result);
    }
}
