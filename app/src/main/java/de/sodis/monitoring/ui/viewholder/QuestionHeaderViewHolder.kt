package de.sodis.monitoring.ui.viewholder

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.sodis.monitoring.ui.adapter.RecyclerViewListener
import de.sodis.monitoring.ui.model.QuestionItem
import de.sodis.monitoring.ui.model.SodisItem
import kotlinx.android.synthetic.main.question_header_item.view.*
import java.io.File

class QuestionHeaderViewHolder(itemView: View, private val recyclerViewClickListener: RecyclerViewListener) : RecyclerView.ViewHolder(itemView), SodisViewHolder {

    override fun bindView(sodisItem: SodisItem) {
        val questionItem = sodisItem as QuestionItem
        itemView.question_header.text = questionItem.title
        itemView.question_text.text = questionItem.questionText
        Glide.with(itemView.context).load(Uri.fromFile(File(questionItem.imageUri))).into(itemView.question_image)
    }

    override fun onClick(v: View?) {
        // do nothing
    }

}
