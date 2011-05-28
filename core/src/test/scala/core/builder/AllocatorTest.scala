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

  val planning1 = "planning" needs 32.hours of manager where (ShouldFinishBefore(wed26_0))
  val implementation1 = "implementation" needs 32.hours of developer where (MustStartAfter(planning1))
  val ongoingTesting1 = "ongoing testing" needs 16.hours of tester where (MustStartAfter(planning1), JustifyFinishWith(implementation1))
  val finalTesting1 = "final testing" needs 16.hours of tester where (MustStartAfter(implementation1), MustStartAfter(ongoingTesting1), CannotBeShared)
  val deployOnUat1 = "deploy on UAT" needs 4.hours of developer where (MustStartOn(wed02_0), MustStartAfter(finalTesting1), CannotBeShared)
  val uat1 = "UAT" needs 16.hours of analyst where (MustStartAfter(deployOnUat1))
  val deployOnProduction1 = "deploy on Production" needs 4.hours of developer where (ShouldStartAfter(fri04_0), MustStartAfter(uat1), CannotBeShared)
  val setupUsers1 = "setup users" needs 16.hours of analyst where (MustStartAfter(deployOnProduction1))
  val rebuildIndexes1 = "rebuild indexes" needs 16.hours of developer where (MustStartAfter(deployOnProduction1))
  val notifyCustomer1 = "notify customer" needs 16.hours of manager where (MustStartAfter(setupUsers1), MustStartAfter(rebuildIndexes1))
  val researchMarket1 = "research market" needs 16.hours of analyst where (NoConditions)

  val milestoneA1 = new Milestone(
    "1.0.0",
    new Project("Project Alpha"),
    shuffle(List(planning1, implementation1, ongoingTesting1, finalTesting1, deployOnUat1, uat1, deployOnProduction1, setupUsers1, rebuildIndexes1, notifyCustomer1, researchMarket1)),
    List(developerC_availability, developerD_availability, testerA_availability)
  )

  val implementation2 = "implementation" needs 32.hours of developer where (MustStartAfter(implementation1))
  val ongoingTesting2 = "ongoing testing" needs 16.hours of tester where (MustStartAfter(finalTesting1), JustifyFinishWith(implementation2))
  val finalTesting2 = "final testing" needs 16.hours of tester where (MustStartAfter(implementation2), MustStartAfter(ongoingTesting2), CannotBeShared)
  val deployOnUat2 = "deploy on UAT" needs 4.hours of developer where (MustStartOn(wed02_0), MustStartAfter(finalTesting2), CannotBeShared)
  val uat2 = "UAT" needs 16.hours of analyst where (MustStartAfter(deployOnUat2))
  val deployOnProduction2 = "deploy on Production" needs 4.hours of developer where (ShouldStartAfter(fri04_0), MustStartAfter(uat2), CannotBeShared)

  val milestoneA2 = new Milestone(
    "2.0.0",
    new Project("Project Alpha"),
    shuffle(List(implementation1, ongoingTesting2, finalTesting2, deployOnUat2, uat2, deployOnProduction2)),
    List(developerC_availability, developerD_availability, testerA_availability)
  )

  val implementation3 = "implementation" needs 32.hours of developer where (NoConditions)
  val ongoingTesting3 = "ongoing testing" needs 16.hours of tester where (JustifyFinishWith(implementation3))
  val finalTesting3 = "final testing" needs 16.hours of tester where (MustStartAfter(implementation3), MustStartAfter(ongoingTesting3), CannotBeShared)

  val milestoneB = new Milestone(
    "1.0.0",
    new Project("Project Beta"),
    shuffle(List(implementation3, ongoingTesting3, finalTesting3)),
    List(developerC_availability, developerD_availability, testerA_availability)
  )

  val planA1 = new Plan("Project Alpha, 1.0.0", mon24_sun06, List(milestoneA1))
  val planA2 = new Plan("Project Alpha", mon24_sun06, List(milestoneA1, milestoneA2))
  val planB = new Plan("Project Beta", mon24_sun06, List(milestoneB))
  val planAB = new Plan("Both projects", mon24_sun06, List(milestoneA1, milestoneA2, milestoneB))

  def testOrderedActivities() {
    // 1. MustStartOn or ShouldFinishBefore
    // 2. Recursive predecessors. Each portion sorted by available resources count
    // 3. Recursive successors. Each portion sorted by available resources count
    // 4. Remainder sorted by available resources count
    assertEquals(
      List(implementation3, ongoingTesting3, finalTesting3),
      new Allocator(planB, Nil).orderedActivities
    )
  }


}