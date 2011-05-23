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
import junit.framework.Assert._

class PeriodTest extends TestCase("Period") with TestingEnvironment {

  val tested = default.getPeriod(tue25_0,24)
  val theSame = default.getPeriod(tue25_0,24)
  val startsTheSameTimeButShorter = default.getPeriod(tue25_0,1)
  val startsTheSameTimeButLonger = default.getPeriod(tue25_0,32)
  val endsTheSameTimeButShorter = default.getPeriod(thu27_0,8)
  val endsTheSameTimeButLonger = default.getPeriod(thu27_8,-40)
  val inside = default.getPeriod(wed26_0,8)
  val surround = default.getPeriod(fri28_4,-48)
  val endsImmediatelyBefore = default.getPeriod(tue25_0,-8)
  val endsBefore = default.getPeriod(mon24_0,-8)
  val startsImmediatelyAfter = default.getPeriod(thu27_8,8)
  val startsAfter = default.getPeriod(tue01_0,1)
  val intersectedStartsBefore = default.getPeriod(wed26_0,-24)
  val intersectedEndsAfter = default.getPeriod(wed26_0,24)

 val zero = default.getPeriod(mon24_0,0)
  val threeDays = default.getPeriod(tue25_0,24)

  val p1 = default.getPeriod(mon24_0,32)
  val p2 = default.getPeriod(mon24_0,1)
  val p3 = default.getPeriod(tue25_0,32)
  val p4 = default.getPeriod(tue01_0,1)
  val p5 = default.getPeriod(tue01_0,1)
  
  def testIntersection {
    def emptyStartsAt(t:Time) = default.getPeriod(t,0)

    assertEquals(tested, tested.intersection(theSame, default))
    assertEquals(startsTheSameTimeButShorter, tested.intersection(startsTheSameTimeButShorter, default))
    assertEquals(tested, tested.intersection(startsTheSameTimeButLonger, default))
    assertEquals(endsTheSameTimeButShorter, tested.intersection(endsTheSameTimeButShorter, default))
    assertEquals(tested, tested.intersection(endsTheSameTimeButLonger, default))
    assertEquals(inside, tested.intersection(inside, default))
    assertEquals(tested, tested.intersection(surround, default))
    assertEquals(emptyStartsAt(endsImmediatelyBefore.start), tested.intersection(endsImmediatelyBefore, default))
    assertEquals(emptyStartsAt(endsBefore.start), tested.intersection(endsBefore, default))
    assertEquals(emptyStartsAt(tested.start), tested.intersection(startsImmediatelyAfter, default))
    assertEquals(emptyStartsAt(tested.start), tested.intersection(startsAfter, default))
    assertEquals(default.getPeriod(wed26_0, -8), tested.intersection(intersectedStartsBefore, default))
    assertEquals(default.getPeriod(wed26_0, 16), tested.intersection(intersectedEndsAfter, default))
  }

  def testCompare {
    assertTrue(tested.compare(theSame) == 0)
    assertTrue(tested.compare(startsTheSameTimeButShorter) < 0)
    assertTrue(tested.compare(startsTheSameTimeButLonger) > 0)
    assertTrue(tested.compare(endsTheSameTimeButShorter) < 0)
    assertTrue(tested.compare(endsTheSameTimeButLonger) > 0)
    assertTrue(tested.compare(inside) < 0)
    assertTrue(tested.compare(surround) > 0)
    assertTrue(tested.compare(endsImmediatelyBefore) > 0)
    assertTrue(tested.compare(endsBefore) > 0)
    assertTrue(tested.compare(startsImmediatelyAfter) < 0)
    assertTrue(tested.compare(startsAfter) < 0)
    assertTrue(tested.compare(intersectedStartsBefore) > 0)
    assertTrue(tested.compare(intersectedEndsAfter) < 0)
  }

  def testSubtraction {
    def f2f(p1:Period,p2:Period) = default.getPeriod(p1.finish,p2.finish)
    def s2s(p1:Period,p2:Period) = default.getPeriod(p1.start,p2.start)

    assertEquals(List(), tested.subtraction(theSame, default))
    assertEquals(List(f2f(startsTheSameTimeButShorter,tested)), tested.subtraction(startsTheSameTimeButShorter, default))
    assertEquals(List(), tested.subtraction(startsTheSameTimeButLonger, default))
    assertEquals(List(s2s(tested,endsTheSameTimeButShorter)), tested.subtraction(endsTheSameTimeButShorter, default))
    assertEquals(List(), tested.subtraction(endsTheSameTimeButLonger, default))
    assertEquals(List(s2s(tested,inside),f2f(inside,tested)), tested.subtraction(inside, default))
    assertEquals(List(), tested.subtraction(surround, default))
    assertEquals(List(tested), tested.subtraction(endsImmediatelyBefore, default))
    assertEquals(List(tested), tested.subtraction(endsBefore, default))
    assertEquals(List(tested), tested.subtraction(startsImmediatelyAfter, default))
    assertEquals(List(tested), tested.subtraction(startsAfter, default))
    assertEquals(List(f2f(intersectedStartsBefore,tested)), tested.subtraction(intersectedStartsBefore, default))
    assertEquals(List(s2s(tested,intersectedEndsAfter)), tested.subtraction(intersectedEndsAfter, default))
  }
}