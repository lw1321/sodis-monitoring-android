package de.sodis.monitoring.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.ui.adapter.RecyclerViewListerner
import de.sodis.monitoring.ui.model.DefaultParentItem
import de.sodis.monitoring.ui.model.SodisItem
import kotlinx.android.synthetic.main.default_item.view.*

class ParentDefaultViewHolder(
    itemView: View,
    private val recyclerViewClickListener: RecyclerViewListerner
) : RecyclerView.ViewHolder(itemView), SodisViewHolder {


    override fun onClick(p0: View?) {
        recyclerViewClickListener.recyclerViewListCLicked(p0!!, adapterPosition)
    }

    override fun bindView(
        sodisItem: SodisItem
    ) {
        val parentItem = sodisItem as DefaultParentItem
        itemView.surveyHeaderTitle.text = parentItem.title
        itemView.setOnClickListener(this)
    }
}