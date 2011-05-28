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

package core.builder

import annotation.tailrec
import core.model._

class Allocator(plan: Plan, allocations: List[Allocation]) {
  val orderedActivities = {

    def resourcesCount(a: Activity) = if (a.cannotBeShared) 1 else plan.resources(a).map(_.resource).distinct.size

    @tailrec def dependencies(predecessors: Boolean, nextPortion: List[Activity], accumulated: List[Activity]): List[Activity] = {
      val immediateDeps = if (predecessors) plan.predecessors else plan.successors
      val reverseDeps = if (predecessors) plan.successors else plan.predecessors
      nextPortion.flatMap(immediateDeps(_)).filterNot(accumulated.contains).sortBy(resourcesCount) match {
        case List() => accumulated
        case immediateDependencies => {
          val firstLevelOnly = immediateDependencies.filter(reverseDeps(_).find(immediateDependencies.contains).isEmpty) match {
            case List() => immediateDependencies // circular references, return as is
            case noDependant => noDependant
          }
          val result = if (predecessors) firstLevelOnly ++ accumulated else accumulated ++ firstLevelOnly
          dependencies(predecessors, firstLevelOnly, result)
        }
      }
    }

    def predecessors(entryPoints: List[Activity]) = dependencies(true, entryPoints, Nil).filterNot(entryPoints.contains)

    def successors(entryPoints: List[Activity]) = dependencies(false, entryPoints, Nil).filterNot(entryPoints.contains)

    val bpt = plan.activities.filter(_.basePlanningTime.isDefined).sortBy(_.basePlanningTime.get)
    val bptAndPredecessors = bpt ++ predecessors(bpt)
    val bptAndPredecessorsAndSuccessors = bptAndPredecessors ++ successors(bptAndPredecessors)
    val remainderEntryPoints = plan.activities
      .filterNot(bptAndPredecessorsAndSuccessors.contains) // remainder
      .filter(plan.predecessors(_).isEmpty) // entry point
      .sortBy(resourcesCount)
    val orderedRemainder = remainderEntryPoints ++ successors(remainderEntryPoints)

    bptAndPredecessorsAndSuccessors ++ orderedRemainder
  }

  /*
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
  */
}





















