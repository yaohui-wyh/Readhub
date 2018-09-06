package com.madfish.ide.view

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.*
import com.intellij.ui.components.panels.Wrapper
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.ListTableModel
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.table.ComponentsListFocusTraversalPolicy
import com.madfish.ide.action.RHExportAction
import com.madfish.ide.action.RHRefreshAction
import com.madfish.ide.action.RHSettingsAction
import com.madfish.ide.action.RHWebAction
import com.madfish.ide.configurable.RHData
import com.madfish.ide.messages.READHUB_REFRESH_TOPIC
import com.madfish.ide.messages.READHUB_VIEW_TOPIC
import com.madfish.ide.messages.TableViewListener
import com.madfish.ide.model.RHBaseItem
import com.madfish.ide.model.RHCategory
import com.madfish.ide.util.RHDataKeys
import com.madfish.ide.util.RHUtil
import net.miginfocom.swing.MigLayout
import java.awt.*
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.table.TableCellRenderer
import javax.swing.text.BadLocationException

/**
 * Created by Roger™
 */
open class RHToolWindowContent(var project: Project, var category: RHCategory) {
    var myTable = TableView<RHBaseItem>()
    var myLinkPanel = JPanel(VerticalFlowLayout(VerticalFlowLayout.LEFT, 0, 2, true, false))
    val background: Color = UIUtil.getTableBackground()
    private var mySummaryPanel = HtmlPanel()
    private var loadMoreBtn = JButton(RHUtil.message("View.loadMore"))
    private var myPaginationLabel = JLabel()

    private val provider: DataProvider = DataProvider {
        when {
            RHDataKeys.tableItem.`is`(it) -> myTable.selectedObject
            else -> null
        }
    }

    fun updateTableContent(filterText: String = "") {
        val columns = getColumns()
        val model = ListTableModel<RHBaseItem>(columns, RHData.instance.getItems(category, filterText))
        myTable.setPaintBusy(false)
        myTable.setModelAndUpdateColumns(model)
        myTable.columnModel.getColumn(columns.size - 1).maxWidth = 80
//        myTable.setRowSelectionInterval(0, 0)
    }

    fun scrollToTableRow(idx: Int) {
        myTable.selectionModel.setSelectionInterval(idx, idx)
        myTable.scrollRectToVisible(Rectangle(myTable.getCellRect(idx, 0, true)))
    }

    private fun setSummaryPanel() {
        val item = RHDataKeys.tableItem.getData(provider)
        mySummaryPanel.margin = Insets(20, 10, 20, 10)
        mySummaryPanel.text = "<html><head>" +
                UIUtil.getCssFontDeclaration(UIUtil.getLabelFont(), UIUtil.getLabelForeground(), null, null) +
                "</head><body>${item?.getSummaryText().orEmpty()}</body></html>"
    }

    private fun setLinkPanel() {
        myLinkPanel.border = IdeBorderFactory.createEmptyBorder(15, 10, 20, 10)
        myLinkPanel.removeAll()
        setLinkContent(myLinkPanel, RHDataKeys.tableItem.getData(provider))
        myLinkPanel.repaint()
        myLinkPanel.revalidate()
    }

    open fun setLinkContent(parent: JPanel, item: RHBaseItem? = null) {}

    open fun getColumns(): Array<ColumnInfo<RHBaseItem, String>> {
        val titleColumn = object : ColumnInfo<RHBaseItem, String>("") {
            override fun valueOf(item: RHBaseItem?) = item?.getTitleText().orEmpty()
            override fun getRenderer(item: RHBaseItem?): TableCellRenderer? = RHBaseCellRenderer(item)
        }

        val datetimeColumn = object : ColumnInfo<RHBaseItem, String>("") {
            override fun valueOf(item: RHBaseItem?) = RHUtil.getTimeDelta(item?.getDateTime())
            override fun getRenderer(item: RHBaseItem?): TableCellRenderer? = RHSmallCellRenderer()
        }
        return arrayOf(titleColumn, datetimeColumn)
    }

    init {
        setView()
        subscribeEvents()
    }

    fun subscribeEvents() {
        val busConnection = project.messageBus.connect(project)
        busConnection.subscribe(READHUB_VIEW_TOPIC, object : TableViewListener {
            override fun onItemClicked(name: String, obj: RHBaseItem?) {
                if (name == category.getName() && obj != null) {
                    RHData.instance.setItemAsRead(obj)
                    setSummaryPanel()
                    setLinkPanel()
                    updatePaginationLabel()
                }
            }

            override fun updateTable(name: String) {
                ApplicationManager.getApplication().invokeLater {
                    updateTableContent()
                    updatePaginationLabel()
                    setSummaryPanel()
                    setLinkPanel()
                }
            }
        })
    }

    private fun setView() {
        updateTableContent()
        initTableStyle()
        setSummaryPanel()
        setLinkPanel()
    }

