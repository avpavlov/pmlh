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

class TimeTest extends TestCase("Time") with TestingEnvironment {

  def testCompare {
    assertTrue(mon24_0.compare(mon24_0) == 0)
    assertTrue(mon24_0.compare(mon24_4) < 0)
    assertTrue(mon24_4.compare(mon24_0) > 0)

    assertTrue(mon24_0.compare(mon31_0) < 0)
    assertTrue(mon31_0.compare(mon24_0) > 0)
  }

}