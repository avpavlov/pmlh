package core.model

/**
 * Created by IntelliJ IDEA.
 * User: pav
 * Date: 03.06.2010
 * Time: 0:19:45
 * To change this template use File | Settings | File Templates.
 */

class Activity(val name:String,
               val hours:Int,
               val resourceType:ResourceType,
               val conditions:List[Condition]
              ) extends Ordered[Activity] {

  val predecessors = conditions.filter(_.isInstanceOf[DependsOn]).map(_.asInstanceOf[DependsOn].activity).toSet

  val paths:Set[List[Activity]] = predecessors.flatMap(predecessor=>predecessor.paths match {
    case paths if paths.isEmpty => Set(List(predecessor))
    case paths => paths.map(_:::List(predecessor))
  })

  def successors(activities:List[Activity]) = activities.filter(_.predecessors.contains(this)).toSet

  val cannotBeShared = !conditions.find(_.isInstanceOf[CannotBeShared]).isEmpty
  
  val expectedStartTime = conditions
          .filter(_.isInstanceOf[MustStartOn])
          .map(_.asInstanceOf[MustStartOn].time) match {
            case List() => None
            case notEmptyList => Some(notEmptyList.min)
          }

  override def compare(that: Activity) = (expectedStartTime,that.expectedStartTime) match {
    case (None,None) => 0
    case (None, _) => 1 // if no date then place at end
    case (_, None) => -1
    case (Some(x),Some(y)) => x.compare(y)
  }
}
