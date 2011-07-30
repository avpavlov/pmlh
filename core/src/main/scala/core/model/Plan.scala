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

import annotation.tailrec

class Project(val name: String)

class Milestone(val name: String,
                val project: Project,
                val activities: List[Activity],
                val availableResources: List[AvailableResource])

class Plan(val name: String, val period: Period, val milestones: List[Milestone], val dependencies: List[Dependency] = Nil) {

  val activities = milestones.flatMap(_.activities)

  val availableResources = milestones.flatMap(m => m.activities.map(a => a -> m.availableResources.filter(_.resource.resourceType == a.resourceType))).toMap

  val resources = availableResources.map {
    case (a, ar) => a -> ar.map(_.resource).distinct
  }

  val predecessors = {
    def findDeps(a: Activity) = dependencies.filter(_.activity == a).flatMap(_.dependsOn).distinct
    activities.map(activity => (activity -> findDeps(activity))).toMap
  }

  val successors = {
    def findDeps(a: Activity) = activities.filter(predecessors(_).contains(a)).distinct
    activities.map(activity => (activity -> findDeps(activity))).toMap
  }

  val mustStartAfter = {
    def findDeps(a: Activity) = dependencies.filter(_.activity == a).filter(_.isInstanceOf[MustStartAfter]).flatMap(_.dependsOn).distinct
    activities.map(activity => (activity -> findDeps(activity))).toMap
  }

  val justifyFinishWith = {
    def findDeps(a: Activity) = dependencies.filter(_.activity == a).filter(_.isInstanceOf[JustifyFinishWith]).flatMap(_.dependsOn).distinct
    activities.map(activity => (activity -> findDeps(activity))).toMap
  }

  val (allLevelPredecessors, allLevelSuccessors) = {
    @tailrec def findDeps(immediateDepsMap: Map[Activity, List[Activity]], nextPortion: List[Activity], accumulated: List[Activity]): List[Activity] = {
      nextPortion.flatMap(immediateDepsMap(_)).filterNot(accumulated.contains) match {
        case empty if empty.isEmpty => accumulated
        case immediateDependencies => findDeps(immediateDepsMap, immediateDependencies, accumulated ++ immediateDependencies)
      }
    }
    def deps(activity: Activity, immediateDepsMap: Map[Activity, List[Activity]]) = findDeps(immediateDepsMap, immediateDepsMap(activity), immediateDepsMap(activity))

    (
      activities.map(activity => (activity -> deps(activity, predecessors).distinct)).toMap,
      activities.map(activity => (activity -> deps(activity, successors).distinct)).toMap
      )
  }

  val zeroLength = activities.filter(_.hours == 0)

  val wrongMustStartOn = activities
    .filter(_.mustStartOn.isDefined)
    .filter(_.shouldFinishBefore.isDefined)
    .filter(a => a.mustStartOn >= a.shouldFinishBefore)

  val wrongShouldFinishBefore = activities
    .filter(_.shouldStartAfter.isDefined)
    .filter(_.shouldFinishBefore.isDefined)
    .filter(a => a.shouldStartAfter >= a.shouldFinishBefore)

  val wrongShouldStartAfter = activities
    .filter(_.shouldStartAfter.isDefined)
    .filter(_.mustStartOn.isDefined)
    .filter(a => a.shouldStartAfter > a.mustStartOn)

  val noResources = activities.filter(resources(_).isEmpty)

  val wrongPreferredResources = activities.filterNot(a => a.preferredResources.forall(resources(a).contains))

  /*
    incompatible: a.MustStartAfter(b), b.MustStartAfter(a)
    incompatible: a.MustStartAfter(b), b.JustifyFinishWith(a)
    compatible: a.JustifyFinishWith(b), b.JustifyFinishWith(a)
  */
  val circularDependencies = activities.filter(a => mustStartAfter(a).exists(allLevelPredecessors(_).contains(a)))

  val valid = (
    zeroLength.isEmpty
      && wrongMustStartOn.isEmpty && wrongShouldFinishBefore.isEmpty && wrongShouldStartAfter.isEmpty
      && noResources.isEmpty && wrongPreferredResources.isEmpty && circularDependencies.isEmpty
    )
}

