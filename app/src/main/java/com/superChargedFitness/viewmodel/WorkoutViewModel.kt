package com.superChargedFitness.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.superChargedFitness.data.local.AppDatabase
import com.superChargedFitness.data.repository.WorkoutRepository
import com.superChargedFitness.pojo.PWorkOutDetails
import com.superChargedFitness.utils.CommonUtility
import com.superChargedFitness.utils.ConstantString
import kotlinx.coroutines.launch

/**
 * ViewModel for WorkoutActivity.
 * Manages timer state, current workout position, sound preferences,
 * and video link lookups — all previously embedded in WorkoutActivity fields.
 */
class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository by lazy {
        WorkoutRepository(AppDatabase.getDatabase(application))
    }

    // Timer state
    private var totalSeconds = 0
    private val _timerText = MutableLiveData("00:00")
    val timerText: LiveData<String> = _timerText

    private val _isTimerPaused = MutableLiveData(false)
    val isTimerPaused: LiveData<Boolean> = _isTimerPaused

    // Sound state
    private val _isSoundOn = MutableLiveData(true)
    val isSoundOn: LiveData<Boolean> = _isSoundOn

    // Video link result
    private val _videoLink = MutableLiveData<String>()
    val videoLink: LiveData<String> = _videoLink

    fun incrementTimer() {
        if (_isTimerPaused.value != true) {
            totalSeconds++
            _timerText.value = CommonUtility.secToTime(totalSeconds)
        }
    }

    fun getTimerText(): String = _timerText.value ?: "00:00"

    fun pauseTimer() {
        _isTimerPaused.value = true
    }

    fun resumeTimer() {
        _isTimerPaused.value = false
    }

    fun toggleSound(): Boolean {
        val newValue = !(_isSoundOn.value ?: true)
        _isSoundOn.value = newValue
        return newValue
    }

    fun setSoundOn(on: Boolean) {
        _isSoundOn.value = on
    }

    fun loadVideoLink(workoutTitle: String) {
        viewModelScope.launch {
            try {
                val link = repository.getVideoLink(workoutTitle)
                _videoLink.postValue(link)
            } catch (e: Exception) {
                _videoLink.postValue("")
            }
        }
    }
}
