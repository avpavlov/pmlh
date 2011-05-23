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
