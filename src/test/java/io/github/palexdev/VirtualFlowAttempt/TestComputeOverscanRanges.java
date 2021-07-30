package io.github.palexdev.VirtualFlowAttempt;

import io.github.palexdev.materialfx.beans.NumberRange;
import io.github.palexdev.materialfx.utils.NumberUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestComputeOverscanRanges {
    private final int overscan = 2;
    private final int n = 19;

    @Test
    public void testOverscan1() {
        NumberRange<Integer> visibleRange = NumberRange.of(0, 4);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(0, 1), upper);
        assertEqualsRange(NumberRange.of(5, 6), lower);
    }

    @Test
    public void testOverscan2() {
        NumberRange<Integer> visibleRange = NumberRange.of(0, 5);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(0, 1), upper);
        assertEqualsRange(NumberRange.of(6, 7), lower);
    }

    @Test
    public void testOverscan3() {
        NumberRange<Integer> visibleRange = NumberRange.of(1, 5);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(0, 1), upper);
        assertEqualsRange(NumberRange.of(6, 7), lower);
    }

    @Test
    public void testOverscan4() {
        NumberRange<Integer> visibleRange = NumberRange.of(1, 6);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(0, 1), upper);
        assertEqualsRange(NumberRange.of(7, 8), lower);
    }

    @Test
    public void testOverscan5() {
        NumberRange<Integer> visibleRange = NumberRange.of(2, 6);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(0, 1), upper);
        assertEqualsRange(NumberRange.of(7, 8), lower);
    }

    @Test
    public void testOverscan6() {
        NumberRange<Integer> visibleRange = NumberRange.of(2, 7);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(0, 1), upper);
        assertEqualsRange(NumberRange.of(8, 9), lower);
    }

    @Test
    public void testOverscan7() {
        NumberRange<Integer> visibleRange = NumberRange.of(3, 7);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(1, 2), upper);
        assertEqualsRange(NumberRange.of(8, 9), lower);
    }

    @Test
    public void testOverscan8() {
        NumberRange<Integer> visibleRange = NumberRange.of(3, 8);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(1, 2), upper);
        assertEqualsRange(NumberRange.of(9, 10), lower);
    }

    @Test
    public void testOverscan9() {
        NumberRange<Integer> visibleRange = NumberRange.of(4, 8);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(2, 3), upper);
        assertEqualsRange(NumberRange.of(9, 10), lower);
    }

    @Test
    public void testOverscan10() {
        NumberRange<Integer> visibleRange = NumberRange.of(4, 9);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(2, 3), upper);
        assertEqualsRange(NumberRange.of(10, 11), lower);
    }

    @Test
    public void testOverscan11() {
        NumberRange<Integer> visibleRange = NumberRange.of(5, 9);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(3, 4), upper);
        assertEqualsRange(NumberRange.of(10, 11), lower);
    }

    @Test
    public void testOverscan12() {
        NumberRange<Integer> visibleRange = NumberRange.of(5, 10);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(3, 4), upper);
        assertEqualsRange(NumberRange.of(11, 12), lower);
    }

    @Test
    public void testOverscan13() {
        NumberRange<Integer> visibleRange = NumberRange.of(6, 10);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(4, 5), upper);
        assertEqualsRange(NumberRange.of(11, 12), lower);
    }

    @Test
    public void testOverscan14() {
        NumberRange<Integer> visibleRange = NumberRange.of(6, 11);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(4, 5), upper);
        assertEqualsRange(NumberRange.of(12, 13), lower);
    }

    @Test
    public void testOverscan15() {
        NumberRange<Integer> visibleRange = NumberRange.of(7, 11);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(5, 6), upper);
        assertEqualsRange(NumberRange.of(12, 13), lower);
    }

    @Test
    public void testOverscan16() {
        NumberRange<Integer> visibleRange = NumberRange.of(7, 12);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(5, 6), upper);
        assertEqualsRange(NumberRange.of(13, 14), lower);
    }

    @Test
    public void testOverscan17() {
        NumberRange<Integer> visibleRange = NumberRange.of(8, 12);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(6, 7), upper);
        assertEqualsRange(NumberRange.of(13, 14), lower);
    }

    @Test
    public void testOverscan18() {
        NumberRange<Integer> visibleRange = NumberRange.of(8, 13);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(6, 7), upper);
        assertEqualsRange(NumberRange.of(14, 15), lower);
    }

    @Test
    public void testOverscan19() {
        NumberRange<Integer> visibleRange = NumberRange.of(9, 13);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(7, 8), upper);
        assertEqualsRange(NumberRange.of(14, 15), lower);
    }

    @Test
    public void testOverscan20() {
        NumberRange<Integer> visibleRange = NumberRange.of(9, 14);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(7, 8), upper);
        assertEqualsRange(NumberRange.of(15, 16), lower);
    }

    @Test
    public void testOverscan21() {
        NumberRange<Integer> visibleRange = NumberRange.of(10, 14);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(8, 9), upper);
        assertEqualsRange(NumberRange.of(15, 16), lower);
    }

    @Test
    public void testOverscan22() {
        NumberRange<Integer> visibleRange = NumberRange.of(10, 15);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(8, 9), upper);
        assertEqualsRange(NumberRange.of(16, 17), lower);
    }

    @Test
    public void testOverscan23() {
        NumberRange<Integer> visibleRange = NumberRange.of(11, 15);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(9, 10), upper);
        assertEqualsRange(NumberRange.of(16, 17), lower);
    }

    @Test
    public void testOverscan24() {
        NumberRange<Integer> visibleRange = NumberRange.of(11, 16);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(9, 10), upper);
        assertEqualsRange(NumberRange.of(17, 18), lower);
    }

    @Test
    public void testOverscan25() {
        NumberRange<Integer> visibleRange = NumberRange.of(12, 16);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(10, 11), upper);
        assertEqualsRange(NumberRange.of(17, 18), lower);
    }

    @Test
    public void testOverscan26() {
        NumberRange<Integer> visibleRange = NumberRange.of(12, 17);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(10, 11), upper);
        assertEqualsRange(NumberRange.of(18, 19), lower);
    }

    @Test
    public void testOverscan27() {
        NumberRange<Integer> visibleRange = NumberRange.of(13, 17);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(11, 12), upper);
        assertEqualsRange(NumberRange.of(18, 19), lower);
    }

    @Test
    public void testOverscan28() {
        NumberRange<Integer> visibleRange = NumberRange.of(13, 18);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(11, 12), upper);
        assertEqualsRange(NumberRange.of(18, 19), lower);
    }

    @Test
    public void testOverscan29() {
        NumberRange<Integer> visibleRange = NumberRange.of(14, 18);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(12, 13), upper);
        assertEqualsRange(NumberRange.of(18, 19), lower);
    }

    @Test
    public void testOverscan30() {
        NumberRange<Integer> visibleRange = NumberRange.of(14, 19);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(12, 13), upper);
        assertEqualsRange(NumberRange.of(18, 19), lower);
    }

    @Test
    public void testOverscan31() {
        NumberRange<Integer> visibleRange = NumberRange.of(15, 19);
        NumberRange<Integer> upper = computeUpperRange(visibleRange);
        NumberRange<Integer> lower = computeLowerRange(visibleRange);
        assertEqualsRange(NumberRange.of(13, 14), upper);
        assertEqualsRange(NumberRange.of(18, 19), lower);
    }

    private NumberRange<Integer> computeUpperRange(NumberRange<Integer> range) {
        int min = NumberUtils.clamp(range.getMin() - overscan, 0, n);
        int max = min;
        for (int i = 1; i < overscan; i++) {
            max += 1;
        }
        return NumberRange.of(min, max);
    }

    private NumberRange<Integer> computeLowerRange(NumberRange<Integer> range) {
        int max = NumberUtils.clamp(range.getMax() + overscan, 0, n);
        int min = max;
        for (int i = 1; i < overscan; i++) {
            min -= 1;
        }
        return NumberRange.of(min, max);
    }

    private <T extends Number> void assertEqualsRange(NumberRange<T> r1, NumberRange<T> r2) {
        assertEquals(r1.getMin(), r2.getMin());
        assertEquals(r1.getMax(), r2.getMax());
    }
}
