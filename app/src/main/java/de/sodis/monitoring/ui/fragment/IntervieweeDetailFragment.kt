package de.sodis.monitoring.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.sodis.monitoring.*
import de.sodis.monitoring.viewmodel.IntervieweeModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_family_age_structure.view.*
import kotlinx.android.synthetic.main.view_holder_picture.view.*
import kotlinx.android.synthetic.main.view_holder_technology.view.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


class IntervieweeDetailFragment(private val intervieweeId: Int) : BaseListFragment() {

    private val intervieweeModel: IntervieweeModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(IntervieweeModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intervieweeModel.setInterviewee(intervieweeId)
        intervieweeModel.intervieweeDetail.observe(this, Observer { intervieweeD ->
            recyclerView.withModels {
                header {
                    id("header")
                    text(intervieweeD.interviewee.name)
                }
                picture {
                    id("picture${intervieweeId}")
                    onClick { _ ->
                        dispatchTakePictureIntent()
                    }
                    onBind { model, view, position ->
                        val bitmapdata = try {
                            context!!.openFileInput("interviewee_${intervieweeId}.jpg").readBytes()
                        } catch (ex: FileNotFoundException) {
                            null
                        }
                        if (bitmapdata != null) {
                            val bitmap =
                                BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.size)
                            view.dataBinding.root.imageView.setImageBitmap(bitmap)
                        } else {
                            view.dataBinding.root.imageView.setImageResource(R.drawable.ic_emoji_happy)
                        }

                    }

                }

                keyValue {
                    id("keyValueVillage")
                    key("village")
                    value(intervieweeD.village.name)
                }
                keyValue {
                    id("keyValueCount")
                    key("miembro de la familia")
                    value(
                        (intervieweeD.interviewee.boysCount
                                + intervieweeD.interviewee.girlsCount
                                + intervieweeD.interviewee.youngMenCount
                                + intervieweeD.interviewee.youngWomenCount
                                + intervieweeD.interviewee.womenCount
                                + intervieweeD.interviewee.menCount
                                + intervieweeD.interviewee.oldWomenCount
                                + intervieweeD.interviewee.oldMenCount).toString()
                    )
                }
                familyAgeStructure{
                    id("family")
                    f0(intervieweeD.interviewee.girlsCount.toString())
                    f1(intervieweeD.interviewee.youngWomenCount.toString())
                    f2(intervieweeD.interviewee.womenCount.toString())
                    f3(intervieweeD.interviewee.oldWomenCount.toString())
                    m0(intervieweeD.interviewee.boysCount.toString())
                    m1(intervieweeD.interviewee.youngMenCount.toString())
                    m2(intervieweeD.interviewee.menCount.toString())
                    m3(intervieweeD.interviewee.oldMenCount.toString())
                    onBind { model, view, position ->

                        view.dataBinding.root.editTextf0.addTextChangedListener {
                            var count = view.dataBinding.root.editTextf0.text.toString().toIntOrNull() ?: 0
                            val newInterviewee = intervieweeD.interviewee.copy(girlsCount = count)
                                intervieweeModel.updateInterviewee(newInterviewee)
                        }
                    }


                }
                intervieweeD.intervieweeTechnologies.forEach { techno ->
                    //are there open tasks for this technology?
                    val taskFilteredList = intervieweeD.tasks.filter { task ->
                        task.intervieweeTechnologyId == techno.id
                    }
                    var taskStatus: String? = null
                    if (taskFilteredList.isNotEmpty()) {
                        taskStatus = taskFilteredList.first().name!!
                    }
                    taskFilteredList ?: "All good"
                    technology {
                        id("technology")
                        state(techno.stateTechnology.toString())
                        knowledgeState(techno.stateKnowledge.toString())
                        name(techno.name)
                        taskName(taskStatus ?: "")
                        onBind { model, view, position ->
                            //TODO ist die Zuweisung der Farben richtig?
                            view.dataBinding.root.technolgyImage.setColorFilter(
                                when (techno.stateTechnology) {
                                    0 -> Color.GREEN
                                    1 -> Color.RED
                                    2 -> Color.YELLOW
                                    else -> Color.GRAY
                                }
                            )
                            view.dataBinding.root.technologyKnowledgeImage.setColorFilter(
                                when (techno.stateKnowledge) {
                                    0 -> Color.GREEN
                                    1 -> Color.RED
                                    2 -> Color.YELLOW
                                    else -> Color.GRAY
                                }
                            )
                            view.dataBinding.root.technologyTaskImage.setColorFilter(Color.YELLOW)
                            if (taskStatus == null) {
                                view.dataBinding.root.technologyTaskImage.visibility = View.GONE
                            } else {
                                view.dataBinding.root.technologyTaskImage.visibility = View.VISIBLE
                            }

                        }
                    }
                }

            }
            recyclerView.recycledViewPool.clear()
        })
    }

    val REQUEST_IMAGE_CAPTURE = 1

    protected fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data!!.extras.get("data") as Bitmap
            activity!!.applicationContext.openFileOutput(
                "interviewee_${intervieweeId}.jpg",
                Context.MODE_PRIVATE
            ).use {
                val blob = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, blob)
                it.write(blob.toByteArray())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.navigation_forward_button_1?.isGone = true
        return view
    }
}
