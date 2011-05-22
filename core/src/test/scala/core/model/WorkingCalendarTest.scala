/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

package core.model

import junit.framework._
import junit.framework.Assert._
import java.util.Calendar._
import java.text.SimpleDateFormat
import core.util.TestingEnvironment

class WorkingCalendarTest extends TestCase("WorkingCalendar") with TestingEnvironment {

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

    // global calendar
    // MON 31/05/2010 and TUE 01/06/2010 - forced holidays
    // FRI 28/05/2010 and SAT 29/05/2010 - forced working days
    // FRI-SUN - not working days
    assertTrue(global.isWorkingDay(mon24))
    assertTrue(global.isWorkingDay(tue25))
    assertTrue(global.isWorkingDay(wed26))
    assertTrue(global.isWorkingDay(thu27))
    assertTrue(global.isWorkingDay(fri28))
    assertTrue(global.isWorkingDay(sat29))
    assertFalse(global.isWorkingDay(sun30))
    assertFalse(global.isWorkingDay(mon31))
    assertFalse(global.isWorkingDay(tue01))
    assertTrue(global.isWorkingDay(wed02))
    assertTrue(global.isWorkingDay(thu03))
    assertFalse(global.isWorkingDay(fri04))
    assertFalse(global.isWorkingDay(sat05))
    assertFalse(global.isWorkingDay(sun06))

