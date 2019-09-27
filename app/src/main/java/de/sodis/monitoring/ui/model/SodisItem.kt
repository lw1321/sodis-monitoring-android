package de.sodis.monitoring.ui.model

abstract class SodisItem (var type: Int, var childItemList: List<SodisItem> = emptyList(), var isExpanded: Boolean = false) {
    class TYPE {
        companion object {
            const val TYPE_PARENT_DEFAULT = 0
        }
    }
}


