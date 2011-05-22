/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

package core.builder

import core.model.{Resource, Period, Activity}

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
