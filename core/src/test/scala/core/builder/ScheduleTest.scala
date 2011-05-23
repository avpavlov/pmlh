/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

package core.builder

import junit.framework.TestCase
import core.util.TestingEnvironment
import junit.framework._

import Assert._
import core.model._

class ScheduleTest extends TestCase("Schedule") with TestingEnvironment {

  val task32hForDeveloper = "task 1" needs 32.hours of developer
  val task40hForDeveloper = "task 2" needs 40.hours of developer

  val schedule = new Schedule(List(
      developerC implements task32hForDeveloper from (fri28 at 0.hours) during 8.hours,
      developerC implements task40hForDeveloper from (mon24 at 0.hours) during 8.hours,
      developerC implements task32hForDeveloper from (wed26 at 0.hours) during 8.hours,
      developerD implements task32hForDeveloper from (wed26 at 0.hours) during 8.hours
    ))

  def testGetStartTime {
    assertEquals(tue25 at 0.hours, schedule.getStartTime(task32hForDeveloper))
    assertEquals(mon24 at 0.hours, schedule.getStartTime(task40hForDeveloper))
  }

  def testHoursAllocated_forActivity {
    assertEquals(24, schedule.getHoursAllocated(task32hForDeveloper))
    assertEquals(8, schedule.getHoursAllocated(task40hForDeveloper))
  }

  def testHoursAllocated_forResource {
    assertEquals(24, schedule.getHoursAllocated(developerC, default.getPeriod(mon24_0,mon31_0)))
    assertEquals(16, schedule.getHoursAllocated(developerC, default.getPeriod(mon24_0,thu27_0)))
    assertEquals(4, schedule.getHoursAllocated(developerC, default.getPeriod(mon24_4,mon24_8)))
    assertEquals(8, schedule.getHoursAllocated(developerD, mon24_sun06))
    assertEquals(0, schedule.getHoursAllocated(testerA, mon24_sun06))
  }

  def testGetUnallocatedPeriods {
    assertEquals(
      List(default.getPeriod(tue25_0,wed26_0),default.getPeriod(thu27_0,fri28_0),default.getPeriod(mon31_0,sun06_0)),
      schedule.getUnallocatedPeriods(developerC, mon24_sun06)
    )
    assertEquals(
      List(),
      schedule.getUnallocatedPeriods(developerC, default.getPeriod(mon24_0,mon24_8))
    )
    assertEquals(
      List(default.getPeriod(mon24_0,wed26_0),default.getPeriod(thu27_0,sun06_0)),
      schedule.getUnallocatedPeriods(developerD, mon24_sun06)
    )
    assertEquals(
      List(mon24_sun06),
      schedule.getUnallocatedPeriods(testerA, mon24_sun06)
    )
  }

}