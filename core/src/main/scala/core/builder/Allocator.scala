/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

package core.builder

import java.util.{Calendar, Date}
import annotation.tailrec
import core.model._

class Allocator(plan: Plan, allocations:List[Allocation]) {

  private[this] def sequences(planned:Set[Allocation], hoursRemain:Int, available:List[Allocation]):List[Set[Allocation]]
    = available.flatMap(a =>
        if (a.interval.hours >= hoursRemain )
          List(planned + a.copy(interval=a.resource.calendar.getPeriod(a.interval.finish, -hoursRemain)))
        else
          sequences(planned + a, hoursRemain - a.interval.hours, available.filter(_!=a))
      )

  def scheduleActivity(schedule:Schedule, activity:Activity, expectedFinishTime:Time):Seq[Schedule] = {
    // get all allowed allocations for all available resources
    val availableResources = plan.resourcesMap(activity)
    val allowedAllocations = (for (ar <- availableResources) yield {
      val r = ar.resource
      val c = r.calendar
      val maxAvailablePeriod = ar.period.intersection(c.getPeriod(plan.start, expectedFinishTime), c)
      val allowedAllocations = schedule
              .getUnallocatedPeriods(r, maxAvailablePeriod)
              .map(Allocation(activity, r, _))
      allowedAllocations
    }).flatten.toList

    // get remaining time
    val hoursRemain = activity.hours - schedule.getHoursAllocated(activity)

    // generate all possible sequences which fit deadline
    val s = sequences(Set(), hoursRemain, allowedAllocations)

    // create separate schedule for each sequence
    s.map(allocations => new Schedule(schedule.allocations ++ allocations))
  }
}





















