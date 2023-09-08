package com.example.pruebas

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.pruebas.databinding.ActivityTimerBinding
import java.util.concurrent.TimeUnit

class TimersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimerBinding
    private lateinit var timerViewModel: TimerViewModel

    private val VIBRATE_PERMISION_CODE = 1000
    private val NOTIFICATION_PERMISSION_CODE = 1001

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (checkVibrationPermission() && checkNotificationPermission()){
            Log.d("onCreate","Permission Success")
        }else{
            if (!checkVibrationPermission()){
                requestVibratePermission()
            }
            if (!checkNotificationPermission()){
                requestNotificationPermission()
            }
        }

        timerViewModel = ViewModelProvider(this).get(TimerViewModel::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "My Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        setup()
    }

    private fun setup() {
        configureEditText()
        initListeners()
        initObservers()

    }

    //start setup config
    private fun configureEditText() {
        val editTextHours = binding.hours
        val editTextMinutes = binding.minutes

        editTextHours.setOnFocusChangeListener { view, hasfocus ->
            if (hasfocus){
                editTextHours.text.clear()
            }else{
                if (editTextHours.text.isEmpty()){
                    editTextHours.setText("00")
                }
            }
        }

        editTextMinutes.setOnFocusChangeListener { view, hasfocus ->
            if (hasfocus){
                editTextMinutes.text.clear()
            }else{
                if (editTextMinutes.text.isEmpty()){
                    editTextMinutes.setText("00")
                }
            }
        }
        editTextHours.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO){
                clearFocus()
                editTextMinutes.requestFocus()
                true
            }else{
                false
            }
        }
        editTextMinutes.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO){
                clearFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken, 0)
                true
            }else{
                false
            }
        }
    }


    private fun initListeners() {
        binding.startTemp.setOnClickListener {
            val hoursText = binding.hours.text.toString()
            val minutesText = binding.minutes.text.toString()

            val hours = if (hoursText.isEmpty()) 0 else hoursText.toInt()
            val minutes = if (minutesText.isEmpty()) 0 else minutesText.toInt()

            val totalTime  = TimeUnit.HOURS.toMillis(hours.toLong()) +
                    TimeUnit.MINUTES.toMillis(minutes.toLong())

            timerViewModel.startTimer(totalTime)
        }

        binding.cancelButton.setOnClickListener {
            timerViewModel.endTimer()
        }
    }

    private fun initObservers() {
        timerViewModel.timerLiveData.observe(this){
            binding.textViewTimeRemaining.text = it.toString()
        }
        timerViewModel.progressLiveData.observe(this){
            binding.progressTime.progress = it
        }
        timerViewModel.progressState.observe(this){state ->
            if (state){
                Toast.makeText(this, "Timer Finalizado", Toast.LENGTH_SHORT).show()
                binding.cancelButton.visibility = View.GONE
            }else{
                Toast.makeText(this, "Timer iniciado", Toast.LENGTH_SHORT).show()
                binding.cancelButton.visibility = View.VISIBLE

            }
        }
    }

    //end setup config
    private fun clearFocus() {
        binding.hours.clearFocus()
        binding.minutes.clearFocus()
    }


    //permission config start
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission(): Boolean {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        val result = ContextCompat.checkSelfPermission(this,permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun checkVibrationPermission(): Boolean {
        val permission = Manifest.permission.VIBRATE
        val result = ContextCompat.checkSelfPermission(this,permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestVibratePermission(){
        val permission = Manifest.permission.VIBRATE
        ActivityCompat.requestPermissions(this,arrayOf(permission), VIBRATE_PERMISION_CODE)
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission(){
        val permission = Manifest.permission.POST_NOTIFICATIONS
        ActivityCompat.requestPermissions(this,arrayOf(permission), NOTIFICATION_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == VIBRATE_PERMISION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permiso Aceptados", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Permiso no aceptado", Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode == NOTIFICATION_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permiso Aceptados", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Permiso no aceptado", Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    //permission config end

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        clearFocus()
    }
}