    /** Called only once */
    open fun initTableStyle() {
        myTable.emptyText.text = RHUtil.message("View.emptyTable")
        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        myTable.showHorizontalLines = false
        myTable.showVerticalLines = false
        myTable.intercellSpacing = JBUI.emptySize()
        myTable.setShowGrid(false)
        myTable.columnModel.columnSelectionAllowed = false
        myTable.rowHeight = 25
        myTable.background = background
        ScrollingUtil.installActions(myTable, false)
        myTable.selectionModel.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                project.messageBus.syncPublisher(READHUB_VIEW_TOPIC).onItemClicked(category.getName(), myTable.selectedObject)
            }
        }
    }

    fun createToolWindow(): SimpleToolWindowPanel {
        val panel = SimpleToolWindowPanel(false, true)
        panel.setContent(createRootPanel())
        return panel
    }

    private fun createRootPanel(): JPanel {
        val splitter = OnePixelSplitter(false, 0.6f)
        splitter.firstComponent = buildTableComponent()
        splitter.secondComponent = buildDetailComponent()

        val rootPanel = JPanel(BorderLayout())
        rootPanel.add(splitter)

        // Avoid default focus on SearchField
        rootPanel.isFocusCycleRoot = true
        rootPanel.focusTraversalPolicy = object : ComponentsListFocusTraversalPolicy() {
            override fun getOrderedComponents(): List<Component> {
                return listOf(myTable)
            }
        }
        return rootPanel
    }

    /**
     * 左侧（上）列表面板的 Toolbar
     */
    private fun buildTableToolbar(): JComponent? {
        val actionGroup = DefaultActionGroup()
        actionGroup.addSeparator()
        actionGroup.add(RHRefreshAction())
        actionGroup.add(RHWebAction(category))
        actionGroup.add(RHSettingsAction())

        val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true)
        toolbar.setTargetComponent(myTable)

        val searchField = object : SearchTextField(true) {}
        searchField.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent?) {
                e?.let {
                    updateTableContent(getText(e))
                    updatePaginationLabel()
                    setSummaryPanel()
                    setLinkPanel()
                }
            }

            private fun getText(e: DocumentEvent): String {
                return try {
                    e.document.getText(0, e.document.length).trim()
                } catch (ex: BadLocationException) {
                    ""
                }
            }
        })
        val wrapper = Wrapper(searchField)
        wrapper.setVerticalSizeReferent(toolbar.component)
        wrapper.border = JBUI.Borders.emptyLeft(5)

        val panel = JPanel(MigLayout("ins 0, fill", "[left]0[left, fill]push[right]", "center"))
        panel.add(wrapper)
        panel.add(toolbar.component)
        panel.add(myPaginationLabel)

        return panel
    }

    fun updatePaginationLabel() {
        if (myTable.selectedRow >= 0) {
            myPaginationLabel.text = "${(myTable.selectedRow + 1)} / ${myTable.listTableModel.items.size}"
            myPaginationLabel.font = JBUI.Fonts.smallFont()
            myPaginationLabel.border = JBUI.Borders.emptyRight(30)
        } else {
            myPaginationLabel.text = ""
        }
    }

    /**
     * 左侧（中）列表面板
     */
    private fun buildTablePane(): JPanel {
        val panel = JPanel(BorderLayout())
        val pane = ScrollPaneFactory.createScrollPane(myTable, SideBorder.TOP)
        pane.verticalScrollBar.addAdjustmentListener { e ->
            val maxOffset = pane.verticalScrollBar.maximum - pane.verticalScrollBar.height
            if (maxOffset > 0 && e.value == maxOffset) {
                loadMoreBtn.isVisible = true
            } else if (maxOffset - e.value > loadMoreBtn.height * 2) {
                loadMoreBtn.isVisible = false
            }
        }

        panel.add(pane, BorderLayout.CENTER)
        panel.add(loadMoreBtn, BorderLayout.SOUTH)
        loadMoreBtn.addActionListener {
            myTable.setPaintBusy(true)
            project.messageBus.syncPublisher(READHUB_REFRESH_TOPIC).loadPrevItems(category)
            loadMoreBtn.isVisible = false
        }
        loadMoreBtn.isVisible = false
        return panel
    }

    /**
     * 左侧容器组件
     */
    private fun buildTableComponent(): SimpleToolWindowPanel {
        val panel = SimpleToolWindowPanel(true, true)
        panel.setToolbar(buildTableToolbar())
        panel.setContent(buildTablePane())
        return panel
    }

    /**
     * 右侧（上）摘要面板的 Toolbar
     */
    private fun buildSummaryToolbar(): JComponent? {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(RHExportAction(provider))

        val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true)
        toolbar.setTargetComponent(mySummaryPanel)
        return toolbar.component
    }

    /**
     * 右侧（上）摘要面板
     */
    private fun buildSummaryPanel(): SimpleToolWindowPanel {
        mySummaryPanel.background = background
        val summaryPane = ScrollPaneFactory.createScrollPane(mySummaryPanel, SideBorder.TOP)

        val panel = SimpleToolWindowPanel(true, true)
        panel.setToolbar(buildSummaryToolbar())
        panel.setContent(summaryPane)

        return panel
    }

    /**
     * 右侧容器组件
     */
    open fun buildDetailComponent(): JComponent {
        val splitter = OnePixelSplitter(true, 0.7f)

        myLinkPanel.background = background
        val linkPane = ScrollPaneFactory.createScrollPane(myLinkPanel, SideBorder.TOP)
        splitter.firstComponent = buildSummaryPanel()
        splitter.secondComponent = linkPane

        return splitter
    }
}
