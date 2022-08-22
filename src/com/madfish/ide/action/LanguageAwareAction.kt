package com.madfish.ide.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.Icon

/**
 * Created by Roger™
 */
abstract class LanguageAwareAction(
        private val text: String,
        private val description: String,
        icon: Icon?
) : AnAction(text, description, icon) {

    override fun update(e: AnActionEvent) {
        e.presentation.let { p ->
            p.text = text
            p.description = description
        }
    }
}