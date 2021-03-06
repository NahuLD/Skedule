package com.okkero.skedule

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.resume

fun Plugin.schedule(
        initialContext: SynchronizationContext = SynchronizationContext.SYNC,
        co: suspend BukkitSchedulerController.() -> Unit
): CoroutineTask {
    return server.scheduler.schedule(this, initialContext, co)
}

/**
 * Schedule a coroutine with the Bukkit Scheduler.
 *
 * @receiver The BukkitScheduler instance to use for scheduling tasks.
 * @param plugin The Plugin instance to use for scheduling tasks.
 * @param initialContext The initial synchronization context to start off the coroutine with. See
 * [SynchronizationContext].
 *
 * @see SynchronizationContext
 */
fun BukkitScheduler.schedule(
        plugin: Plugin,
        initialContext: SynchronizationContext = SynchronizationContext.SYNC,
        co: suspend BukkitSchedulerController.() -> Unit
): CoroutineTask {
    val controller = BukkitSchedulerController(plugin, this)
    val block: suspend BukkitSchedulerController.() -> Unit = {
        try {
            start(initialContext)
            co()
        } finally {
            cleanup()
        }
    }

    block.createCoroutine(receiver = controller, completion = controller).resume(Unit)

    return CoroutineTask(controller)
}