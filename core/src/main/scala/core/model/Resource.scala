package core.model

/**
 * Created by IntelliJ IDEA.
 * User: pav
 * Date: 03.06.2010
 * Time: 0:19:16
 * To change this template use File | Settings | File Templates.
 */

case class ResourceType(val name:String)
case class Resource(val name:String,val resourceType:ResourceType,val calendar:WorkingCalendar)
case class AvailableResource(val resource:Resource,val period:Period)

