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

import junit.framework.TestCase
import junit.framework.Assert._
import core.util.TestingEnvironment

class PlanTest extends TestCase("Plan") with TestingEnvironment {
  def testDependencies {

    val implementation = new Activity("implementation", 32, developer, Nil)
    val ongoingTesting = new Activity("ongoing testing", 16, tester, List(JustifyFinishWith(implementation)))
    val finalTesting = new Activity("final testing", 16, tester, List(MustStartAfter(implementation), MustStartAfter(ongoingTesting), CannotBeSharedNorDivided))
    val deployOnUat = new Activity("deploy on UAT", 4, developer, List(MustStartOn(wed02_0), MustStartAfter(finalTesting), CannotBeSharedNorDivided))
    val deployOnProduction = new Activity("deploy on production", 4, developer, List(MustStartOn(fri04_0), MustStartAfter(deployOnUat), CannotBeSharedNorDivided))

    val milestone1 = new Milestone(
      "1.0.0",
      null,
      List(implementation, ongoingTesting, finalTesting, deployOnUat, deployOnProduction),
      List(developerC_availability, developerD_availability, testerA_availability)
    )

    val loadTesting = new Activity("load testing", 8, tester, List(MustStartAfter(finalTesting), CannotBeSharedNorDivided))

    val milestone2 = new Milestone(
      "performance analysis",
      null,
      List(loadTesting),
      List(testerB_availability)
    )

    val plan = new Plan("2 weeks", mon24_sun06, List(milestone1, milestone2))

    assertEquals(
      List(
        implementation, ongoingTesting, finalTesting, deployOnUat, deployOnProduction, loadTesting
      )
      , plan.activities
    )
    assertEquals(
      Map(
        implementation -> List(developerC_availability, developerD_availability),
        ongoingTesting -> List(testerA_availability),
        finalTesting -> List(testerA_availability),
        deployOnUat -> List(developerC_availability, developerD_availability),
        deployOnProduction -> List(developerC_availability, developerD_availability),
        loadTesting -> List(testerB_availability)
      )
      , plan.availableResources
    )
    assertEquals(
      Map(
        implementation -> Set(developerC, developerD),
        ongoingTesting -> Set(testerA),
        finalTesting -> Set(testerA),
        deployOnUat -> Set(developerC, developerD),
        deployOnProduction -> Set(developerC, developerD),
        loadTesting -> Set(testerB)
      )
      , plan.resources
    )
    assertEquals(
      Map(
        implementation -> Set(),
        ongoingTesting -> Set(implementation),
        finalTesting -> Set(ongoingTesting, implementation),
        deployOnUat -> Set(finalTesting),
        deployOnProduction -> Set(deployOnUat),
        loadTesting -> Set(finalTesting)
      )
      , plan.predecessors
    )
    assertEquals(
      Map(
        implementation -> Set(ongoingTesting, finalTesting),
        ongoingTesting -> Set(finalTesting),
        finalTesting -> Set(deployOnUat, loadTesting),
        deployOnUat -> Set(deployOnProduction),
        deployOnProduction -> Set(),
        loadTesting -> Set()
      )
      , plan.successors
    )
    assertEquals(
      Map(
        implementation -> Set(),
        ongoingTesting -> Set(implementation),
        finalTesting -> Set(ongoingTesting, implementation),
        deployOnUat -> Set(finalTesting, ongoingTesting, implementation),
        deployOnProduction -> Set(deployOnUat, finalTesting, ongoingTesting, implementation),
        loadTesting -> Set(finalTesting, ongoingTesting, implementation)
      )
      , plan.allLevelPredecessors
    )
    assertEquals(
      Map(
        implementation -> Set(ongoingTesting, finalTesting, deployOnUat, loadTesting, deployOnProduction),
        ongoingTesting -> Set(finalTesting, deployOnUat, loadTesting, deployOnProduction),
        finalTesting -> Set(deployOnUat, loadTesting, deployOnProduction),
        deployOnUat -> Set(deployOnProduction),
        deployOnProduction -> Set(),
        loadTesting -> Set()
      )
      , plan.allLevelSuccessors
    )
  }

  def testValidation_wrongMustStartOn {
    val noConditions = "a1" needs 8.hours of tester where NoConditions
    val mustStartOn_only = "a2" needs 8.hours of tester where (MustStartOn(wed02_0))
    val shouldFinishBefore_only = "a3" needs 8.hours of tester where (ShouldFinishBefore(wed02_0))
    val correct = "a5" needs 8.hours of tester where (MustStartOn(tue01_0), ShouldFinishBefore(wed02_0))
    val incorrect1 = "a4" needs 8.hours of tester where (MustStartOn(wed02_0), ShouldFinishBefore(wed02_0))
    val incorrect2 = "a6" needs 8.hours of tester where (MustStartOn(wed02_0), ShouldFinishBefore(tue01_0))

    val milestone = new Milestone(
      "m",
      null,
      List(noConditions, mustStartOn_only, shouldFinishBefore_only, correct, incorrect1, incorrect2),
      List(testerB_availability)
    )

    val plan = new Plan("2 weeks", mon24_sun06, List(milestone))

    assertEquals(List(incorrect1, incorrect2), plan.wrongMustStartOn)

  }

