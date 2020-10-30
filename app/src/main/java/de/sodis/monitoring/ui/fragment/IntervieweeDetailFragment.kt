package de.sodis.monitoring.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.Shader.TileMode
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import de.sodis.monitoring.*
import de.sodis.monitoring.viewmodel.IntervieweeModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_family_age_structure.view.*
import kotlinx.android.synthetic.main.view_holder_picture.view.*
import kotlinx.android.synthetic.main.view_holder_technology.view.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


class IntervieweeDetailFragment : BaseListFragment() {

    private val intervieweeModel: IntervieweeModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
            .get(IntervieweeModel::class.java)
    }

    val args: IntervieweeDetailFragmentArgs by navArgs()
    var intervieweeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intervieweeId = args.intervieweeId
        intervieweeModel.setInterviewee(intervieweeId)
        intervieweeModel.intervieweeDetail.observe(this, Observer { intervieweeD ->
            recyclerView.withModels {
                picture {
                    id("pictureHeader${intervieweeId}")
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
                            val size = Math.min(bitmap.width, bitmap.height)
                            val cropedBitmap = if (bitmap.width < bitmap.height) {
                                Bitmap.createBitmap(
                                    bitmap,
                                    0,
                                    (bitmap.height - bitmap.width) / 2,
                                    size,
                                    size
                                )
                            } else {
                                Bitmap.createBitmap(
                                    bitmap,
                                    (bitmap.width - bitmap.height) / 2,
                                    0,
                                    size,
                                    size
                                )
                            }

                            val roundedBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(resources, cropedBitmap)

                            //cut corners
                            roundedBitmapDrawable.cornerRadius = Math.min(
                                bitmap.width,
                                bitmap.height
                            ) * 0.05f
//                                    view.dataBinding.root.imageView.setImageBitmap(bitmap)
                            view.dataBinding.root.imageView.setImageDrawable(roundedBitmapDrawable)
                        } else {
                            view.dataBinding.root.imageView.setImageResource(R.drawable.ic_add_a_photo_black_24dp)//TODO add C for Carlos etc
                        }

                    }
                }

                keyValue {
                    id("keyValueVillage")
                    key("village")
                    value(intervieweeD.village.name)
                }

                if (intervieweeD.sector != null) {
                    keyValue {
                        id("keyValueSector")
                        key("sector")
                        value(intervieweeD.sector!!.name)
                        onClick { _ ->
                            activity?.let {
                                val builder = AlertDialog.Builder(it).apply {
                                    setTitle("Choose Sector")
                                    setItems(intervieweeModel.getSectorsOfVillage(intervieweeD.interviewee.villageId),
                                        DialogInterface.OnClickListener { dialog, which ->
                                            // The 'which' argument contains the index position
                                            // of the selected item
                                        })
                                }

                                val dialog = builder.create()

                                dialog.show()
                            }


                        }
                    }
                }

                keyValue {
                    id("keyValueLocalExpert")
                    key("Local Expert")
                    value(intervieweeD.user?.firstName + " " + intervieweeD.user?.lastName)
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
                familyAgeStructure {
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
                            var count =
                                view.dataBinding.root.editTextf0.text.toString().toIntOrNull() ?: 0
                            val newInterviewee = intervieweeD.interviewee.copy(girlsCount = count)
                            intervieweeModel.updateInterviewee(newInterviewee)
                        }
                        view.dataBinding.root.editTextf0.setOnFocusChangeListener { view, b ->
                            if (!b) {
                                recyclerView.requestModelBuild()
                            }
                        }
                        view.dataBinding.root.editTextf1.addTextChangedListener {
                            var count =
                                view.dataBinding.root.editTextf1.text.toString().toIntOrNull() ?: 0
                            val newInterviewee =
                                intervieweeD.interviewee.copy(youngWomenCount = count)
                            intervieweeModel.updateInterviewee(newInterviewee)
                        }
                        view.dataBinding.root.editTextf1.setOnFocusChangeListener { view, b ->
                            if (!b) {
                                recyclerView.requestModelBuild()
                            }
                        }
                        view.dataBinding.root.editTextf2.addTextChangedListener {
                            var count =
                                view.dataBinding.root.editTextf2.text.toString().toIntOrNull() ?: 0
                            val newInterviewee = intervieweeD.interviewee.copy(womenCount = count)
                            intervieweeModel.updateInterviewee(newInterviewee)
                        }
                        view.dataBinding.root.editTextf2.setOnFocusChangeListener { view, b ->
                            if (!b) {
                                recyclerView.requestModelBuild()
                            }
                        }
                        view.dataBinding.root.editTextf3.addTextChangedListener {
                            var count =
                                view.dataBinding.root.editTextf3.text.toString().toIntOrNull() ?: 0
                            val newInterviewee =
                                intervieweeD.interviewee.copy(oldWomenCount = count)
                            intervieweeModel.updateInterviewee(newInterviewee)
                        }
                        view.dataBinding.root.editTextf3.setOnFocusChangeListener { view, b ->
                            if (!b) {
                                recyclerView.requestModelBuild()
                            }
                        }
                        view.dataBinding.root.editTextm0.addTextChangedListener {
                            var count =
                                view.dataBinding.root.editTextm0.text.toString().toIntOrNull() ?: 0
                            val newInterviewee = intervieweeD.interviewee.copy(boysCount = count)
                            intervieweeModel.updateInterviewee(newInterviewee)
                        }
                        view.dataBinding.root.editTextm0.setOnFocusChangeListener { view, b ->
                            if (!b) {
                                recyclerView.requestModelBuild()
                            }
                        }
                        view.dataBinding.root.editTextm1.addTextChangedListener {
                            var count =
                                view.dataBinding.root.editTextm1.text.toString().toIntOrNull() ?: 0
                            val newInterviewee =
                                intervieweeD.interviewee.copy(youngMenCount = count)
                            intervieweeModel.updateInterviewee(newInterviewee)
                        }
                        view.dataBinding.root.editTextm1.setOnFocusChangeListener { view, b ->
                            if (!b) {
                                recyclerView.requestModelBuild()
                            }
                        }
                        view.dataBinding.root.editTextm2.addTextChangedListener {
                            var count =
                                view.dataBinding.root.editTextm2.text.toString().toIntOrNull() ?: 0
                            val newInterviewee = intervieweeD.interviewee.copy(menCount = count)
                            intervieweeModel.updateInterviewee(newInterviewee)
                        }
                        view.dataBinding.root.editTextm2.setOnFocusChangeListener { view, b ->
                            if (!b) {
                                recyclerView.requestModelBuild()
                            }
                        }
                        view.dataBinding.root.editTextm3.addTextChangedListener {
                            var count =
                                view.dataBinding.root.editTextm3.text.toString().toIntOrNull() ?: 0
                            val newInterviewee = intervieweeD.interviewee.copy(oldMenCount = count)
                            intervieweeModel.updateInterviewee(newInterviewee)
                        }
                        view.dataBinding.root.editTextm3.setOnFocusChangeListener { view, b ->
                            if (!b) {
                                recyclerView.requestModelBuild()
                            }
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
                        onClick { _ ->
                            //show surveys for the corresponding technoology
                            val action =
                                IntervieweeDetailFragmentDirections.actionIntervieweeDetailFragmentToMonitoringOverviewFragment(
                                    intervieweeId = intervieweeId,
                                    technologyId = techno.technologyId
                                )
                            findNavController().navigate(action)
                        }
                        onBind { model, view, position ->
                            //TODO ist die Zuweisung der Farben richtig?
                            val bo = BitmapFactory.Options()
                            bo.inMutable = true
                            val b = BitmapFactory.decodeResource(
                                resources,
                                when (techno.name) {
                                    "Cocina Ecologica" -> R.drawable.ofen_kreis_basic
                                    "Lavado de Manos" -> R.drawable.handwaschstation_icon
                                    "Baño" -> R.drawable.toilette_icon
                                    "Tecnología para Agua Segura (Filtro)" -> R.drawable.wasserfilter_icon

                                    else -> R.drawable.sodis_logo
                                },
                                bo
                            )
                            applyCircleGradient(b)
                            view.dataBinding.root.technolgyImage.setImageBitmap(b)

                            view.dataBinding.root.technolgyImage.setColorFilter(
                                when (techno.stateTechnology) {
                                    0 -> Color.GRAY
                                    1 -> Color.YELLOW
                                    2 -> Color.GREEN
                                    else -> Color.RED
                                }
                            )
                            view.dataBinding.root.technologyKnowledgeImage.setColorFilter(
                                when (techno.stateKnowledge) {
                                    0 -> Color.GRAY
                                    1 -> Color.YELLOW
                                    2 -> Color.GREEN
                                    else -> Color.RED
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
            view?.navigation_forward_button_1?.setImageResource(R.drawable.ic_baseline_save_24)
            view?.navigation_forward_button_1?.setOnClickListener {
                intervieweeModel.saveInterviewee()
                Snackbar.make(
                    view!!,
                    "Saved",
                    Snackbar.LENGTH_LONG
                ).show()
            }

        })
    }

    private fun applyCircleGradient(b: Bitmap) {
        val canvas = Canvas(b)
        val gradient = LinearGradient(
            0f,
            0f,
            0f,
            b.height.toFloat(),
            intArrayOf(Color.TRANSPARENT, Color.argb(70, 255, 255, 255)),
            floatArrayOf(0.0f, 1f),
            TileMode.CLAMP
        )

        val paint = Paint()
        paint.shader = gradient

        canvas.drawCircle(
            b.width.toFloat() / 2.0f,
            b.height.toFloat() / 2.0f,
            b.width.toFloat() / 2.0f,
            paint
        )
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
                //TODO set reasonable compression factor
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
        view?.navigation_forward_button_left?.isGone = true
        return view
    }
}
