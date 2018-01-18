package com.developersam.controller

import com.developersam.control.HttpMethod
import com.developersam.scheduler.Scheduler
import com.developersam.scheduler.SchedulerItemData
import com.developersam.webcore.service.NoArgService
import com.developersam.webcore.service.OneArgService
import com.developersam.webcore.service.StructuredInputService
import com.developersam.webcore.service.TwoArgsService
import com.google.appengine.api.users.UserServiceFactory

/**
 * A service that loads list of scheduler items for a user, or gives a login
 * URL to the user.
 */
object SchedulerLoadItemsService : NoArgService() {

    override val uri: String = "/apis/scheduler/load"

    override val output: Any
        get() {
            val userService = UserServiceFactory.getUserService()
            return if (userService.isUserLoggedIn) {
                Scheduler.allSchedulerItems
            } else {
                "url: " + userService.createLoginURL("/scheduler")
            }
        }

}

/**
 * A scheduler service that write a scheduler item into database.
 */
object SchedulerWriteItemService : StructuredInputService<SchedulerItemData>(
        inType = SchedulerItemData::class.java
) {

    override val uri: String = "/apis/scheduler/write"
    override val method: HttpMethod = HttpMethod.POST

    override fun output(input: SchedulerItemData): Boolean {
        return input.writeToDatabase()
    }

}

/**
 * A scheduler service that delete an scheduler item.
 */
object SchedulerDeleteItemService : OneArgService(parameterName = "key") {

    override val uri: String = "/apis/scheduler/delete"

    override fun output(argument: String): Any? {
        Scheduler.delete(key = argument)
        return null
    }

}

/**
 * A scheduler service that mark an scheduler item as completed or uncompleted.
 */
object SchedulerMarkAsService: TwoArgsService(
        parameter1Name = "key",
        parameter2Name = "completed"
) {
    override val uri: String = "/apis/scheduler/markAs"
    override val method: HttpMethod = HttpMethod.POST

    override fun output(argument1: String, argument2: String): Any? {
        val completed = argument2.toBoolean()
        Scheduler.markAs(key = argument1, completionStatus = completed)
        return null
    }

}