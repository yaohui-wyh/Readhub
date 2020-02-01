package com.madfish.ide.util

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationListener
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 * Created by Roger™
 */
class Notification {

    companion object {
        private val NOTIFICATION_TOOLWINDOW_GROUP = NotificationGroup.toolWindowGroup(
                Constants.Plugins.name,
                Constants.ToolWindows.toolWindowId,
                false
        )

        fun successBalloon(project: Project?, content: String, listener: NotificationListener? = null, title: String = "") {
            NOTIFICATION_TOOLWINDOW_GROUP.createNotification(title, content, NotificationType.INFORMATION, listener).notify(project)
        }

        fun warnBalloon(project: Project?, content: String, listener: NotificationListener? = null, title: String = "") {
            NOTIFICATION_TOOLWINDOW_GROUP.createNotification(title, content, NotificationType.WARNING, listener).notify(project)
        }

        fun errorBalloon(project: Project?, content: String, listener: NotificationListener? = null, title: String = "") {
            NOTIFICATION_TOOLWINDOW_GROUP.createNotification(title, content, NotificationType.ERROR, listener).notify(project)
        }
    }
}