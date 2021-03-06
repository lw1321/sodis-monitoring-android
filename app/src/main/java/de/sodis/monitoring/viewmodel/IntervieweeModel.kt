package de.sodis.monitoring.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel


class IntervieweeModel(application: Application) : AndroidViewModel(application) {
/*
    lateinit var familyCount: MutableLiveData<Int>
    var intervieweeList: LiveData<List<Interviewee>>
    var villageList: LiveData<List<Village>>
    var intervieweeDetail: MutableLiveData<IntervieweeDetail>
    lateinit var technologyList: LiveData<List<IntervieweeTechnology>>
    var modiefied: Boolean = false
    var currentIntervieweeId: String = "0"
    var villageName: MutableLiveData<String>

    private val monitoringDatabase = MonitoringDatabase.getDatabase(application.applicationContext)

    private val todoPointRepository = TodoPointRepository(monitoringDatabase.todoPointDao(), MonitoringApi())

    init {
        intervieweeList = intervieweeRepository.getAll()
        villageList = intervieweeRepository.getAllVillages()
        intervieweeDetail = MutableLiveData()
        villageName = MutableLiveData()
        villageName.postValue("")
    }

    fun checkChangeTodoPoint(todoPoint: TodoPoint) {
        var toSet = todoPoint
        if(!todoPoint.done!!) {
            toSet.done=true
            toSet.duedate = Calendar.getInstance()
        }
        else {
            toSet.done=false
            toSet.duedate = null
        }
        todoPointRepository.updateTodoPoint(
            todoPoint = toSet
        )
    }

    fun getByVillage(villageId: Int): LiveData<List<Interviewee>> {
        return intervieweeRepository.getByVillage(villageId)
    }

    fun setInterviewee(intervieweeId: String) {
        currentIntervieweeId = intervieweeId
        intervieweeRepository.getFamilyCount(intervieweeId).observeForever(Observer {
            if (intervieweeDetail.value != null) {
                val value = intervieweeDetail.value
                value!!.interviewee.menCount = it
                intervieweeDetail.postValue(value)
            }
        })
        intervieweeRepository.getTechnologies(intervieweeId)
            .observeForever(Observer { updatedTechList ->
                //update the intervieweedetails..
                if (intervieweeDetail.value != null) {
                    val value = intervieweeDetail.value
                    value!!.intervieweeTechnologies.forEach { oldTechList ->
                        //update states
                        val newTechno =
                            updatedTechList.first { techno -> techno.id == oldTechList.id }
                        oldTechList.stateKnowledge = newTechno.stateKnowledge
                        oldTechList.stateTechnology = newTechno.stateTechnology
                    }
                    intervieweeDetail.postValue(value)
                }
            })
        todoPointRepository.getUndonePointsOfFamily(intervieweeId).observeForever {
            if(intervieweeDetail.value!=null) {
                val value = intervieweeDetail.value
                value!!.todoPoints = it;
                intervieweeDetail.postValue(value)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            intervieweeDetail.postValue(intervieweeRepository.getById(intervieweeId = intervieweeId))
        }

    }

    fun getByID(intervieweeId: String): Interviewee {
        return intervieweeRepository.getIntervieweeByID(intervieweeId)
    }

    fun updateInterviewee(interviewee: Interviewee) {
        intervieweeDetail.value!!.interviewee = interviewee
        modiefied = true
    }


    fun getVillageByID(int: Int): Village {
        return intervieweeRepository.getVillageByID(int)
    }

    fun storeImagePath(currentPhotoPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            intervieweeRepository.updateImagePath(
                intervieweeDetail.value!!.interviewee.id,
                currentPhotoPath
            )
        }
    }

    fun requestVillageName(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            var name = intervieweeRepository.getVillageName(id)
            villageName.postValue(name)
        }
    }

    fun createInterviewee(name: String, village: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            intervieweeRepository.createInterviewee(name, village)
        }
    }*/
}
