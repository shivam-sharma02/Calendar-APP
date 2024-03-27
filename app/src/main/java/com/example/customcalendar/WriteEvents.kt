package com.example.customcalendar

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.db.EventDatabase
import com.example.model.Event
import com.example.repository.EventRepository
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WriteEvents : AppCompatActivity() {

    lateinit var viewModel: EventViewModel
    private lateinit var alarmManager: AlarmManager

    
    private lateinit var timePicker: MaterialTimePicker
    private lateinit var calendar: Calendar

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_events)

        createNotificationChannel()

        val newsRepository = EventRepository(EventDatabase(this))
        val viewModelProviderFactory = EventViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(EventViewModel::class.java)

            val date = intent?.getStringExtra("EXTRA_DATE")

        val parts = date?.split(" ")
        val month = parts?.get(0)
        val day = parts?.get(1)
        val year = parts?.get(2)

        Log.e("RecievedDate", "$parts , $month, $day, $date")

        val currentMonth = findViewById<TextView>(R.id.currentMonth)
        val currentDate = findViewById<TextView>(R.id.currentDate)
        val selectTime = findViewById<Button>(R.id.selectTime)
        val alarmSwitch = findViewById<Switch>(R.id.alarmSwitch)
        val eventDescription = findViewById<TextInputLayout>(R.id.eventDescription)
        val selectedTime = findViewById<TextView>(R.id.timeView)
        val createEvent = findViewById<Button>(R.id.createEvent)
        val deleteEvent = findViewById<Button>(R.id.deleteEvent)

        selectTime.visibility = View.INVISIBLE
        selectedTime.visibility = View.INVISIBLE



        val currentDate2 = when(month) {
            "0" -> "January 2024"
            "1" -> "February 2024"
            "2" -> "March 2024"
            "3" -> "April 2024"
            "4" -> "May 2024"
            "5" -> "June 2024"
            "6" -> "July 2024"
            "7" -> "August 2024"
            "8" -> "September 2024"
            "9" -> "October 2024"
            "10" -> "November 2024"
            "11" -> "December 2024"

            else -> "Unkown Month"
        }

        currentMonth.text = month
        currentDate.text = day

        alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectTime.visibility = View.VISIBLE
            } else {
                selectTime.visibility = View.INVISIBLE
                selectedTime.visibility = View.INVISIBLE
            }
        }

        selectTime.setOnClickListener{
            showTimePicker()
        }

        createEvent.setOnClickListener {

            val eventDescriptionToPass = eventDescription.editText?.text.toString()
            val eventDateToPass = date

            if (eventDescriptionToPass.isNotEmpty() && !alarmSwitch.isChecked) {
                val event = Event(eventDescription = eventDescriptionToPass, eventDate = eventDateToPass)
                viewModel.saveEvent(event)
                Toast.makeText(this@WriteEvents, "Event saved successfully !!", Toast.LENGTH_SHORT).show()
            }
            else if (eventDescriptionToPass.isNotEmpty() && alarmSwitch.isChecked && selectedTime != null){

                alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReceiver::class.java).apply {
                    putExtra("EXTRA_DESCRIPTION", eventDescriptionToPass)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val formattedDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                val timeString = selectedTime.text.toString().replace(" ", "")
                val timeFormat = SimpleDateFormat("hh:mma", Locale.getDefault())

                try {
                    val time = timeFormat.parse(timeString)

                    if (time != null) {
                        val dateToSet = date?.let { date -> formattedDate.parse(date) }

                        if (dateToSet != null) {
                            calendar.time = dateToSet
                            val calTime = Calendar.getInstance()
                            calTime.time = time
                            calendar[Calendar.HOUR_OF_DAY] = calTime[Calendar.HOUR_OF_DAY]
                            calendar[Calendar.MINUTE] = calTime[Calendar.MINUTE]

                            val millis = calendar.timeInMillis
                            Log.d("AlarmDebug", "Scheduled alarm for millis: $millis")

                            alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                millis,
                                pendingIntent
                            )

                            var hour = calendar[Calendar.HOUR_OF_DAY]
                            var minute = calendar[Calendar.MINUTE]
                            val timeToSendForText = "$hour : $minute"

                            val event = Event(
                                eventDescription =  eventDescriptionToPass,
                                eventDate = eventDateToPass,
                                eventAlarm = timeToSendForText
                            )
                            viewModel.saveEvent(event)

                            Toast.makeText(this, "Event with Alarm has been added", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Log.e("AlarmDebug", "Date parsing failed")
                            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Log.e("AlarmDebug", "Time parsing failed")
                        Toast.makeText(this, "Invalid time format", Toast.LENGTH_SHORT).show()
                    }

                }
                catch (e: ParseException) {
                    Log.e("AlarmDebug", "Error parsing date or time: ${e.message}")
                    Toast.makeText(this, "Error parsing date or time", Toast.LENGTH_SHORT).show()
                }
            }

            else {
                Toast.makeText(this@WriteEvents, "Please enter event description and date!", Toast.LENGTH_SHORT).show()
            }
        }

        deleteEvent.setOnClickListener {
            viewModel.getEvents().observe(this, Observer<List<Event>> {events ->
                val eventForDeleteDate = events.find { it.eventDate == date }

                if (eventForDeleteDate != null){
                    viewModel.deleteEvent(eventForDeleteDate)

                    alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    val intent = Intent(this, AlarmReceiver::class.java)

                    val pendingIntent = PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_IMMUTABLE)

                    alarmManager.cancel(pendingIntent)
                    Toast.makeText(this, "Event deleted successfully :)", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    private fun showTimePicker() {
        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        timePicker.show(supportFragmentManager,"alarm")

        timePicker.addOnPositiveButtonClickListener {


            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = timePicker.hour
            calendar[Calendar.MINUTE] = timePicker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0

            val formattedTime = String.format(
                "%02d : %02d %s",
                if (calendar.get(Calendar.HOUR_OF_DAY) > 12) calendar.get(Calendar.HOUR_OF_DAY) - 12 else calendar.get(
                    Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                if (calendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
            )

//            binding.selectTime.text = formattedTime
            val selectedTime = findViewById<TextView>(R.id.timeView)
            selectedTime.text = formattedTime

            selectedTime.visibility = View.VISIBLE

        }


    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "event_id",
                "Event Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Notification for event"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}