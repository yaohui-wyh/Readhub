package com.madfish.ide.util

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

/**
 * Created by Roger™
 */
class Notification {

    companion object {
        private val NOTIFICATION_TOOLWINDOW_GROUP = NotificationGroupManager.getInstance().getNotificationGroup(Constants.Plugins.name)

        fun successBalloon(project: Project?, content: String, title: String = "") {
            NOTIFICATION_TOOLWINDOW_GROUP.createNotification(title, content, NotificationType.INFORMATION).notify(project)
        }

        fun errorBalloon(project: Project?, content: String, title: String = "") {
            NOTIFICATION_TOOLWINDOW_GROUP.createNotification(title, content, NotificationType.ERROR).notify(project)
        }
    }
}