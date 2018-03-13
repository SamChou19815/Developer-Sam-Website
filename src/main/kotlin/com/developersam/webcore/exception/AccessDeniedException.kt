package com.developersam.webcore.exception

import java.lang.RuntimeException

/**
 * [AccessDeniedException] should be thrown when a service is accessed illegally
 * in an undesired way.
 */
class AccessDeniedException : RuntimeException("Access Denied!")