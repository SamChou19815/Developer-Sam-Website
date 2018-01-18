package com.developersam.webcore.service

import com.developersam.webcore.gson.globalGson
import com.google.gson.Gson
import com.developersam.control.NoArgService as NAS
import com.developersam.control.OneArgService as OAS
import com.developersam.control.TwoArgsService as TAS
import com.developersam.control.StructuredInputService as SIS

/**
 * A [NoArgService] with the default [globalGson].
 */
abstract class NoArgService protected constructor() : NAS() {
    final override val gson: Gson = globalGson
}

/**
 * A [OneArgService] with the default [globalGson].
 */
abstract class OneArgService protected constructor(parameterName: String) :
        OAS(parameterName = parameterName) {
    final override val gson: Gson = globalGson
}

/**
 * A [TwoArgsService] with the default [globalGson].
 */
abstract class TwoArgsService protected constructor(
        parameter1Name: String,
        parameter2Name: String
) : TAS(parameter1Name = parameter1Name, parameter2Name = parameter2Name) {
    final override val gson: Gson = globalGson
}

/**
 * A [StructuredInputService] with the default [globalGson].
 */
abstract class StructuredInputService<in I> protected constructor(
        inType: Class<I>
) : SIS<I>(inType = inType) {
    final override val gson: Gson = globalGson
}