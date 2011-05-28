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

import core.util.TestingEnvironment
import junit.framework._
import scala.Some
import junit.framework.Assert._

class ActivityTest extends TestCase("Activity") with TestingEnvironment {

  def testCannotBeShared() {
    def cannotBeShared(conditions: Condition*) = new Activity("", 1, tester, conditions.toList).cannotBeShared

    assertEquals(true, cannotBeShared(CannotBeShared, MustStartOn(wed02_0), CannotBeShared))
    assertEquals(true, cannotBeShared(MustStartOn(wed02_0), CannotBeShared))
    assertEquals(false, cannotBeShared(MustStartOn(wed02_0)))
    assertEquals(true, cannotBeShared(CannotBeShared))
    assertEquals(false, cannotBeShared())
  }

  def testMustStartOn() {
    def mustStartOn(conditions: Condition*) = new Activity("", 1, tester, conditions.toList).mustStartOn

    assertEquals(Some(wed02_0), mustStartOn(CannotBeShared, MustStartOn(wed02_0), CannotBeShared, ShouldStartAfter(mon24_0)))
    assertEquals(Some(mon24_0), mustStartOn(MustStartOn(wed02_0), CannotBeShared, MustStartOn(mon24_0)))
    assertEquals(Some(wed02_0), mustStartOn(MustStartOn(wed02_0)))
    assertEquals(None, mustStartOn(CannotBeShared))
    assertEquals(None, mustStartOn())
  }

  def testShouldStartAfter() {
    def shouldStartAfter(conditions: Condition*) = new Activity("", 1, tester, conditions.toList).shouldStartAfter

    assertEquals(Some(mon24_0), shouldStartAfter(CannotBeShared, MustStartOn(wed02_0), CannotBeShared, ShouldStartAfter(mon24_0)))
    assertEquals(Some(wed02_0), shouldStartAfter(ShouldStartAfter(wed02_0), CannotBeShared, ShouldStartAfter(mon24_0)))
    assertEquals(Some(wed02_0), shouldStartAfter(ShouldStartAfter(wed02_0)))
    assertEquals(None, shouldStartAfter(CannotBeShared))
    assertEquals(None, shouldStartAfter())
  }

  def testShouldFinishBefore() {
    def shouldFinishBefore(conditions: Condition*) = new Activity("", 1, tester, conditions.toList).shouldFinishBefore

    assertEquals(Some(mon24_0), shouldFinishBefore(CannotBeShared, MustStartOn(wed02_0), CannotBeShared, ShouldFinishBefore(mon24_0)))
    assertEquals(Some(mon24_0), shouldFinishBefore(ShouldFinishBefore(wed02_0), CannotBeShared, ShouldFinishBefore(mon24_0)))
    assertEquals(Some(wed02_0), shouldFinishBefore(ShouldFinishBefore(wed02_0)))
    assertEquals(None, shouldFinishBefore(CannotBeShared))
    assertEquals(None, shouldFinishBefore())
  }

  def testBasePlanningTime() {
    def basePlanningTime(conditions: Condition*) = new Activity("", 1, tester, conditions.toList).basePlanningTime

    assertEquals(Some(wed26_0), basePlanningTime(ShouldStartAfter(mon24_0), ShouldFinishBefore(tue25_0), MustStartOn(wed26_0)))
    assertEquals(Some(tue25_0), basePlanningTime(MustStartOn(tue25_0)))
    assertEquals(Some(tue25_0), basePlanningTime(MustStartOn(wed02_0), MustStartOn(tue25_0)))
    assertEquals(None, basePlanningTime(CannotBeShared))
    assertEquals(None, basePlanningTime())
    assertEquals(Some(tue25_0), basePlanningTime(ShouldStartAfter(mon24_0), ShouldFinishBefore(tue25_0)))
    assertEquals(Some(tue25_0), basePlanningTime(ShouldFinishBefore(tue25_0)))
    assertEquals(Some(mon24_0), basePlanningTime(ShouldFinishBefore(tue25_0), ShouldFinishBefore(mon24_0)))
    assertEquals(None, basePlanningTime(ShouldStartAfter(tue25_0)))
  }

}