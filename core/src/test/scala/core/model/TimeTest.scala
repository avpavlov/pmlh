/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

package core.model

import junit.framework.TestCase
import core.util.TestingEnvironment
import junit.framework._

import Assert._
import core.model._

class TimeTest extends TestCase("Time") with TestingEnvironment {

  def testCompare {
    assertTrue(mon24_0.compare(mon24_0) == 0)
    assertTrue(mon24_0.compare(mon24_4) < 0)
    assertTrue(mon24_4.compare(mon24_0) > 0)

    assertTrue(mon24_0.compare(mon31_0) < 0)
    assertTrue(mon31_0.compare(mon24_0) > 0)
  }

}