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

import java.util.{Calendar, Date}
import java.util.Calendar._
import annotation.tailrec

abstract sealed class DayOfWeek(val index: Int)

case object Sun extends DayOfWeek(SUNDAY)

case object Mon extends DayOfWeek(MONDAY)

case object Tue extends DayOfWeek(TUESDAY)

case object Wed extends DayOfWeek(WEDNESDAY)

case object Thu extends DayOfWeek(THURSDAY)

case object Fri extends DayOfWeek(FRIDAY)

case object Sat extends DayOfWeek(SATURDAY)

object DayOfWeek {
  implicit def date2Calendar(date: Date) = {
    val c = Calendar.getInstance; c.setTime(date); c
  }

  def apply(date: Date) = mapping(date.get(DAY_OF_WEEK))

  private[this] val mapping = List(Mon, Tue, Wed, Thu, Fri, Sat, Sun).map(dayOfWeek => dayOfWeek.index -> dayOfWeek).toMap
}

class WorkingCalendar(val parentCalendar: WorkingCalendar = null,
                      val workingDaysOfWeek: List[DayOfWeek] = Nil,
                      val hoursPerDay: Int = 8,
                      val holidays: List[Date] = Nil,
                      val workingDays: List[Date] = Nil
                       ) {
  require(parentCalendar == null || workingDaysOfWeek.isEmpty, "Parent calendar and working days of week are mutually exclusive.")

  def isWorkingDay(date: Date): Boolean = {
    if (holidays.contains(date)) false
    else if (workingDays.contains(date)) true
    else if (parentCalendar != null) parentCalendar.isWorkingDay(date)
    else workingDaysOfWeek.contains(DayOfWeek(date))
  }

  def getNextWorkingDay(date: Date): Date = getNextWorkingDay(date, 24 * 60 * 60 * 1000)

  def getPreviousWorkingDay(date: Date): Date = getNextWorkingDay(date, -24 * 60 * 60 * 1000)

  @tailrec private[this] def getNextWorkingDay(date: Date, delta: Int): Date = new Date(date.getTime + delta) match {
    case d if isWorkingDay(d) => d
    case d => getNextWorkingDay(d, delta)
  }

  def getPeriod(start: Time, finish: Time): Period = {
    val (expectedPeriodStart, expectedPeriodEnd) = if (start <= finish) (start, finish) else (finish, start)

    val canWorkAtFirstDay = isWorkingDay(expectedPeriodStart.day) && expectedPeriodStart.hour < hoursPerDay
    val canWorkAtLastDay = isWorkingDay(expectedPeriodEnd.day) && expectedPeriodEnd.hour > 0
    val periodStart = if (canWorkAtFirstDay) expectedPeriodStart else Time(getNextWorkingDay(expectedPeriodStart.day), 0)
    val periodEnd = if (canWorkAtLastDay) expectedPeriodEnd else Time(getPreviousWorkingDay(expectedPeriodEnd.day), hoursPerDay)

    @tailrec def findPeriodLength(current: Time, hours: Int): Int = current.day.compareTo(periodEnd.day) match {
      case cmpResult if cmpResult > 0 => hours
      case cmpResult if cmpResult == 0 => hours + (periodEnd.hour - current.hour)
      case _ => findPeriodLength(Time(getNextWorkingDay(current.day), 0), hours + (hoursPerDay - current.hour))
    }

    if (periodStart >= periodEnd)
      Period(periodStart, periodStart, 0)
    else
      Period(periodStart, periodEnd, findPeriodLength(periodStart, 0))
  }

  def getPeriod(start: Time, hours: Int): Period = {
    val forward = hours >= 0

    def moveToNextWorkingDay(current: Time) = forward match {
      case true => Time(getNextWorkingDay(current.day), 0)
      case false => Time(getPreviousWorkingDay(current.day), hoursPerDay)
    }

    @tailrec def findPeriodEnd(current: Time, hours: Int): Time = {
      val lastHour = current.hour + hours
      val isLastDay = if (forward) lastHour <= hoursPerDay else lastHour >= 0
      if (isLastDay) {
        Time(current.day, lastHour)
      } else {
        val hoursRemain = if (forward) lastHour - hoursPerDay else lastHour
        findPeriodEnd(moveToNextWorkingDay(current), hoursRemain)
      }
    }

    val canWorkAtFirstDay = isWorkingDay(start.day) match {
      case false => false
      case _ if forward => hoursPerDay > start.hour
      case _ => start.hour > 0
    }
    val periodStart = if (canWorkAtFirstDay) start else moveToNextWorkingDay(start)
    val periodEnd = findPeriodEnd(periodStart, hours)

    if (forward)
      Period(periodStart, periodEnd, hours)
    else
      Period(periodEnd, periodStart, -hours)
  }

}

object WorkingCalendar {
  val StandardWorkingWeek: List[DayOfWeek] = List(Mon, Tue, Wed, Thu, Fri)
}