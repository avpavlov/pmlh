/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

/*
 * Copyright (c) Alexander Pavlov 2010-2011.
 */

package core.model

case class ResourceType(val name:String)
case class Resource(val name:String,val resourceType:ResourceType,val calendar:WorkingCalendar)
case class AvailableResource(val resource:Resource,val period:Period)

