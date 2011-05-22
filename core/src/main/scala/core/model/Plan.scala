package core.model

import java.util.{Date}
/**
 * Created by IntelliJ IDEA.
 * User: Hp
 * Date: 27.05.2010
 * Time: 23:48:43
 * To change this template use File | Settings | File Templates.
 */

class Project(val name:String)
class Milestone(val name:String,
                val project:Project,
                val activities:List[Activity],
                val availableResources:List[AvailableResource])

class Plan(val name:String,val start:Time,val milestones:List[Milestone]) {

  val activities = milestones.flatMap(_.activities)

  val resourcesMap = milestones.flatMap(m => m.activities.map(a => a -> m.availableResources.filter(_.resource.resourceType==a.resourceType))).toMap

  // map activity to its successors
  val successorsMap = activities.map(activity => (activity->activity.successors(activities))).toMap

  // entry points (no predecessors)
  val startActivities = activities.filter(_.predecessors.isEmpty)

  // exit points (no successors)
  val finalActivities = activities.filter(successorsMap(_).isEmpty).sorted
}

