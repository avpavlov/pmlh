package core.builder

import core.model.{Resource, Period, Activity}

/**
 * Created by IntelliJ IDEA.
 * User: pav
 * Date: 05.06.2010
 * Time: 2:55:49
 * To change this template use File | Settings | File Templates.
 */

case class Allocation(val activity: Activity, val resource: Resource, val interval: Period)
class Schedule(val allocations:List[Allocation]) {

  def getHoursAllocated(a:Activity) = allocations
          .filter(_.activity == a)
          .map(_.interval.hours)
          .sum

  def getUnallocatedPeriods(r:Resource,p:Period):List[Period] = allocations
          .filter(_.resource == r)
          .map(_.interval)
          .foldLeft(List(p))(
            (unallocated, busy)=>unallocated.flatMap(_.subtraction(busy,r.calendar))
          )
          .filter(_.hours > 0)

  def getHoursAllocated(r:Resource,p:Period) = allocations
          .filter(_.resource == r)
          .map(_.interval.intersection(p,r.calendar).hours)
          .sum

  def getStartTime(a:Activity) = allocations
          .filter(_.activity == a)
          .map(_.interval.start)
          .min
}
