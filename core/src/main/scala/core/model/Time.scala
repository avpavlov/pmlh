/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

package core.model

import java.util.Date
import annotation.tailrec

case class Time(val day:Date,val hour:Int) extends Ordered[Time] {
  override def compare(that: Time) = day.compareTo(that.day) match {
    case 0 => hour - that.hour
    case r => r
  }

}

