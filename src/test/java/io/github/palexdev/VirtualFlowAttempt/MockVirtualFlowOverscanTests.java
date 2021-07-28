package io.github.palexdev.VirtualFlowAttempt;

import io.github.palexdev.materialfx.beans.NumberRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MockVirtualFlowOverscanTests extends ApplicationTest {
    private MockVirtualFlow mockVirtualFlow;

    @BeforeEach
    public void init() {
        mockVirtualFlow = new MockVirtualFlow();
        mockVirtualFlow.setOverscan(2);
        mockVirtualFlow.initialize();
    }

    @Test
    public void testScroll1() {
        NumberRange<Integer> vb = mockVirtualFlow.getVisibleIndexes();
        NumberRange<Integer> cvb = mockVirtualFlow.getCompleteVisibleIndexes();

        assertEqualsRange(vb, NumberRange.of(0, 4));
        assertEqualsRange(cvb, NumberRange.of(0, 6));
    }

    @Test
    public void testScroll2() {
        mockVirtualFlow.scrollBy(-2);

        NumberRange<Integer> vb = mockVirtualFlow.getVisibleIndexes();
        NumberRange<Integer> cvb = mockVirtualFlow.getCompleteVisibleIndexes();

        assertEqualsRange(vb, NumberRange.of(0, 5));
        assertEqualsRange(cvb, NumberRange.of(0, 7));
    }

    @Test
    public void testScroll3() {
        mockVirtualFlow.scrollBy(-3);

        NumberRange<Integer> vb = mockVirtualFlow.getVisibleIndexes();
        NumberRange<Integer> cvb = mockVirtualFlow.getCompleteVisibleIndexes();

        assertEqualsRange(vb, NumberRange.of(1, 5));
        assertEqualsRange(cvb, NumberRange.of(0, 7));
    }

    @Test
    public void testScroll4() {
        mockVirtualFlow.scrollBy(-10);

        NumberRange<Integer> vb = mockVirtualFlow.getVisibleIndexes();
        NumberRange<Integer> cvb = mockVirtualFlow.getCompleteVisibleIndexes();

        assertEqualsRange(vb, NumberRange.of(3, 8));
        assertEqualsRange(cvb, NumberRange.of(1, 9));
    }

    @Test
    public void testScroll5() {
        mockVirtualFlow.scrollBy(-9);

        NumberRange<Integer> vb = mockVirtualFlow.getVisibleIndexes();
        NumberRange<Integer> cvb = mockVirtualFlow.getCompleteVisibleIndexes();

        assertEqualsRange(vb, NumberRange.of(3, 7));
        assertEqualsRange(cvb, NumberRange.of(1, 9));
    }

    private <T extends Number> void assertEqualsRange(NumberRange<T> r1, NumberRange<T> r2) {
        assertEquals(r1.getMin(), r2.getMin());
        assertEquals(r1.getMax(), r2.getMax());
    }
}
