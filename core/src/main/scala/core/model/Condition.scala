/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

package core.model

abstract class Condition
abstract class DependsOn(val activity:Activity) extends Condition
abstract class Blocks(val activity:Activity) extends Condition
case class CannotBeShared extends Condition
case class MustStartOn(val time:Time) extends Condition
case class StartAfter(override val activity:Activity) extends DependsOn(activity)
case class FinishNoEarlierThan(override val activity:Activity) extends DependsOn(activity)

