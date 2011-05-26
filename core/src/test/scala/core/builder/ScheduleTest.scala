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

package core.builder

import junit.framework.TestCase
import core.util.TestingEnvironment
import junit.framework._

import Assert._
import core.model._

class ScheduleTest extends TestCase("Schedule") with TestingEnvironment {

  val task32hForDeveloper = "task 1" needs 32.hours of developer where NoConditions
  val task40hForDeveloper = "task 2" needs 40.hours of developer where NoConditions

  val schedule = new Schedule(List(
      developerC implements task32hForDeveloper from (fri28 at 0.hours) during 8.hours,
      developerC implements task40hForDeveloper from (mon24 at 0.hours) during 8.hours,
      developerC implements task32hForDeveloper from (wed26 at 0.hours) during 8.hours,
      developerD implements task32hForDeveloper from (wed26 at 0.hours) during 8.hours
    ))

  def testGetStartTime {
    assertEquals(wed26 at 0.hours, schedule.getStartTime(task32hForDeveloper))
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