package de.sodis.monitoring.ui.model

abstract class SodisItem (var type: Int, var childItemList: List<SodisItem> = emptyList(), var isExpanded: Boolean = false) {
    class TYPE {
        companion object {
            const val TYPE_PARENT_DEFAULT = 0
            const val AUTO_COMPLETE_WITH_HEADER_ITEM = 1
            const val TYPE_QUESTION = 2
            const val TYPE_ANSWER_SELECT = 3
            const val TYPE_ANSWER_TEXT = 4
            const val TYPE_NAVIGATION_BUTTON = 5
            const val TYPE_NAVIGATION_FINISH_BUTTON = 6
        }

    }
}


