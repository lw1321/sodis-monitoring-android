package de.sodis.monitoring.ui.model

class DefaultParentItem(val title: String, childItemList: List<SodisItem> = emptyList()) : SodisItem(
    TYPE.TYPE_PARENT_DEFAULT, childItemList = childItemList
)