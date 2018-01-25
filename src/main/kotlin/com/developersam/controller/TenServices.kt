package com.developersam.controller

import com.developersam.webcore.service.HttpMethod
import com.developersam.ten.TenBoard
import com.developersam.ten.TenClientMove
import com.developersam.ten.TenServerResponse
import com.developersam.webcore.service.StructuredInputService

/**
 * The TEN game service that gives back an AI response for a human move.
 */
object TenResponseService : StructuredInputService<TenClientMove>(
        inType = TenClientMove::class.java
) {

    override val uri: String = "/apis/ten/response"
    override val method: HttpMethod = HttpMethod.POST

    override fun output(input: TenClientMove): TenServerResponse {
        return TenBoard.respond(clientMove = input)
    }

}