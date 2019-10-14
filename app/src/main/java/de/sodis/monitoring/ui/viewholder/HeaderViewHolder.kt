package de.sodis.monitoring.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.ui.adapter.RecyclerViewListener
import de.sodis.monitoring.ui.model.HeaderItem
import de.sodis.monitoring.ui.model.SodisItem
import kotlinx.android.synthetic.main.header_item.view.*

class HeaderViewHolder(itemView: View, private val recyclerViewClickListener: RecyclerViewListener) : RecyclerView.ViewHolder(itemView), SodisViewHolder {

    override fun bindView(sodisItem: SodisItem) {
        itemView.headerTitle.text = (itemView as HeaderItem).title
    }

    override fun onClick(v: View?) {
        //do nothing
    }

}
