package de.sodis.monitoring.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.google.android.material.snackbar.Snackbar
import de.sodis.monitoring.*
import de.sodis.monitoring.viewmodel.PlaceViewModel
import de.sodis.monitoring.viewmodel.MyViewModelFactory
import de.sodis.monitoring.viewmodel.SurveyViewModel
import kotlinx.android.synthetic.main.continuable_list.view.*
import kotlinx.android.synthetic.main.view_holder_interviewee_overview.*
import kotlinx.android.synthetic.main.view_holder_interviewee_overview.view.*
import kotlinx.android.synthetic.main.view_holder_picture.view.*
import kotlinx.android.synthetic.main.view_holder_technology.view.*
import java.io.File


//TODO refactor redundant code
class IntervieweeOverviewFragment : BaseListFragment() {

    //val args: Args by navArgs()

    private val placeViewModel: PlaceViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
                .get(PlaceViewModel::class.java)
    }
    private val surveyViewModel: SurveyViewModel by lazy {
        ViewModelProviders.of(this, MyViewModelFactory(activity!!.application, emptyList()))
                .get(SurveyViewModel::class.java)
    }

    val args: IntervieweeOverviewFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        var villageId = args.villageId
        surveyViewModel.surveyList.observe(viewLifecycleOwner, Observer { surveyList ->
            placeViewModel.intervieweeItem.observe(viewLifecycleOwner, Observer { familyList ->
                surveyViewModel.completedSurveyList.observe(viewLifecycleOwner, Observer { completedSurveyList ->
                    recyclerView.withModels {
                        familyList.filter { it.villageId == villageId }.forEach { interviewee ->
                            intervieweeOverview {
                                id(interviewee.id)
                                text(interviewee.name)
                                onClick { clicked ->
                                    val action =
                                            IntervieweeOverviewFragmentDirections.actionIntervieweeOverviewFragmentToIntervieweeDetailFragment(
                                                    intervieweeId = interviewee.id
                                            )
                                    findNavController().navigate(action)
                                }
                                onBind { model, view, position ->
                                    val nutricionCompleted = completedSurveyList.any { it.intervieweeId == interviewee.id &&  it.surveyHeaderId == surveyList.first { it.surveyName == "Nutrición" }.surveyId }
                                    val taraSurveyCompleted = completedSurveyList.any { it.intervieweeId == interviewee.id && it.surveyHeaderId == surveyList.first { it.surveyName == "Tara" }.surveyId}
                                    val kitchenSurveyCompleted = completedSurveyList.any { it.intervieweeId == interviewee.id && it.surveyHeaderId == surveyList.first { it.surveyName == "Cocinas Ecológicas" }.surveyId}
                                    val filterSurveyCompleted = completedSurveyList.any{it.intervieweeId == interviewee.id && it.surveyHeaderId == surveyList.first{it.surveyName == "Filtro de Agua"}.surveyId}
                                    val toiletSurveyCompleted = completedSurveyList.any{it.intervieweeId == interviewee.id && it.surveyHeaderId == surveyList.first{it.surveyName == "Baños"}.surveyId}
                                    val washingSurveyCompleted = completedSurveyList.any{it.intervieweeId == interviewee.id && it.surveyHeaderId == surveyList.first{it.surveyName == "Lavado de manos"}.surveyId}


                                    view.dataBinding.root.imageViewToilet.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (toiletSurveyCompleted) R.color.colorGreen500 else R.color.colorGrey500))
                                    view.dataBinding.root.imageViewKitchen.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (kitchenSurveyCompleted) R.color.colorGreen500 else R.color.colorGrey500))
                                    view.dataBinding.root.imageViewFilter.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (filterSurveyCompleted) R.color.colorGreen500 else R.color.colorGrey500))
                                    view.dataBinding.root.imageViewWashing.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (washingSurveyCompleted) R.color.colorGreen500 else R.color.colorGrey500))
                                    view.dataBinding.root.imageViewTara.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (taraSurveyCompleted) R.color.colorGreen500 else R.color.colorGrey500))
                                    view.dataBinding.root.imageViewNutricion.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, if (nutricionCompleted) R.color.colorGreen500 else R.color.colorGrey500))

                                }
                            }
                        }
                    }
                })
            })
        })






        view?.navigation_forward_button_1?.isGone = false
        view?.navigation_forward_button_1?.setImageResource(R.drawable.ic_person_add_black_24dp)
        view?.navigation_forward_button_1?.setOnClickListener {
            //show add new family dialog
            // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
            val builder: AlertDialog.Builder? = activity?.let {
                AlertDialog.Builder(it)
            }
            val edittext = EditText(activity as MainActivity)
            builder?.setTitle(getString(R.string.add_new_family_dialog_title))
            builder?.setMessage(getString(R.string.enter_family_name))
            builder?.setView(edittext)

            builder?.setPositiveButton(getString(R.string.save),
                    DialogInterface.OnClickListener { dialog, whichButton -> //What ever you want to do with the value
                        //todo create new family and technologies
                        if (edittext.text.isEmpty()) {
                            Snackbar.make(
                                    view,
                                    getString(R.string.name_empty_message),
                                    Snackbar.LENGTH_LONG
                            ).show()
                        } else {
                            placeViewModel.createInterviewee(
                                    name = edittext.text.toString(),
                                    village = args.villageId
                            )
                        }

                    })

            builder?.setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        // what ever you want to do with No option.
                    })


// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
            val dialog: AlertDialog? = builder?.create()
            dialog?.show()
        }
        view?.navigation_forward_button_left?.isGone = true
        view?.navigation_cancel_button?.isGone = true
        view?.navigation_cancel_button?.isGone = true
        (activity as MainActivity).show_bottom_navigation()
        return view
    }
}