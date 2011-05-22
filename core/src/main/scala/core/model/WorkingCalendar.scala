/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

package core.model

import java.util.{Calendar, Date}
import java.util.Calendar._
import annotation.tailrec

class WorkingCalendar(val base: WorkingCalendar = null,
                      val hoursPerDay: Int = 8,
                      val holidays: List[Date] = Nil,
                      val workingDays: List[Date] = Nil,
                      val workingDaysOfWeek: List[Int] = List(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)
        ) {
  implicit def date2Calendar(date: Date) = {
    val c = Calendar.getInstance
    c.setTime(date)
    c
  }

  def isWorkingDay(date: Date): Boolean = {
    if (holidays.contains(date)) false
    else if (workingDays.contains(date)) true
    else if (base != null && base.isWorkingDay(date)) true
    else workingDaysOfWeek.contains(date.get(DAY_OF_WEEK))
  }

  def getNextWorkingDay(date: Date): Date = getNextWorkingDay(date, 24 * 60 * 60 * 1000)

  def getPreviousWorkingDay(date: Date): Date = getNextWorkingDay(date, -24 * 60 * 60 * 1000)

  @tailrec private[this] def getNextWorkingDay(date: Date, delta: Int): Date = new Date(date.getTime + delta) match {
    case d if isWorkingDay(d) => d
    case d => getNextWorkingDay(d, delta)
  }

  def getPeriod(start:Time, finish:Time):Period = {
    val (expectedPeriodStart,expectedPeriodEnd) = if (start <= finish) (start,finish) else (finish,start)

    val canWorkAtFirstDay = isWorkingDay(expectedPeriodStart.day) && expectedPeriodStart.hour < hoursPerDay
    val canWorkAtLastDay = isWorkingDay(expectedPeriodEnd.day) && expectedPeriodEnd.hour > 0
    val periodStart = if (canWorkAtFirstDay) expectedPeriodStart else Time(getNextWorkingDay(expectedPeriodStart.day), 0)
    val periodEnd = if (canWorkAtLastDay) expectedPeriodEnd else Time(getPreviousWorkingDay(expectedPeriodEnd.day), hoursPerDay)

    @tailrec def findPeriodLength(current: Time, hours: Int): Int = current.day.compareTo(periodEnd.day) match {
      case cmpResult if cmpResult > 0 => hours
      case cmpResult if cmpResult == 0 => hours + (periodEnd.hour-current.hour)
      case _ => findPeriodLength(Time(getNextWorkingDay(current.day), 0), hours + (hoursPerDay-current.hour))
    }

    if (periodStart >= periodEnd)
      Period(periodStart, periodStart, 0)
    else
      Period(periodStart, periodEnd, findPeriodLength(periodStart,0))
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






















