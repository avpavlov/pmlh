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


