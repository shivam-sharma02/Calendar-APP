package com.example.customcalendar

import android.annotation.SuppressLint
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
import java.util.Calendar

class WriteEvents : AppCompatActivity() {

    lateinit var viewModel: EventViewModel

    private lateinit var timePicker: MaterialTimePicker
    private lateinit var calendar: Calendar

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_events)

        val newsRepository = EventRepository(EventDatabase(this))
        val viewModelProviderFactory = EventViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(EventViewModel::class.java)

        val date = intent?.getStringExtra("EXTRA_DATE")

        val parts = date?.split(" ")
        val month = parts?.get(0)
        val day = parts?.get(1)

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

            if (eventDescriptionToPass.isNotEmpty()) {
                val event = Event(eventDescription = eventDescriptionToPass, eventDate = eventDateToPass)
                viewModel.saveEvent(event)
                Toast.makeText(this@WriteEvents, "Event saved successfully !!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@WriteEvents, "Please enter event description and date!", Toast.LENGTH_SHORT).show()
            }
        }

        deleteEvent.setOnClickListener {
            viewModel.getEvents().observe(this, Observer<List<Event>> {events ->
                val eventForDeleteDate = events.find { it.eventDate == date }

                if (eventForDeleteDate != null){
                    viewModel.deleteEvent(eventForDeleteDate)
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

}