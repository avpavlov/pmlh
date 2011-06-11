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

import core.util.TestingEnvironment
import junit.framework._

import Assert._
import core.model._

class AllocatorTest extends TestCase("Allocator") with TestingEnvironment {

  val planning1 = "planning 1" needs 32.hours of manager where (ShouldFinishBefore(mon24_0))
  val implementation1 = "implementation 1" needs 32.hours of developer where NoConditions
  val ongoingTesting1 = "ongoing testing 1" needs 16.hours of tester where NoConditions
  val finalTesting1 = "final testing 1" needs 16.hours of tester where (CannotBeSharedNorDivided)
  val deployOnUat1 = "deploy on UAT 1" needs 4.hours of developer where (MustStartOn(wed26_0), CannotBeSharedNorDivided)
  val uat1 = "UAT 1" needs 16.hours of analyst where NoConditions
  val deployOnProduction1 = "deploy on Production 1" needs 4.hours of developer where (ShouldStartAfter(fri04_0), CannotBeSharedNorDivided)
  val setupUsers1 = "setup users" needs 16.hours of analyst where NoConditions
  val rebuildIndexes1 = "rebuild indexes" needs 16.hours of developer where NoConditions
  val notifyCustomer1 = "notify customer" needs 16.hours of manager where NoConditions
  val researchMarket1 = "research market" needs 16.hours of analyst where NoConditions
  val iso27Kcertification1 = "ISO 27K certification" needs 16.hours of manager where NoConditions
  val updateNewsOnsite1 = "update news on site" needs 16.hours of manager where (CannotBeSharedNorDivided)

  val a1deps = List(
    MustStartAfter(implementation1, planning1),
    MustStartAfter(ongoingTesting1, planning1),
    JustifyFinishWith(ongoingTesting1, implementation1),
    MustStartAfter(finalTesting1, implementation1, ongoingTesting1),
    MustStartAfter(deployOnUat1, finalTesting1),
    MustStartAfter(uat1, deployOnUat1),
    MustStartAfter(deployOnProduction1, uat1),
    MustStartAfter(setupUsers1, deployOnProduction1),
    MustStartAfter(rebuildIndexes1, deployOnProduction1),
    MustStartAfter(notifyCustomer1, setupUsers1, rebuildIndexes1),
    MustStartAfter(updateNewsOnsite1, iso27Kcertification1)
  )

  val milestoneA1 = new Milestone(
    "1.0.0",
    new Project("Project Alpha"),
    shuffle(List(planning1, implementation1, ongoingTesting1, finalTesting1, deployOnUat1, uat1, deployOnProduction1, setupUsers1, rebuildIndexes1, notifyCustomer1, researchMarket1, iso27Kcertification1, updateNewsOnsite1)),
    List(
      testerA_availability, testerB_availability,
      developerC_availability, developerD_availability, developerE_availability,
      managerF_availability,
      analystG_availability, analystH_availability
    )
  )

  val implementation2 = "implementation 2" needs 32.hours of developer where NoConditions
  val ongoingTesting2 = "ongoing testing 2" needs 16.hours of tester where NoConditions
  val finalTesting2 = "final testing 2" needs 16.hours of tester where (CannotBeSharedNorDivided)
  val deployOnUat2 = "deploy on UAT 2" needs 4.hours of developer where (MustStartOn(wed02_0), CannotBeSharedNorDivided)
  val uat2 = "UAT 2" needs 16.hours of analyst where NoConditions
  val deployOnProduction2 = "deploy on Production 2" needs 4.hours of developer where (ShouldStartAfter(fri04_0), CannotBeSharedNorDivided)

  val a2deps = List(
    MustStartAfter(implementation2, implementation1),
    MustStartAfter(ongoingTesting2, finalTesting1),
    JustifyFinishWith(ongoingTesting2, implementation2),
    MustStartAfter(finalTesting2, implementation2, ongoingTesting2),
    MustStartAfter(deployOnUat2, finalTesting2),
    MustStartAfter(uat2, deployOnUat2),
    MustStartAfter(deployOnProduction2, uat2)
  )


  val milestoneA2 = new Milestone(
    "2.0.0",
    new Project("Project Alpha"),
    shuffle(List(implementation2, ongoingTesting2, finalTesting2, deployOnUat2, uat2, deployOnProduction2)),
    List(testerA_availability, developerC_availability, analystG_availability)
  )

  val implementation3 = "implementation 3" needs 32.hours of developer where NoConditions
  val ongoingTesting3 = "ongoing testing 3" needs 16.hours of tester where NoConditions
  val finalTesting3 = "final testing 3" needs 16.hours of tester where (CannotBeSharedNorDivided)

  val b1deps = List(
    JustifyFinishWith(ongoingTesting3, implementation3),
    MustStartAfter(finalTesting3, implementation3, ongoingTesting3)
  )

  val milestoneB = new Milestone(
    "1.0.0",
    new Project("Project Beta"),
    shuffle(List(implementation3, ongoingTesting3, finalTesting3)),
    List(developerC_availability, developerD_availability, testerA_availability)
  )

  val planA1 = new Plan("Project Alpha", mon24_sun06, List(milestoneA1), a1deps)
  val planB = new Plan("Project Beta", mon24_sun06, List(milestoneB), b1deps)
  val planAB = new Plan("Both projects", mon24_sun06, List(milestoneA1, milestoneA2, milestoneB), a1deps ++ a2deps ++ b1deps)

  def testOrderedActivities() {
    assertEquals(
      List(
        // both have base planning time so placed at beginning
        planning1, deployOnUat1
        // recursive predecessors of activities above. Earlier ones placed before later ones. Less resource demanding placed before more resource demanding.
        , implementation1, ongoingTesting1, finalTesting1
        // recursive successors of activities above. Earlier ones placed before later ones. Less resource demanding placed before more resource demanding.
        , uat1, deployOnProduction1, setupUsers1, rebuildIndexes1, notifyCustomer1
        // entry points to remaining activities. Less resource demanding placed before more resource demanding.
        , iso27Kcertification1, researchMarket1
        // recursive successors of activities above. Less resource demanding placed before more resource demanding.
        , updateNewsOnsite1
      ),
      new Allocator(planA1, Nil).orderedActivities
    )
    assertEquals(
      List(
        // no activities with base planning time and its predecessors/successors
        // ...
        // entry point
        implementation3
        // recursive successors of activities above.
        , ongoingTesting3, finalTesting3
      ),
      new Allocator(planB, Nil).orderedActivities
    )
    assertEquals(
      List(
        // these have base planning time so placed at beginning
        planning1, deployOnUat1, deployOnUat2
        // recursive predecessors of activities above. Earlier ones placed before later ones. Less resource demanding placed before more resource demanding.
        , implementation1, ongoingTesting1, finalTesting1, implementation2, ongoingTesting2, finalTesting2
        // recursive successors of activities above. Earlier ones placed before later ones. Less resource demanding placed before more resource demanding.
        , uat2, uat1, deployOnProduction2, deployOnProduction1, setupUsers1, rebuildIndexes1, notifyCustomer1
        // entry points to remaining activities. Less resource demanding placed before more resource demanding.
        , iso27Kcertification1, researchMarket1, implementation3
        // recursive successors of activities above. Less resource demanding placed before more resource demanding.
        , updateNewsOnsite1, ongoingTesting3, finalTesting3
      ),
      new Allocator(planAB, Nil).orderedActivities
    )
  }


}