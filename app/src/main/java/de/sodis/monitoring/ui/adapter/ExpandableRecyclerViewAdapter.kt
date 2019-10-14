package de.sodis.monitoring.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.sodis.monitoring.R
import de.sodis.monitoring.ui.model.SodisItem
import de.sodis.monitoring.ui.viewholder.*

class ExpandableRecyclerViewAdapter (
    val listener: RecyclerViewListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    RecyclerViewListener{

    private  var items: MutableList<SodisItem> = mutableListOf()

    //todo refactor, use recyclerview for child items
    override fun recyclerViewListCLicked(view: View, id: Any) {
        listener.recyclerViewListCLicked(view, id)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            SodisItem.TYPE.TYPE_PARENT_DEFAULT-> ParentDefaultViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.default_item, parent, false),
                this
            )
            SodisItem.TYPE.AUTO_COMPLETE_WITH_HEADER_ITEM-> AutoCompleteHeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.interviewee_item, parent, false),
                this
            )
            SodisItem.TYPE.TYPE_ANSWER_SELECT-> AnswerSelectViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.text_choice_item, parent, false),
                this
            )
            SodisItem.TYPE.TYPE_ANSWER_TEXT-> AnswerTextViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.text_input_item, parent, false),
                this
            )
            else -> ParentDefaultViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.default_item, parent, false),
                this
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SodisViewHolder).bindView(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    /**
     * Set the data and calls notifyDataSetChanged()
     */
    fun setItems(items:List<SodisItem>){
        this.items = items.toMutableList()
        notifyDataSetChanged()
    }
}

