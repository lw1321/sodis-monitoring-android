package de.sodis.monitoring.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.ui.adapter.RecyclerViewListener
import de.sodis.monitoring.ui.model.SodisItem
import kotlinx.android.synthetic.main.navigation_item.view.*

class NavigationButtonViewHolder(itemView: View, private val recyclerViewClickListener: RecyclerViewListener): RecyclerView.ViewHolder(itemView), SodisViewHolder {

    override fun onClick(v: View?) {
        //do nothing
    }

    override fun bindView(sodisItem: SodisItem) {
        //nothing to bind
        itemView.navigation_forward_button_2.setOnClickListener {
            recyclerViewClickListener.recyclerViewListCLicked(itemView, adapterPosition)
        }
    }

}