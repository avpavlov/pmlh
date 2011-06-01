/*
 * Copyright 2010-2011 Alexander Pavlov. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALEXANDER PAVLOV ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ALEXANDER PAVLOV OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Alexander Pavlov.
 */

package core.model

import junit.framework._
import junit.framework.Assert._
import core.util.TestingEnvironment

class WorkingCalendarTest extends TestCase("WorkingCalendar") with TestingEnvironment {

  // 'short-week' calendar, working days are MON-THU
  // MON 31/05/2010 and TUE 01/06/2010 - forced holidays
  // FRI 28/05/2010 and SAT 29/05/2010 - forced working days
  val shortWeek = new WorkingCalendar(workingDaysOfWeek = List(Mon, Tue, Wed, Thu), holidays = List(mon31, tue01), workingDays = List(fri28, sat29))

  // customized 'short-week' calendar
  // FRI 28/05/2010 - forced holidays (ignores parent calendar)
  // SUN 30/05/2010 - forced working day (ignores parent calendar)
  val customizedShortWeek = new WorkingCalendar(shortWeek, Nil, 8, List(fri28), List(sun30))

  // 'working week-end' calendar, working days are SAT-SUN
  // all parent calendar settings are ignored
  val workingWeekend = new WorkingCalendar(workingDaysOfWeek = List(Sat, Sun))

  def testIsWorkingDay {
    // default calendar
    assertTrue(default.isWorkingDay(mon24))
    assertTrue(default.isWorkingDay(tue25))
    assertTrue(default.isWorkingDay(wed26))
    assertTrue(default.isWorkingDay(thu27))
    assertTrue(default.isWorkingDay(fri28))
    assertFalse(default.isWorkingDay(sat29))
    assertFalse(default.isWorkingDay(sun30))
    assertTrue(default.isWorkingDay(mon31))
    assertTrue(default.isWorkingDay(tue01))
    assertTrue(default.isWorkingDay(wed02))
    assertTrue(default.isWorkingDay(thu03))
    assertTrue(default.isWorkingDay(fri04))
    assertFalse(default.isWorkingDay(sat05))
    assertFalse(default.isWorkingDay(sun06))

    // 'short-week' calendar, working days are MON-THU
    // MON 31/05/2010 and TUE 01/06/2010 - forced holidays
    // FRI 28/05/2010 and SAT 29/05/2010 - forced working days
    assertTrue(shortWeek.isWorkingDay(mon24))
    assertTrue(shortWeek.isWorkingDay(tue25))
    assertTrue(shortWeek.isWorkingDay(wed26))
    assertTrue(shortWeek.isWorkingDay(thu27))
    assertTrue(shortWeek.isWorkingDay(fri28))
    assertTrue(shortWeek.isWorkingDay(sat29))
    assertFalse(shortWeek.isWorkingDay(sun30))
    assertFalse(shortWeek.isWorkingDay(mon31))
    assertFalse(shortWeek.isWorkingDay(tue01))
    assertTrue(shortWeek.isWorkingDay(wed02))
    assertTrue(shortWeek.isWorkingDay(thu03))
    assertFalse(shortWeek.isWorkingDay(fri04))
    assertFalse(shortWeek.isWorkingDay(sat05))
    assertFalse(shortWeek.isWorkingDay(sun06))

    // customized 'short-week' calendar
    // FRI 28/05/2010 - forced holidays (ignores parent calendar)
    // SUN 30/05/2010 - forced working day (ignores parent calendar)
    assertTrue(customizedShortWeek.isWorkingDay(mon24))
    assertTrue(customizedShortWeek.isWorkingDay(tue25))
    assertTrue(customizedShortWeek.isWorkingDay(wed26))
    assertTrue(customizedShortWeek.isWorkingDay(thu27))
    assertFalse(customizedShortWeek.isWorkingDay(fri28))
    assertTrue(customizedShortWeek.isWorkingDay(sat29))
    assertTrue(customizedShortWeek.isWorkingDay(sun30))
    assertFalse(customizedShortWeek.isWorkingDay(mon31))
    assertFalse(customizedShortWeek.isWorkingDay(tue01))
    assertTrue(customizedShortWeek.isWorkingDay(wed02))
    assertTrue(customizedShortWeek.isWorkingDay(thu03))
    assertFalse(customizedShortWeek.isWorkingDay(fri04))
    assertFalse(customizedShortWeek.isWorkingDay(sat05))
    assertFalse(customizedShortWeek.isWorkingDay(sun06))

    // 'working week-end' calendar, working days are SAT-SUN
    // all parent calendar settings are ignored
    assertFalse(workingWeekend.isWorkingDay(mon24))
    assertFalse(workingWeekend.isWorkingDay(tue25))
    assertFalse(workingWeekend.isWorkingDay(wed26))
    assertFalse(workingWeekend.isWorkingDay(thu27))
    assertFalse(workingWeekend.isWorkingDay(fri28))
    assertTrue(workingWeekend.isWorkingDay(sat29))
    assertTrue(workingWeekend.isWorkingDay(sun30))
    assertFalse(workingWeekend.isWorkingDay(mon31))
    assertFalse(workingWeekend.isWorkingDay(tue01))
    assertFalse(workingWeekend.isWorkingDay(wed02))
    assertFalse(workingWeekend.isWorkingDay(thu03))
    assertFalse(workingWeekend.isWorkingDay(fri04))
    assertTrue(workingWeekend.isWorkingDay(sat05))
    assertTrue(workingWeekend.isWorkingDay(sun06))
  }

