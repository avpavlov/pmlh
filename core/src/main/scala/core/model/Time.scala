package core.model

import java.util.Date
import annotation.tailrec

/**
 * Created by IntelliJ IDEA.
 * User: pav
 * Date: 03.06.2010
 * Time: 0:24:38
 * To change this template use File | Settings | File Templates.
 */

case class Time(val day:Date,val hour:Int) extends Ordered[Time] {
  override def compare(that: Time) = day.compareTo(that.day) match {
    case 0 => hour - that.hour
    case r => r
  }

}

