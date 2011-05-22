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

  val a1 = new Activity("a1", 32, developer, Nil)
  val a2 = new Activity("a2", 32, developer, Nil)
  val schedule = new Schedule(List(
      new Allocation(a1, developerC, default.getPeriod(fri28_0,mon31_0)),
      new Allocation(a2, developerC, default.getPeriod(mon24_0,tue25_0)),
      new Allocation(a1, developerC, default.getPeriod(tue25_0,wed26_0)),
      new Allocation(a1, developerD, default.getPeriod(wed26_0,thu27_0))
    ))

  def testGetStartTime {
    assertEquals(tue25_0, schedule.getStartTime(a1))
    assertEquals(mon24_0, schedule.getStartTime(a2))
  }

  def testHoursAllocated_forActivity {
    assertEquals(24, schedule.getHoursAllocated(a1))
    assertEquals(8, schedule.getHoursAllocated(a2))
  }

  def testHoursAllocated_forResource {
    assertEquals(24, schedule.getHoursAllocated(developerC, default.getPeriod(mon24_0,mon31_0)))
    assertEquals(16, schedule.getHoursAllocated(developerC, default.getPeriod(mon24_0,wed26_0)))
    assertEquals(4, schedule.getHoursAllocated(developerC, default.getPeriod(mon24_4,mon24_8)))
    assertEquals(8, schedule.getHoursAllocated(developerD, mon24_sun06))
    assertEquals(0, schedule.getHoursAllocated(testerA, mon24_sun06))
  }

  def testGetUnallocatedPeriods {
    fail("to be implemented")
  }

}