  def testGetNextWorkingDay {
    // default calendar
    assertEquals(tue25, default.getNextWorkingDay(mon24))
    assertEquals(wed26, default.getNextWorkingDay(tue25))
    assertEquals(thu27, default.getNextWorkingDay(wed26))
    assertEquals(fri28, default.getNextWorkingDay(thu27))
    assertEquals(mon31, default.getNextWorkingDay(fri28))
    assertEquals(mon31, default.getNextWorkingDay(sat29))
    assertEquals(mon31, default.getNextWorkingDay(sun30))
    assertEquals(tue01, default.getNextWorkingDay(mon31))
    assertEquals(wed02, default.getNextWorkingDay(tue01))
    assertEquals(thu03, default.getNextWorkingDay(wed02))
    assertEquals(fri04, default.getNextWorkingDay(thu03))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"), default.getNextWorkingDay(fri04))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"), default.getNextWorkingDay(sat05))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"), default.getNextWorkingDay(sun06))

    // 'short-week' calendar, working days are MON-THU
    // MON 31/05/2010 and TUE 01/06/2010 - forced holidays
    // FRI 28/05/2010 and SAT 29/05/2010 - forced working days
    assertEquals(tue25, shortWeek.getNextWorkingDay(mon24))
    assertEquals(wed26, shortWeek.getNextWorkingDay(tue25))
    assertEquals(thu27, shortWeek.getNextWorkingDay(wed26))
    assertEquals(fri28, shortWeek.getNextWorkingDay(thu27))
    assertEquals(sat29, shortWeek.getNextWorkingDay(fri28))
    assertEquals(wed02, shortWeek.getNextWorkingDay(sat29))
    assertEquals(wed02, shortWeek.getNextWorkingDay(sun30))
    assertEquals(wed02, shortWeek.getNextWorkingDay(mon31))
    assertEquals(wed02, shortWeek.getNextWorkingDay(tue01))
    assertEquals(thu03, shortWeek.getNextWorkingDay(wed02))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"), shortWeek.getNextWorkingDay(thu03))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"), shortWeek.getNextWorkingDay(fri04))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"), shortWeek.getNextWorkingDay(sat05))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"), shortWeek.getNextWorkingDay(sun06))

    // customized 'short-week' calendar
    // FRI 28/05/2010 - forced holidays (ignores parent calendar)
    // SUN 30/05/2010 - forced working day (ignores parent calendar)
    assertEquals(tue25, customizedShortWeek.getNextWorkingDay(mon24))
    assertEquals(wed26, customizedShortWeek.getNextWorkingDay(tue25))
    assertEquals(thu27, customizedShortWeek.getNextWorkingDay(wed26))
    assertEquals(sat29, customizedShortWeek.getNextWorkingDay(thu27))
    assertEquals(sat29, customizedShortWeek.getNextWorkingDay(fri28))
    assertEquals(sun30, customizedShortWeek.getNextWorkingDay(sat29))
    assertEquals(wed02, customizedShortWeek.getNextWorkingDay(sun30))
    assertEquals(wed02, customizedShortWeek.getNextWorkingDay(mon31))
    assertEquals(wed02, customizedShortWeek.getNextWorkingDay(tue01))
    assertEquals(thu03, customizedShortWeek.getNextWorkingDay(wed02))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"), customizedShortWeek.getNextWorkingDay(thu03))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"), customizedShortWeek.getNextWorkingDay(fri04))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"), customizedShortWeek.getNextWorkingDay(sat05))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"), customizedShortWeek.getNextWorkingDay(sun06))

    // 'working week-end' calendar, working days are SAT-SUN
    // all parent calendar settings are ignored
    assertEquals(sat29, workingWeekend.getNextWorkingDay(mon24))
    assertEquals(sat29, workingWeekend.getNextWorkingDay(tue25))
    assertEquals(sat29, workingWeekend.getNextWorkingDay(wed26))
    assertEquals(sat29, workingWeekend.getNextWorkingDay(thu27))
    assertEquals(sat29, workingWeekend.getNextWorkingDay(fri28))
    assertEquals(sun30, workingWeekend.getNextWorkingDay(sat29))
    assertEquals(sat05, workingWeekend.getNextWorkingDay(sun30))
    assertEquals(sat05, workingWeekend.getNextWorkingDay(mon31))
    assertEquals(sat05, workingWeekend.getNextWorkingDay(tue01))
    assertEquals(sat05, workingWeekend.getNextWorkingDay(wed02))
    assertEquals(sat05, workingWeekend.getNextWorkingDay(thu03))
    assertEquals(sat05, workingWeekend.getNextWorkingDay(fri04))
    assertEquals(sun06, workingWeekend.getNextWorkingDay(sat05))
    assertEquals("dd/MM/yyyy".parse("12/06/2010"), workingWeekend.getNextWorkingDay(sun06))
  }

  def testGetPreviousWorkingDay {
    // default calendar
    assertEquals("dd/MM/yyyy".parse("21/05/2010"), default.getPreviousWorkingDay(mon24))
    assertEquals(mon24, default.getPreviousWorkingDay(tue25))
    assertEquals(tue25, default.getPreviousWorkingDay(wed26))
    assertEquals(wed26, default.getPreviousWorkingDay(thu27))
    assertEquals(thu27, default.getPreviousWorkingDay(fri28))
    assertEquals(fri28, default.getPreviousWorkingDay(sat29))
    assertEquals(fri28, default.getPreviousWorkingDay(sun30))
    assertEquals(fri28, default.getPreviousWorkingDay(mon31))
    assertEquals(mon31, default.getPreviousWorkingDay(tue01))
    assertEquals(tue01, default.getPreviousWorkingDay(wed02))
    assertEquals(wed02, default.getPreviousWorkingDay(thu03))
    assertEquals(thu03, default.getPreviousWorkingDay(fri04))
    assertEquals(fri04, default.getPreviousWorkingDay(sat05))
    assertEquals(fri04, default.getPreviousWorkingDay(sun06))

    // 'short-week' calendar, working days are MON-THU
    // MON 31/05/2010 and TUE 01/06/2010 - forced holidays
    // FRI 28/05/2010 and SAT 29/05/2010 - forced working days
    assertEquals("dd/MM/yyyy".parse("20/05/2010"), shortWeek.getPreviousWorkingDay(mon24))
    assertEquals(mon24, shortWeek.getPreviousWorkingDay(tue25))
    assertEquals(tue25, shortWeek.getPreviousWorkingDay(wed26))
    assertEquals(wed26, shortWeek.getPreviousWorkingDay(thu27))
    assertEquals(thu27, shortWeek.getPreviousWorkingDay(fri28))
    assertEquals(fri28, shortWeek.getPreviousWorkingDay(sat29))
    assertEquals(sat29, shortWeek.getPreviousWorkingDay(sun30))
    assertEquals(sat29, shortWeek.getPreviousWorkingDay(mon31))
    assertEquals(sat29, shortWeek.getPreviousWorkingDay(tue01))
    assertEquals(sat29, shortWeek.getPreviousWorkingDay(wed02))
    assertEquals(wed02, shortWeek.getPreviousWorkingDay(thu03))
    assertEquals(thu03, shortWeek.getPreviousWorkingDay(fri04))
    assertEquals(thu03, shortWeek.getPreviousWorkingDay(sat05))
    assertEquals(thu03, shortWeek.getPreviousWorkingDay(sun06))

    // customized 'short-week' calendar
    // FRI 28/05/2010 - forced holidays (ignores parent calendar)
    // SUN 30/05/2010 - forced working day (ignores parent calendar)
    assertEquals("dd/MM/yyyy".parse("20/05/2010"), customizedShortWeek.getPreviousWorkingDay(mon24))
    assertEquals(mon24, customizedShortWeek.getPreviousWorkingDay(tue25))
    assertEquals(tue25, customizedShortWeek.getPreviousWorkingDay(wed26))
    assertEquals(wed26, customizedShortWeek.getPreviousWorkingDay(thu27))
    assertEquals(thu27, customizedShortWeek.getPreviousWorkingDay(fri28))
    assertEquals(thu27, customizedShortWeek.getPreviousWorkingDay(sat29))
    assertEquals(sat29, customizedShortWeek.getPreviousWorkingDay(sun30))
    assertEquals(sun30, customizedShortWeek.getPreviousWorkingDay(mon31))
    assertEquals(sun30, customizedShortWeek.getPreviousWorkingDay(tue01))
    assertEquals(sun30, customizedShortWeek.getPreviousWorkingDay(wed02))
    assertEquals(wed02, customizedShortWeek.getPreviousWorkingDay(thu03))
    assertEquals(thu03, customizedShortWeek.getPreviousWorkingDay(fri04))
    assertEquals(thu03, customizedShortWeek.getPreviousWorkingDay(sat05))
    assertEquals(thu03, customizedShortWeek.getPreviousWorkingDay(sun06))

    // 'working week-end' calendar, working days are SAT-SUN
    // all parent calendar settings are ignored
    assertEquals("dd/MM/yyyy".parse("23/05/2010"), workingWeekend.getPreviousWorkingDay(mon24))
    assertEquals("dd/MM/yyyy".parse("23/05/2010"), workingWeekend.getPreviousWorkingDay(tue25))
    assertEquals("dd/MM/yyyy".parse("23/05/2010"), workingWeekend.getPreviousWorkingDay(wed26))
    assertEquals("dd/MM/yyyy".parse("23/05/2010"), workingWeekend.getPreviousWorkingDay(thu27))
    assertEquals("dd/MM/yyyy".parse("23/05/2010"), workingWeekend.getPreviousWorkingDay(fri28))
    assertEquals("dd/MM/yyyy".parse("23/05/2010"), workingWeekend.getPreviousWorkingDay(sat29))
    assertEquals(sat29, workingWeekend.getPreviousWorkingDay(sun30))
    assertEquals(sun30, workingWeekend.getPreviousWorkingDay(mon31))
    assertEquals(sun30, workingWeekend.getPreviousWorkingDay(tue01))
    assertEquals(sun30, workingWeekend.getPreviousWorkingDay(wed02))
    assertEquals(sun30, workingWeekend.getPreviousWorkingDay(thu03))
    assertEquals(sun30, workingWeekend.getPreviousWorkingDay(fri04))
    assertEquals(sun30, workingWeekend.getPreviousWorkingDay(sat05))
    assertEquals(sat05, workingWeekend.getPreviousWorkingDay(sun06))
  }

  def testGetPeriod_ByFinishTime {
    // zero
    assertEquals(Period(fri28_0, fri28_0, 0), default.getPeriod(fri28_0, fri28_0))
    assertEquals(Period(mon31_0, mon31_0, 0), default.getPeriod(fri28_8, sat29_0))
    assertEquals(Period(mon31_0, mon31_0, 0), default.getPeriod(fri28_8, mon31_0))
    assertEquals(Period(mon31_0, mon31_0, 0), default.getPeriod(sat29_0, fri28_8))
    assertEquals(Period(mon31_0, mon31_0, 0), default.getPeriod(mon31_0, fri28_8))

    // starts and ends the same day (whole day)
    assertEquals(Period(fri28_0, fri28_8, 8), default.getPeriod(fri28_0, fri28_8))
    assertEquals(Period(fri28_0, fri28_8, 8), default.getPeriod(fri28_8, fri28_0))

    // starts and ends the same day (whole day)
    assertEquals(Period(fri28_4, fri28_5, 1), default.getPeriod(fri28_4, fri28_5))
    assertEquals(Period(fri28_4, fri28_5, 1), default.getPeriod(fri28_5, fri28_4))

    // spans two days
    assertEquals(Period(mon31_0, tue01_8, 16), default.getPeriod(mon31_0, tue01_8))
    assertEquals(Period(mon31_0, tue01_8, 16), default.getPeriod(tue01_8, mon31_0))

    // starts when day is over
    assertEquals(Period(tue01_0, tue01_8, 8), default.getPeriod(mon31_8, tue01_8))
    assertEquals(Period(mon31_0, mon31_8, 8), default.getPeriod(tue01_0, mon31_0))

    // ends when day is not started
    assertEquals(Period(tue01_0, tue01_8, 8), default.getPeriod(tue01_8, mon31_8))
    assertEquals(Period(mon31_0, mon31_8, 8), default.getPeriod(mon31_0, tue01_0))

    // starts on holiday
    assertEquals(Period(mon31_0, mon31_8, 8), default.getPeriod(sat29_4, mon31_8))
    assertEquals(Period(fri28_0, fri28_8, 8), default.getPeriod(sat29_4, fri28_0))

    // includes holiday
    assertEquals(Period(fri28_0, mon31_4, 12), default.getPeriod(fri28_0, mon31_4))
    assertEquals(Period(fri28_0, mon31_4, 12), default.getPeriod(mon31_4, fri28_0))
  }

  def testGetPeriod_ByHours() {
    // zero
    assertEquals(Period(fri28_0, fri28_0, 0), default.getPeriod(fri28_0, 0))
    assertEquals(Period(mon31_0, mon31_0, 0), default.getPeriod(fri28_8, 0))
    assertEquals(Period(mon31_0, mon31_0, 0), default.getPeriod(sat29_0, 0))
    assertEquals(Period(mon31_0, mon31_0, 0), default.getPeriod(mon31_0, 0))

    // the same day (whole day)
    assertEquals(Period(fri28_0, fri28_8, 8), default.getPeriod(fri28_0, 8))
    assertEquals(Period(fri28_0, fri28_8, 8), default.getPeriod(fri28_8, -8))

    // the same day (part of day)
    assertEquals(Period(fri28_4, fri28_5, 1), default.getPeriod(fri28_4, 1))
    assertEquals(Period(fri28_4, fri28_5, 1), default.getPeriod(fri28_5, -1))

    // spans two days
    assertEquals(Period(mon31_0, tue01_8, 16), default.getPeriod(mon31_0, 16))
    assertEquals(Period(mon31_0, tue01_8, 16), default.getPeriod(tue01_8, -16))

    // starts when day is over
    assertEquals(Period(tue01_0, tue01_8, 8), default.getPeriod(mon31_8, 8))
    assertEquals(Period(mon31_0, mon31_8, 8), default.getPeriod(tue01_0, -8))

    // starts on holiday
    assertEquals(Period(mon31_0, mon31_8, 8), default.getPeriod(sat29_4, 8))
    assertEquals(Period(fri28_0, fri28_8, 8), default.getPeriod(sat29_4, -8))

    // includes holiday
    assertEquals(Period(fri28_0, mon31_4, 12), default.getPeriod(fri28_0, 12))
    assertEquals(Period(fri28_0, mon31_4, 12), default.getPeriod(mon31_4, -12))
  }


}