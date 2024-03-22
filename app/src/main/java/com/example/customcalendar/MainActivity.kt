package com.example.customcalendar

import CalendarAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.db.EventDatabase
import com.example.model.Event
import com.example.repository.EventRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: EventViewModel

    private val lastDayInCalendar = Calendar.getInstance(Locale.ENGLISH)
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)

    private val currentDate = Calendar.getInstance(Locale.ENGLISH)
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]

    private var selectedDay: Int = currentDay
    private var selectedMonth: Int = currentMonth
    private var selectedYear: Int = currentYear

    private val dates = ArrayList<Date>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var currMonthTextView: TextView
    private lateinit var nextMonthTextView: TextView
    private lateinit var prevMonthTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newsRepository = EventRepository(EventDatabase(this))
        val viewModelProviderFactory = EventViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(EventViewModel::class.java)

        recyclerView = findViewById(R.id.calendar_recycler_view)
        currMonthTextView = findViewById(R.id.txt_current_month)
        nextMonthTextView = findViewById(R.id.calendar_next_button)
        prevMonthTextView = findViewById(R.id.calendar_prev_button)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        lastDayInCalendar.add(Calendar.MONTH, 9)

        viewModel.getEvents().observe(this, Observer<List<Event>> {events ->

            setUpCalendar(events)

            prevMonthTextView!!.setOnClickListener {
                if (cal.after(currentDate)) {
                    cal.add(Calendar.MONTH, -1)
                    if (cal == currentDate)
                        setUpCalendar(events)
                    else
                        setUpCalendar(events,changeMonth = cal)
                }
            }

            nextMonthTextView!!.setOnClickListener {
                if (cal.before(lastDayInCalendar)) {
                    cal.add(Calendar.MONTH, 1)
                    setUpCalendar(events,changeMonth = cal)
                }
            }

        })




    }

    private fun setUpCalendar(events: List<Event> = emptyList(), changeMonth: Calendar? = null) {
        // first part
        currMonthTextView!!.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        selectedDay =
            when {
                changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
                else -> currentDay
            }
        selectedMonth =
            when {
                changeMonth != null -> changeMonth[Calendar.MONTH]
                else -> currentMonth
            }
        selectedYear =
            when {
                changeMonth != null -> changeMonth[Calendar.YEAR]
                else -> currentYear
            }

        // second part
        var currentPosition = 0
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

        while (dates.size < maxDaysInMonth) {
            if (monthCalendar[Calendar.DAY_OF_MONTH] == selectedDay)
                currentPosition = dates.size
            dates.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // third part
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = layoutManager
        val calendarAdapter = CalendarAdapter(this, dates, currentDate, changeMonth, events)
        recyclerView!!.adapter = calendarAdapter

        when {
            currentPosition > 2 -> recyclerView!!.scrollToPosition(currentPosition - 3)
            maxDaysInMonth - currentPosition < 2 -> recyclerView!!.scrollToPosition(currentPosition)
            else -> recyclerView!!.scrollToPosition(currentPosition)
        }

        calendarAdapter.setOnItemClickListener(object : CalendarAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val clickCalendar = Calendar.getInstance()
                clickCalendar.time = dates[position]
                selectedDay = clickCalendar[Calendar.DAY_OF_MONTH]
            }
        })
    }
}