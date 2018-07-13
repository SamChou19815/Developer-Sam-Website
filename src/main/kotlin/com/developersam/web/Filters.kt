package com.developersam.web

import com.developersam.auth.SecurityFilters

/**
 * [Filters] can be used to create security filters.
 */
object Filters : SecurityFilters(adminEmails = setOf())