  def testValidation_wrongShouldFinishBefore {
    val noConditions = "a1" needs 8.hours of tester where NoConditions
    val shouldStartAfter_only = "a2" needs 8.hours of tester where (ShouldStartAfter(wed02_0))
    val shouldFinishBefore_only = "a3" needs 8.hours of tester where (ShouldFinishBefore(wed02_0))
    val correct = "a5" needs 8.hours of tester where (ShouldStartAfter(tue01_0), ShouldFinishBefore(wed02_0))
    val incorrect1 = "a4" needs 8.hours of tester where (ShouldStartAfter(wed02_0), ShouldFinishBefore(wed02_0))
    val incorrect2 = "a6" needs 8.hours of tester where (ShouldStartAfter(wed02_0), ShouldFinishBefore(tue01_0))

    val milestone = new Milestone(
      "m",
      null,
      List(noConditions, shouldStartAfter_only, shouldFinishBefore_only, correct, incorrect1, incorrect2),
      List(testerB_availability)
    )

    val plan = new Plan("2 weeks", mon24_sun06, List(milestone))

    assertEquals(List(incorrect1, incorrect2), plan.wrongShouldFinishBefore)
  }

  def testValidation_wrongShouldStartAfter {
    val noConditions = "a1" needs 8.hours of tester where NoConditions
    val shouldStartAfter_only = "a2" needs 8.hours of tester where (ShouldStartAfter(wed02_0))
    val mustStartOn_only = "a3" needs 8.hours of tester where (MustStartOn(wed02_0))
    val correct1 = "a5" needs 8.hours of tester where (ShouldStartAfter(tue01_0), MustStartOn(wed02_0))
    val correct2 = "a4" needs 8.hours of tester where (ShouldStartAfter(wed02_0), MustStartOn(wed02_0))
    val incorrect = "a6" needs 8.hours of tester where (ShouldStartAfter(wed02_0), MustStartOn(tue01_0))

    val milestone = new Milestone(
      "m",
      null,
      List(noConditions, shouldStartAfter_only, mustStartOn_only, correct1, correct2, incorrect),
      List(testerB_availability)
    )

    val plan = new Plan("2 weeks", mon24_sun06, List(milestone))

    assertEquals(List(incorrect), plan.wrongShouldStartAfter)
  }

  def testValidation_zeroLength {
    val correct = "a1" needs 8.hours of tester where NoConditions
    val incorrect = "a2" needs 0.hours of developer where NoConditions

    val milestone = new Milestone(
      "m",
      null,
      List(correct, incorrect),
      List(testerA_availability, developerD_availability)
    )

    val plan = new Plan("2 weeks", mon24_sun06, List(milestone))

    assertEquals(List(incorrect), plan.zeroLength)
  }

  def testValidation_noResources {
    val correct = "a1" needs 8.hours of tester where NoConditions
    val incorrect = "a4" needs 8.hours of developer where NoConditions

    val milestone = new Milestone(
      "m",
      null,
      List(correct, incorrect),
      List(testerA_availability)
    )

    val plan = new Plan("2 weeks", mon24_sun06, List(milestone))

    assertEquals(List(incorrect), plan.noResources)
  }

  def testValidation_wrongPreferredResources {
    val noConditions = "a1" needs 8.hours of tester where NoConditions
    val correct1 = "a2" needs 8.hours of tester where (PreferredResources())
    val correct2 = "a3" needs 8.hours of developer where (PreferredResources(developerC, developerD))
    val incorrect = "a4" needs 8.hours of developer where (PreferredResources(developerE))

    val milestone = new Milestone(
      "m",
      null,
      List(noConditions, correct1, correct2, incorrect),
      List(developerC_availability, developerD_availability)
    )

    val plan = new Plan("2 weeks", mon24_sun06, List(milestone))

    assertEquals(List(incorrect), plan.wrongPreferredResources)
  }

  def testValidation_circularDependencies {
    /*
    val noConditions = "nc" needs 8.hours of tester where NoConditions

    val a = "a" needs 8.hours of tester where NoConditions
    val b = "b" needs 8.hours of tester where (MustStartAfter(a))
    val c = "c" needs 8.hours of tester where (MustStartAfter(b))
    a.conditions = List(MustStartAfter(c))

    val d = "d" needs 8.hours of tester where NoConditions
    val e = "e" needs 8.hours of tester where (MustStartAfter(d))
    val f = "f" needs 8.hours of tester where (MustStartAfter(e))
    d.conditions = List(JustifyFinishWith(f))

    val g = "g" needs 8.hours of tester where NoConditions
    val h = "h" needs 8.hours of tester where (JustifyFinishWith(g))
    g.conditions = List(JustifyFinishWith(h))

    val milestone = new Milestone(
      "m",
      null,
      List(noConditions,a,b,c,d,e,f,g,h),
      List(testerA_availability)
    )

    val plan = new Plan("2 weeks", mon24_sun06, List(milestone))

    assertEquals(List(a,b,c,d,e,f),plan.circularDependencies)
    */
    fail("to be implemented")
  }

}