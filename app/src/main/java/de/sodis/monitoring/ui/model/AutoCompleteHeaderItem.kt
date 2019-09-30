package de.sodis.monitoring.ui.model

class AutoCompleteHeaderItem(val title: String, val list: List<String>) : SodisItem(TYPE.AUTO_COMPLETE_WITH_HEADER_ITEM, childItemList = emptyList())