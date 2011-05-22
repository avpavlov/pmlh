/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

package core.model

import scala.math._

case class Period(val start: Time, val finish: Time, val hours: Int) extends Ordered[Period] {
  private[Period] def contains(t: Time) = t >= start && t <= finish && hours > 0;
  private[Period] def min(t1: Time, t2: Time) = if (t1 > t2) t2 else t1;

  def intersection(p: Period, c: WorkingCalendar) =
    if (contains(p.start)) c.getPeriod(p.start, min(p.finish, finish))
    else if (p.contains(start)) c.getPeriod(start, min(p.finish, finish))
    else if (start <= p.start) Period(start, start, 0)
    else Period(p.start, p.start, 0)

  def subtraction(p: Period, c: WorkingCalendar) =
    if (start >= p.finish || finish <= p.start)
      List(this)
    else (signum(start.compareTo(p.start)), signum(finish.compareTo(p.finish))) match {
      case (-1, 1) => List(c.getPeriod(start, p.start), c.getPeriod(p.finish, finish))
      case (-1, _) => List(c.getPeriod(start, p.start))
      case ( _, 1) => List(c.getPeriod(p.finish, finish))
      case _ => List()
    }

  override def compare(that: Period) = start.compareTo(that.start) match {
    case 0 => hours.compare(that.hours) * -1 // longer period should be placed at beginning
    case r => r
  }
}


