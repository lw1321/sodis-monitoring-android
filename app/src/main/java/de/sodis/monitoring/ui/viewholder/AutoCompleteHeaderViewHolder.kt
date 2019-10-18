package de.sodis.monitoring.ui.viewholder

import android.R
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.ui.adapter.RecyclerViewListener
import de.sodis.monitoring.ui.model.AutoCompleteHeaderItem
import de.sodis.monitoring.ui.model.SodisItem
import kotlinx.android.synthetic.main.interviewee_item.view.*


class AutoCompleteHeaderViewHolder(itemView: View, private val recyclerViewClickListener: RecyclerViewListener) : RecyclerView.ViewHolder(itemView), SodisViewHolder {

    override fun onClick(v: View?) {
        //recyclerViewClickListener.recyclerViewListCLicked(v!!, adapterPosition)
    }
    override fun bindView(sodisItem: SodisItem) {
        val item = sodisItem as AutoCompleteHeaderItem
        itemView.title.text = item.title

        val adapter = ArrayAdapter<String>(
            itemView.context,
            R.layout.simple_dropdown_item_1line, sodisItem.list
        )
        itemView.multiAutoCompleteTextView.threshold = 1 //will start working from first character
        itemView.multiAutoCompleteTextView.setAdapter(adapter)

    }
}
