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

package core.util

import java.text.SimpleDateFormat
import java.util.Calendar._
import core.model._
import java.util.Date
import core.builder.Allocation

trait TestingEnvironment {
  implicit def wrapString(format: String) = new SimpleDateFormat(format)

  implicit def wrapHours(h: Int) = new {
    def hours = h
  }

  implicit def wrapDate(date: Date) = new {
    def at(hours: Int) = Time(date, hours)
  }

  implicit def wrapActivity(name: String) = new {
    def needs(h: Int) = new {
      def of(resourceType: ResourceType) = new {
        def where(conditions: Condition*) = new Activity(name, h, resourceType, conditions.toList)

        def where(conditions: Array[Condition]) = new Activity(name, h, resourceType, conditions.toList)
      }
    }
  }

  val NoConditions: Array[Condition] = Array()

  implicit def wrapResource(resource: Resource) = new {
    def implements(a: Activity) = new {
      def from(t: Time) = new {
        def during(h: Int) = new Allocation(a, resource, default.getPeriod(t, h))
      }
    }
  }

  val mon24 = "dd/MM/yyyy".parse("24/05/2010")
  val tue25 = "dd/MM/yyyy".parse("25/05/2010")
  val wed26 = "dd/MM/yyyy".parse("26/05/2010")
  val thu27 = "dd/MM/yyyy".parse("27/05/2010")
  val fri28 = "dd/MM/yyyy".parse("28/05/2010")
  val sat29 = "dd/MM/yyyy".parse("29/05/2010")
  val sun30 = "dd/MM/yyyy".parse("30/05/2010")
  val mon31 = "dd/MM/yyyy".parse("31/05/2010")
  val tue01 = "dd/MM/yyyy".parse("01/06/2010")
  val wed02 = "dd/MM/yyyy".parse("02/06/2010")
  val thu03 = "dd/MM/yyyy".parse("03/06/2010")
  val fri04 = "dd/MM/yyyy".parse("04/06/2010")
  val sat05 = "dd/MM/yyyy".parse("05/06/2010")
  val sun06 = "dd/MM/yyyy".parse("06/06/2010")

  val mon24_0 = Time(mon24, 0)
  val mon24_4 = Time(mon24, 4)
  val mon24_8 = Time(mon24, 8)
  val tue25_0 = Time(tue25, 0)
  val wed26_0 = Time(wed26, 0)
  val thu27_0 = Time(thu27, 0)
  val thu27_8 = Time(thu27, 8)
  val fri28_0 = Time(fri28, 0)
  val fri28_4 = Time(fri28, 4)
  val fri28_5 = Time(fri28, 5)
  val fri28_8 = Time(fri28, 8)
  val sat29_0 = Time(sat29, 0)
  val sat29_4 = Time(sat29, 4)
  val sat29_8 = Time(sat29, 8)
  val mon31_0 = Time(mon31, 0)
  val mon31_4 = Time(mon31, 4)
  val mon31_8 = Time(mon31, 8)
  val tue01_0 = Time(tue01, 0)
  val tue01_4 = Time(tue01, 4)
  val tue01_8 = Time(tue01, 8)
  val wed02_0 = Time(wed02, 0)
  val fri04_0 = Time(fri04, 0)
  val sun06_0 = Time(sun06, 0)

  // default calendar
  val default = new WorkingCalendar

  // global calendar
  // MON 31/05/2010 and TUE 01/06/2010 - forced holidays
  // FRI 28/05/2010 and SAT 29/05/2010 - forced working days
  // FRI-SUN - not working days
  val global = new WorkingCalendar(null, 8, List(mon31, tue01), List(fri28, sat29), List(MONDAY, TUESDAY, WEDNESDAY, THURSDAY))

  // personal calendar based on global
  // FRI 28/05/2010 - forced holidays (ignores global calendar)
  // SUN 30/05/2010 - forced working day
  // week days ARE NOT IMPORTANT, IT USES GLOBAL
  val personal = new WorkingCalendar(global, 8, List(fri28), List(sun30), Nil)

  val developer = new ResourceType("developer")
  val tester = new ResourceType("tester")
  val manager = new ResourceType("manager")
  val analyst = new ResourceType("analyst")

  var testerA = Resource("A", tester, default)
  var testerB = Resource("B", tester, default)
  var developerC = Resource("C", developer, default)
  var developerD = Resource("D", developer, default)
  var developerE = Resource("E", developer, default)
  var managerF = Resource("F", manager, default)
  var analystG = Resource("G", analyst, default)
  var analystH = Resource("H", analyst, default)

  var mon24_sun06 = default.getPeriod(mon24_0, sun06_0)

  val testerA_availability = AvailableResource(testerA, mon24_sun06)
  val testerB_availability = AvailableResource(testerB, mon24_sun06)
  val developerC_availability = AvailableResource(developerC, mon24_sun06)
  val developerD_availability = AvailableResource(developerD, mon24_sun06)
  val developerE_availability = AvailableResource(developerE, mon24_sun06)
  val managerF_availability = AvailableResource(managerF, mon24_sun06)
  val analystG_availability = AvailableResource(analystG, mon24_sun06)
  val analystH_availability = AvailableResource(analystH, mon24_sun06)

  def shuffle[T](list: List[T]) = list.map((_, scala.math.random)).sortWith((d1, d2) => d1._2 > d2._2).map(_._1)

}