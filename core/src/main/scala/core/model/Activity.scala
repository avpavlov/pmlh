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

class Activity(val name:String,
               val hours:Int,
               val resourceType:ResourceType,
               val conditions:List[Condition]
              ) extends Ordered[Activity] {

  val predecessors = conditions.filter(_.isInstanceOf[DependsOn]).map(_.asInstanceOf[DependsOn].activity).toSet

  val paths:Set[List[Activity]] = predecessors.flatMap(predecessor=>predecessor.paths match {
    case paths if paths.isEmpty => Set(List(predecessor))
    case paths => paths.map(_:::List(predecessor))
  })

  def successors(activities:List[Activity]) = activities.filter(_.predecessors.contains(this)).toSet

  val cannotBeShared = !conditions.find(_.isInstanceOf[CannotBeShared]).isEmpty
  
  val expectedStartTime = conditions
          .filter(_.isInstanceOf[MustStartOn])
          .map(_.asInstanceOf[MustStartOn].time) match {
            case List() => None
            case notEmptyList => Some(notEmptyList.min)
          }

  override def compare(that: Activity) = (expectedStartTime,that.expectedStartTime) match {
    case (None,None) => 0
    case (None, _) => 1 // if no date then place at end
    case (_, None) => -1
    case (Some(x),Some(y)) => x.compare(y)
  }
}
