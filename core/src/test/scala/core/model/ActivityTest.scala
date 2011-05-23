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
import core.util.TestingEnvironment
import junit.framework._

import Assert._
import core.model._

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