    // personal calendar based on global
    // FRI 28/05/2010 - forced holidays (ignores global calendar)
    // SUN 30/05/2010 - forced working day
    // week days ARE NOT IMPORTANT, IT USES GLOBAL
    assertTrue(personal.isWorkingDay(mon24))
    assertTrue(personal.isWorkingDay(tue25))
    assertTrue(personal.isWorkingDay(wed26))
    assertTrue(personal.isWorkingDay(thu27))
    assertFalse(personal.isWorkingDay(fri28))
    assertTrue(personal.isWorkingDay(sat29))
    assertTrue(personal.isWorkingDay(sun30))
    assertFalse(personal.isWorkingDay(mon31))
    assertFalse(personal.isWorkingDay(tue01))
    assertTrue(personal.isWorkingDay(wed02))
    assertTrue(personal.isWorkingDay(thu03))
    assertFalse(personal.isWorkingDay(fri04))
    assertFalse(personal.isWorkingDay(sat05))
    assertFalse(personal.isWorkingDay(sun06))
  }

  def testGetNextWorkingDay {
    // default calendar
    assertEquals(tue25,default.getNextWorkingDay(mon24))
    assertEquals(wed26,default.getNextWorkingDay(tue25))
    assertEquals(thu27,default.getNextWorkingDay(wed26))
    assertEquals(fri28,default.getNextWorkingDay(thu27))
    assertEquals(mon31,default.getNextWorkingDay(fri28))
    assertEquals(mon31,default.getNextWorkingDay(sat29))
    assertEquals(mon31,default.getNextWorkingDay(sun30))
    assertEquals(tue01,default.getNextWorkingDay(mon31))
    assertEquals(wed02,default.getNextWorkingDay(tue01))
    assertEquals(thu03,default.getNextWorkingDay(wed02))
    assertEquals(fri04,default.getNextWorkingDay(thu03))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"),default.getNextWorkingDay(fri04))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"),default.getNextWorkingDay(sat05))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"),default.getNextWorkingDay(sun06))

    // global calendar
    // MON 31/05/2010 and TUE 01/06/2010 - forced holidays
    // FRI 28/05/2010 and SAT 29/05/2010 - forced working days
    // FRI-SUN - not working days
    assertEquals(tue25,global.getNextWorkingDay(mon24))
    assertEquals(wed26,global.getNextWorkingDay(tue25))
    assertEquals(thu27,global.getNextWorkingDay(wed26))
    assertEquals(fri28,global.getNextWorkingDay(thu27))
    assertEquals(sat29,global.getNextWorkingDay(fri28))
    assertEquals(wed02,global.getNextWorkingDay(sat29))
    assertEquals(wed02,global.getNextWorkingDay(sun30))
    assertEquals(wed02,global.getNextWorkingDay(mon31))
    assertEquals(wed02,global.getNextWorkingDay(tue01))
    assertEquals(thu03,global.getNextWorkingDay(wed02))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"),global.getNextWorkingDay(thu03))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"),global.getNextWorkingDay(fri04))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"),global.getNextWorkingDay(sat05))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"),global.getNextWorkingDay(sun06))

    // personal calendar based on global
    // FRI 28/05/2010 - forced holidays (ignores global calendar)
    // SUN 30/05/2010 - forced working day
    // week days ARE NOT IMPORTANT, IT USES GLOBAL
    assertEquals(tue25,personal.getNextWorkingDay(mon24))
    assertEquals(wed26,personal.getNextWorkingDay(tue25))
    assertEquals(thu27,personal.getNextWorkingDay(wed26))
    assertEquals(sat29,personal.getNextWorkingDay(thu27))
    assertEquals(sat29,personal.getNextWorkingDay(fri28))
    assertEquals(sun30,personal.getNextWorkingDay(sat29))
    assertEquals(wed02,personal.getNextWorkingDay(sun30))
    assertEquals(wed02,personal.getNextWorkingDay(mon31))
    assertEquals(wed02,personal.getNextWorkingDay(tue01))
    assertEquals(thu03,personal.getNextWorkingDay(wed02))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"),personal.getNextWorkingDay(thu03))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"),personal.getNextWorkingDay(fri04))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"),personal.getNextWorkingDay(sat05))
    assertEquals("dd/MM/yyyy".parse("07/06/2010"),personal.getNextWorkingDay(sun06))
  }

  def testGetPreviousWorkingDay {
    // default calendar
    assertEquals("dd/MM/yyyy".parse("21/05/2010"),default.getPreviousWorkingDay(mon24))
    assertEquals(mon24,default.getPreviousWorkingDay(tue25))
    assertEquals(tue25,default.getPreviousWorkingDay(wed26))
    assertEquals(wed26,default.getPreviousWorkingDay(thu27))
    assertEquals(thu27,default.getPreviousWorkingDay(fri28))
    assertEquals(fri28,default.getPreviousWorkingDay(sat29))
    assertEquals(fri28,default.getPreviousWorkingDay(sun30))
    assertEquals(fri28,default.getPreviousWorkingDay(mon31))
    assertEquals(mon31,default.getPreviousWorkingDay(tue01))
    assertEquals(tue01,default.getPreviousWorkingDay(wed02))
    assertEquals(wed02,default.getPreviousWorkingDay(thu03))
    assertEquals(thu03,default.getPreviousWorkingDay(fri04))
    assertEquals(fri04,default.getPreviousWorkingDay(sat05))
    assertEquals(fri04,default.getPreviousWorkingDay(sun06))

    // global calendar
    // MON 31/05/2010 and TUE 01/06/2010 - forced holidays
    // FRI 28/05/2010 and SAT 29/05/2010 - forced working days
    // FRI-SUN - not working days
    assertEquals("dd/MM/yyyy".parse("20/05/2010"),global.getPreviousWorkingDay(mon24))
    assertEquals(mon24,global.getPreviousWorkingDay(tue25))
    assertEquals(tue25,global.getPreviousWorkingDay(wed26))
    assertEquals(wed26,global.getPreviousWorkingDay(thu27))
    assertEquals(thu27,global.getPreviousWorkingDay(fri28))
    assertEquals(fri28,global.getPreviousWorkingDay(sat29))
    assertEquals(sat29,global.getPreviousWorkingDay(sun30))
    assertEquals(sat29,global.getPreviousWorkingDay(mon31))
    assertEquals(sat29,global.getPreviousWorkingDay(tue01))
    assertEquals(sat29,global.getPreviousWorkingDay(wed02))
    assertEquals(wed02,global.getPreviousWorkingDay(thu03))
    assertEquals(thu03,global.getPreviousWorkingDay(fri04))
    assertEquals(thu03,global.getPreviousWorkingDay(sat05))
    assertEquals(thu03,global.getPreviousWorkingDay(sun06))

    // personal calendar based on global
    // FRI 28/05/2010 - forced holidays (ignores global calendar)
    // SUN 30/05/2010 - forced working day
    // week days ARE NOT IMPORTANT, IT USES GLOBAL
    assertEquals("dd/MM/yyyy".parse("20/05/2010"),personal.getPreviousWorkingDay(mon24))
    assertEquals(mon24,personal.getPreviousWorkingDay(tue25))
    assertEquals(tue25,personal.getPreviousWorkingDay(wed26))
    assertEquals(wed26,personal.getPreviousWorkingDay(thu27))
    assertEquals(thu27,personal.getPreviousWorkingDay(fri28))
    assertEquals(thu27,personal.getPreviousWorkingDay(sat29))
    assertEquals(sat29,personal.getPreviousWorkingDay(sun30))
    assertEquals(sun30,personal.getPreviousWorkingDay(mon31))
    assertEquals(sun30,personal.getPreviousWorkingDay(tue01))
    assertEquals(sun30,personal.getPreviousWorkingDay(wed02))
    assertEquals(wed02,personal.getPreviousWorkingDay(thu03))
    assertEquals(thu03,personal.getPreviousWorkingDay(fri04))
    assertEquals(thu03,personal.getPreviousWorkingDay(sat05))
    assertEquals(thu03,personal.getPreviousWorkingDay(sun06))
  }

  def testGetPeriod_ByFinishTime {
    // zero
    assertEquals(Period(fri28_0,fri28_0,0), default.getPeriod(fri28_0,fri28_0))
    assertEquals(Period(mon31_0,mon31_0,0), default.getPeriod(fri28_8,sat29_0))
    assertEquals(Period(mon31_0,mon31_0,0), default.getPeriod(fri28_8,mon31_0))
    assertEquals(Period(mon31_0,mon31_0,0), default.getPeriod(sat29_0,fri28_8))
    assertEquals(Period(mon31_0,mon31_0,0), default.getPeriod(mon31_0,fri28_8))

    // starts and ends the same day (whole day)
    assertEquals(Period(fri28_0,fri28_8,8), default.getPeriod(fri28_0,fri28_8))
    assertEquals(Period(fri28_0,fri28_8,8), default.getPeriod(fri28_8,fri28_0))

    // starts and ends the same day (whole day)
    assertEquals(Period(fri28_4,fri28_5,1), default.getPeriod(fri28_4,fri28_5))
    assertEquals(Period(fri28_4,fri28_5,1), default.getPeriod(fri28_5,fri28_4))

    // spans two days
    assertEquals(Period(mon31_0,tue01_8,16), default.getPeriod(mon31_0, tue01_8))
    assertEquals(Period(mon31_0,tue01_8,16), default.getPeriod(tue01_8, mon31_0))

    // starts when day is over
    assertEquals(Period(tue01_0,tue01_8,8), default.getPeriod(mon31_8, tue01_8))
    assertEquals(Period(mon31_0,mon31_8,8), default.getPeriod(tue01_0, mon31_0))

    // ends when day is not started
    assertEquals(Period(tue01_0,tue01_8,8), default.getPeriod(tue01_8, mon31_8))
    assertEquals(Period(mon31_0,mon31_8,8), default.getPeriod(mon31_0, tue01_0))

    // starts on holiday
    assertEquals(Period(mon31_0,mon31_8,8), default.getPeriod(sat29_4, mon31_8))
    assertEquals(Period(fri28_0,fri28_8,8), default.getPeriod(sat29_4, fri28_0))

    // includes holiday
    assertEquals(Period(fri28_0,mon31_4,12), default.getPeriod(fri28_0, mon31_4))
    assertEquals(Period(fri28_0,mon31_4,12), default.getPeriod(mon31_4, fri28_0))
  }

  def testGetPeriod_ByHours() {
    // zero
    assertEquals(Period(fri28_0,fri28_0,0), default.getPeriod(fri28_0,0))
    assertEquals(Period(mon31_0,mon31_0,0), default.getPeriod(fri28_8,0))
    assertEquals(Period(mon31_0,mon31_0,0), default.getPeriod(sat29_0,0))
    assertEquals(Period(mon31_0,mon31_0,0), default.getPeriod(mon31_0,0))

    // the same day (whole day)
    assertEquals(Period(fri28_0,fri28_8,8), default.getPeriod(fri28_0, 8))
    assertEquals(Period(fri28_0,fri28_8,8), default.getPeriod(fri28_8, -8))

    // the same day (part of day)
    assertEquals(Period(fri28_4,fri28_5,1), default.getPeriod(fri28_4, 1))
    assertEquals(Period(fri28_4,fri28_5,1), default.getPeriod(fri28_5, -1))

    // spans two days
    assertEquals(Period(mon31_0,tue01_8,16), default.getPeriod(mon31_0, 16))
    assertEquals(Period(mon31_0,tue01_8,16), default.getPeriod(tue01_8, -16))

    // starts when day is over
    assertEquals(Period(tue01_0,tue01_8,8), default.getPeriod(mon31_8, 8))
    assertEquals(Period(mon31_0,mon31_8,8), default.getPeriod(tue01_0, -8))

    // starts on holiday
    assertEquals(Period(mon31_0,mon31_8,8), default.getPeriod(sat29_4, 8))
    assertEquals(Period(fri28_0,fri28_8,8), default.getPeriod(sat29_4, -8))

    // includes holiday
    assertEquals(Period(fri28_0,mon31_4,12), default.getPeriod(fri28_0, 12))
    assertEquals(Period(fri28_0,mon31_4,12), default.getPeriod(mon31_4, -12))
  }


}