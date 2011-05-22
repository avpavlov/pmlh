package core.model

/**
 * Created by IntelliJ IDEA.
 * User: pav
 * Date: 03.06.2010
 * Time: 0:18:50
 * To change this template use File | Settings | File Templates.
 */

abstract class Condition
abstract class DependsOn(val activity:Activity) extends Condition
abstract class Blocks(val activity:Activity) extends Condition
case class CannotBeShared extends Condition
case class MustStartOn(val time:Time) extends Condition
case class StartAfter(override val activity:Activity) extends DependsOn(activity)
case class FinishNoEarlierThan(override val activity:Activity) extends DependsOn(activity)

