package de.sodis.monitoring.ui.viewholder

import android.view.View
import de.sodis.monitoring.ui.model.SodisItem

interface SodisViewHolder: View.OnClickListener  {
    /**
     * binds data of the item to the view
     */
    fun bindView(
        sodisItem: SodisItem
    )
}