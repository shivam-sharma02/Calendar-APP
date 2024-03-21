package com.example.customcalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar

class WriteEvents : AppCompatActivity() {

    private lateinit var timePicker: MaterialTimePicker
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_events)



        val date = intent.getStringExtra("EXTRA_DATE")
        val month = intent.getStringExtra("EXTRA_MONTH")

        val currentMonth = findViewById<TextView>(R.id.currentMonth)
        val currentDate = findViewById<TextView>(R.id.currentDate)
        val selectTime = findViewById<Button>(R.id.selectTime)
        val alarmSwitch = findViewById<Switch>(R.id.alarmSwitch)
        val selectedTime = findViewById<TextView>(R.id.timeView)


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

        currentMonth.text = currentDate2
        currentDate.text = date

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