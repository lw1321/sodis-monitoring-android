package de.sodis.monitoring.ui.viewholder

import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.ui.adapter.RecyclerViewListener
import de.sodis.monitoring.ui.model.SodisItem
import de.sodis.monitoring.ui.model.TextItem
import kotlinx.android.synthetic.main.text_input_item.view.*

class AnswerTextViewHolder(itemView: View, private val recyclerViewClickListener: RecyclerViewListener) : RecyclerView.ViewHolder(itemView), SodisViewHolder {
    override fun onClick(v: View?) {
        //do nothing
    }

    override fun bindView(sodisItem: SodisItem) {
        itemView.answerTextInput.hint = (sodisItem as TextItem).hiddenText
    }

}
