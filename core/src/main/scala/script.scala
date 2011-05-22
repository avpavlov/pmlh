import annotation.tailrec



/**
 * Created by IntelliJ IDEA.
 * User: pav
 * Date: 31.07.2010
 * Time: 15:03:49
 * To change this template use File | Settings | File Templates.
 */

case class Fraction(numerator:Int, denominator:Int) {
  val isInvalid = denominator == 0
  val integer = if (isInvalid) 0 else numerator / denominator
  val remainder = if (isInvalid) 0 else numerator % denominator
  val signum = Math.signum(numerator) * Math.signum(denominator)

  def compare(cmp:Fraction):Int = {
    if (isInvalid || cmp == null || cmp.isInvalid) throw new IllegalArgumentException
    else if (numerator == 0) -cmp.signum
    else if (signum != cmp.signum) signum
    else if (integer != cmp.integer) Math.signum(integer-cmp.integer)
    else if (remainder == 0) -cmp.signum
    else if (cmp.remainder == 0) signum  
    else -Fraction(denominator, remainder).compare(Fraction(cmp.denominator, cmp.remainder))
  }
}
