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
import java.text.SimpleDateFormat
import core.util.TestingEnvironment

class PlanTest extends TestCase("Plan") with TestingEnvironment {
  def testDependencies {

    val implementation = new Activity("implementation", 32, developer, Nil)
    val ongoingTesting = new Activity("ongoing testing", 16, tester, List(FinishNoEarlierThan(implementation)))
    val finalTesting = new Activity("final testing", 16, tester, List(StartAfter(implementation),StartAfter(ongoingTesting),CannotBeShared()))
    val deployOnUat = new Activity("deploy on UAT", 4, developer, List(MustStartOn(wed02_0),StartAfter(finalTesting),CannotBeShared()))
    val deployOnProduction = new Activity("deploy on production", 4, developer, List(MustStartOn(fri04_0),StartAfter(deployOnUat),CannotBeShared()))

    val milestone1 = new Milestone(
      "1.0.0",
      null,
      List(implementation,ongoingTesting,finalTesting,deployOnUat,deployOnProduction),
      List(developerC_availability,developerD_availability,testerA_availability)
    )

    val loadTesting = new Activity("load testing", 8, tester, List(StartAfter(finalTesting),CannotBeShared()))

    val milestone2 = new Milestone(
        "performance analysis",
      null,
      List(loadTesting),
      List(testerB_availability)
    )

    val plan = new Plan("2 weeks",mon24_0,List(milestone1,milestone2))

    assertFalse(implementation.cannotBeShared)
    assertFalse(ongoingTesting.cannotBeShared)
    assertTrue(finalTesting.cannotBeShared)
    assertTrue(deployOnUat.cannotBeShared)
    assertTrue(deployOnProduction.cannotBeShared)
    assertTrue(loadTesting.cannotBeShared)
    assertEquals(Set(),implementation.predecessors)
    assertEquals(Set(implementation),ongoingTesting.predecessors)
    assertEquals(Set(ongoingTesting,implementation),finalTesting.predecessors)
    assertEquals(Set(finalTesting),deployOnUat.predecessors)
    assertEquals(Set(deployOnUat),deployOnProduction.predecessors)
    assertEquals(Set(finalTesting),loadTesting.predecessors)
    assertEquals(Set(),implementation.paths)
    assertEquals(Set(List(implementation)),ongoingTesting.paths)
    assertEquals(Set(List(implementation,ongoingTesting),List(implementation)),finalTesting.paths)
    assertEquals(Set(List(implementation,ongoingTesting,finalTesting),List(implementation,finalTesting)),deployOnUat.paths)
    assertEquals(Set(List(implementation,ongoingTesting,finalTesting,deployOnUat),List(implementation,finalTesting,deployOnUat)),deployOnProduction.paths)
    assertEquals(Set(List(implementation,ongoingTesting,finalTesting),List(implementation,finalTesting)),loadTesting.paths)
    assertEquals(
      Map(
        implementation->List(developerC_availability,developerD_availability),
        ongoingTesting->List(testerA_availability),
        finalTesting->List(testerA_availability),
        deployOnUat->List(developerC_availability,developerD_availability),
        deployOnProduction->List(developerC_availability,developerD_availability),
        loadTesting->List(testerB_availability)
      )
      ,plan.resourcesMap
    )
    assertEquals(
      Map(
        implementation->Set(ongoingTesting,finalTesting),
        ongoingTesting->Set(finalTesting),
        finalTesting->Set(deployOnUat, loadTesting),
        deployOnUat->Set(deployOnProduction),
        deployOnProduction->Set(),
        loadTesting->Set()
      )
      ,plan.successorsMap
    )
    assertEquals(List(implementation), plan.startActivities)
    assertEquals(List(deployOnProduction, loadTesting), plan.finalActivities)
  }
}