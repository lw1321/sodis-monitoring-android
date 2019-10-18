package de.sodis.monitoring.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.ui.adapter.RecyclerViewListener
import de.sodis.monitoring.ui.model.SodisItem
import kotlinx.android.synthetic.main.navigation_finish_item.view.*

class NavigationFinishButtonViewHolder(
    itemView: View,
    private val recyclerViewClickListener: RecyclerViewListener
) : RecyclerView.ViewHolder(itemView), SodisViewHolder {

    override fun bindView(sodisItem: SodisItem) {
        itemView.navigation_finish_button.setOnClickListener {
            recyclerViewClickListener.recyclerViewListCLicked(itemView, adapterPosition)
        }
    }

    override fun onClick(v: View?) {
        //do nothing
    }
}
