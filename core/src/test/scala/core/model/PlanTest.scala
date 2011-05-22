/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

/*
 * Copyright (c) Alexander Pavlov 2010-2011.
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