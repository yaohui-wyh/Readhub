package com.madfish.ide.view

import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.IconLoader
import com.intellij.ui.awt.RelativePoint
import java.awt.GraphicsEnvironment
import java.awt.Point
import java.awt.Window
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * Created by Roger™
 */
class RewardPopup {

    companion object {

        fun show(window: Window?) {
            val panel = JPanel()
            val image = IconLoader.getIcon("/images/reward.png", this::class.java)
            panel.add(JLabel(image))
            val r = if (window != null) window.bounds else GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration.bounds
            val point = Point((r.width - image.iconWidth) / 2, (r.height - image.iconHeight) / 2)
            val location = if (window != null) RelativePoint(window, point) else RelativePoint(point)
            JBPopupFactory.getInstance().createComponentPopupBuilder(panel, panel)
                    .setTitle("WeChat QR Code")
                    .setRequestFocus(true)
                    .setFocusable(true)
                    .setResizable(false)
                    .setMovable(false)
                    .setModalContext(false)
                    .setShowShadow(true)
                    .setShowBorder(false)
                    .setCancelKeyEnabled(true)
                    .setCancelOnClickOutside(true)
                    .setCancelOnOtherWindowOpen(true)
                    .createPopup()
                    .show(location)
        }
    }
}
