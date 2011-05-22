package core.model

import junit.framework.TestCase
import core.util.TestingEnvironment
import junit.framework._

import Assert._
import core.model._
/**
 * Created by IntelliJ IDEA.
 * User: pav
 * Date: 03.06.2010
 * Time: 0:26:47
 * To change this template use File | Settings | File Templates.
 */

class ActivityTest extends TestCase("Activity") with TestingEnvironment {

  val a1 = new Activity("a1",1,developer,List(CannotBeShared(),MustStartOn(wed02_0)))
  val a2 = new Activity("a2",1,developer,List(FinishNoEarlierThan(a1),MustStartOn(wed02_0)))
  val a3 = new Activity("a3",1,developer,List(FinishNoEarlierThan(a1),MustStartOn(wed02_0),MustStartOn(mon24_0)))
  val a4 = new Activity("a4",1,developer,List(FinishNoEarlierThan(a1)))
  val a5 = new Activity("a5",1,developer,Nil)

  def testExpectedStartDate {
    assertEquals(Some(wed02_0), a1.expectedStartTime)
    assertEquals(Some(wed02_0), a2.expectedStartTime)
    assertEquals(Some(mon24_0), a3.expectedStartTime)
    assertEquals(None, a4.expectedStartTime)
    assertEquals(None, a5.expectedStartTime)
  }
  
  def testCompare {
    assertTrue(a1.compare(a2) == 0)
    assertTrue(a1.compare(a3) > 0)
    assertTrue(a1.compare(a4) < 0)
    assertTrue(a1.compare(a5) < 0)

    assertTrue(a2.compare(a1) == 0)
    assertTrue(a2.compare(a3) > 0)
    assertTrue(a2.compare(a4) < 0)
    assertTrue(a2.compare(a5) < 0)

    assertTrue(a3.compare(a1) < 0)
    assertTrue(a3.compare(a2) < 0)
    assertTrue(a3.compare(a4) < 0)
    assertTrue(a3.compare(a5) < 0)

    assertTrue(a4.compare(a1) > 0)
    assertTrue(a4.compare(a2) > 0)
    assertTrue(a4.compare(a3) > 0)
    assertTrue(a4.compare(a5) == 0)

    assertTrue(a5.compare(a1) > 0)
    assertTrue(a5.compare(a2) > 0)
    assertTrue(a5.compare(a3) > 0)
    assertTrue(a5.compare(a4) == 0)
  }


}