package de.sodis.monitoring.ui.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.ui.adapter.RecyclerViewListener
import de.sodis.monitoring.ui.model.SelectItem
import de.sodis.monitoring.ui.model.SodisItem
import kotlinx.android.synthetic.main.text_choice_item.view.*

class AnswerSelectViewHolder(itemView: View, private val recyclerViewClickListener: RecyclerViewListener) : RecyclerView.ViewHolder(itemView), SodisViewHolder {

    override fun onClick(v: View?) {
        //do nothing
    }

    override fun bindView(sodisItem: SodisItem) {
        val item = sodisItem as SelectItem
        itemView.optionButton.text = item.name
    }

}
