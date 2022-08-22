package com.madfish.ide.view

import com.intellij.openapi.util.IconLoader

/**
 * Created by Roger™
 */
interface RHIcons {

    companion object {
        val READHUB = IconLoader.getIcon("/images/readhub.png", this::class.java)
        val COFFEE = IconLoader.getIcon("/images/coffee.png", this::class.java)
        val INTENTION_BULB = IconLoader.getIcon("/images/intentionBulb.png", this::class.java)
        val COPY = IconLoader.getIcon("/images/copy.png", this::class.java)
        val PREVIEW = IconLoader.getIcon("/images/preview.png", this::class.java)
        val REFRESH = IconLoader.getIcon("/images/refresh.png", this::class.java)
        val SECONDARY_GROUP = IconLoader.getIcon("/images/secondaryGroup.png", this::class.java)
        val TOOLWINDOW_FAVORITES =  IconLoader.getIcon("/images/toolWindowFavorites.png", this::class.java)
        val CHROME =  IconLoader.getIcon("/images/chrome16.png", this::class.java)
        val GC =  IconLoader.getIcon("/images/gc.png", this::class.java)
    }
}