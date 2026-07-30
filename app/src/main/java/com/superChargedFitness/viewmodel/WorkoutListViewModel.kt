package com.superChargedFitness.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.superChargedFitness.data.local.AppDatabase
import com.superChargedFitness.data.repository.WorkoutRepository
import com.superChargedFitness.pojo.PWorkOutDetails
import kotlinx.coroutines.launch

/**
 * ViewModel for WorkoutListActivity.
 * Loads workout details for a given category table via the repository.
 */
class WorkoutListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository by lazy {
        WorkoutRepository(AppDatabase.getDatabase(application))
    }

    private val _workoutDetails = MutableLiveData<ArrayList<PWorkOutDetails>>()
    val workoutDetails: LiveData<ArrayList<PWorkOutDetails>> = _workoutDetails

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadWorkoutDetails(tableName: String) {
        viewModelScope.launch {
            try {
                val details = repository.getWorkoutDetails(tableName)
                _workoutDetails.postValue(details)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}
