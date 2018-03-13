package com.developersam.webcore.service

/**
 * [GoogleUserService] is a singleton that gives out Google users for the
 * current request. It can only be used inside a servlet environment.
 */
object GoogleUserService {

    /**
     * Thread local to store the [GoogleUser].
     */
    private val threadLocal: ThreadLocal<GoogleUser> = ThreadLocal()

    /**
     * The public current user, which may not exists.
     * It can be accessed everywhere in a servlet environment but can only
     * be assigned within the module.
     */
    var currentUser: GoogleUser?
        get() {
            return threadLocal.get()
        }
        internal set(value) {
            threadLocal.set(value)
        }

    /**
     * Reset the user service as the request clean up step.
     */
    internal fun reset() {
        threadLocal.remove()
    }

}