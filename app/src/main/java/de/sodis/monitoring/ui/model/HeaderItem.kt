package de.sodis.monitoring.ui.model

class HeaderItem (val title: String, childItemList: List<SodisItem> = emptyList()) : SodisItem(
    TYPE.TYPE_HEADER_DEFAULT, childItemList = childItemList
)
