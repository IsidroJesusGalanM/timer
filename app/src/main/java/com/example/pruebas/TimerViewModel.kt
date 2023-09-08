package com.example.pruebas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.CountDownTimer
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit

class TimerViewModel:ViewModel() {
    private var countDownTimer: CountDownTimer? = null

    //var textView
    private var _timerLiveData = MutableLiveData<String>()
    val timerLiveData: LiveData<String>
        get() = _timerLiveData

    //var of progress bar
    private var _progressLiveData = MutableLiveData<Int>()
    val progressLiveData: LiveData<Int>
        get() = _progressLiveData

    private var _progressFinish = MutableLiveData<Boolean>()
    val progressState: LiveData<Boolean>
        get() = _progressFinish

    fun startTimer (totalTime: Long) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(totalTime, 1000){

            override fun onTick(timeUntilFinish: Long) {
                val hoursRemaining = TimeUnit.MILLISECONDS.toHours(timeUntilFinish)
                val minutesRemaining = TimeUnit.MILLISECONDS.toMinutes(timeUntilFinish) %60
                val secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(timeUntilFinish) %60

                val formattedTimeToString = String.format("%02d:%02d:%02d",hoursRemaining,minutesRemaining,secondsRemaining)
                _timerLiveData.postValue(formattedTimeToString)

                val progress = (timeUntilFinish.toFloat() / totalTime.toFloat() * 100).toInt()
                _progressLiveData.postValue(progress)

            }

            override fun onFinish() {
                endTimer()
            }
        }
        countDownTimer?.start()
        _progressFinish.postValue(false)

    }

    fun endTimer() {
        _progressFinish.postValue(true)
        _timerLiveData.postValue("00:00:00")
        _progressLiveData.postValue(0)
        countDownTimer?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}