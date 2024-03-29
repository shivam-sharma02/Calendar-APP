import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.CalendarContract.Events
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.customcalendar.R
import com.example.customcalendar.WriteEvents
import com.example.model.Event
import java.sql.RowId
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Month
import java.util.Calendar
import java.util.Calendar.MONTH
import java.util.Date
import java.util.Locale

class CalendarAdapter(private val context: Context,
                      private val data: ArrayList<Date>,
                      private val currentDate: Calendar,
                      private val changeMonth: Calendar?,
                      private val events: List<Event> ): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {
    private var mListener: OnItemClickListener? = null
    private var index = -1
    private var selectCurrentDate = true
    private val currentMonth = currentDate[Calendar.MONTH]
    private val currentYear = currentDate[Calendar.YEAR]
    private val currentDay = currentDate[Calendar.DAY_OF_MONTH]
    private val selectedDay =
        when {
            changeMonth != null -> changeMonth.getActualMinimum(Calendar.DAY_OF_MONTH)
            else -> currentDay
        }
    private val selectedMonth =
        when {
            changeMonth != null -> changeMonth[Calendar.MONTH]
            else -> currentMonth
        }
    private val selectedYear =
        when {
            changeMonth != null -> changeMonth[Calendar.YEAR]
            else -> currentYear
        }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.custom_calendar, parent, false), mListener!!)
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss", Locale.ENGLISH)
        val cal = Calendar.getInstance()
        cal.time = data[position]

        val displayMonth = cal[Calendar.MONTH]
        val displayYear= cal[Calendar.YEAR]
        val displayDay = cal[Calendar.DAY_OF_MONTH]

        try {
            val dayInWeek = sdf.parse(cal.time.toString())!!
            sdf.applyPattern("EEE")
            holder.txtDayInWeek!!.text = sdf.format(dayInWeek).toString()
        } catch (ex: ParseException) {
            Log.v("Exception", ex.localizedMessage!!)
        }
        holder.txtDay!!.text = cal[Calendar.DAY_OF_MONTH].toString()
        // we will add onClickListener here


        if (displayYear >= currentYear)
            if (displayMonth >= currentMonth || displayYear > currentYear)
                if (displayDay >= currentDay || displayMonth > currentMonth || displayYear > currentYear) {
                    holder.linearLayout!!.setOnClickListener {
                        index = position
                        selectCurrentDate = false
                        holder.listener.onItemClick(position)
                        notifyDataSetChanged()
                        writeEvent(holder)
                    }

                    if (index == position)
                        makeItemSelected(holder)

                    else {
                        if (displayDay == selectedDay
                            && displayMonth == selectedMonth
                            && displayYear == selectedYear
                            && selectCurrentDate)
                            makeItemSelected(holder)
                        else
                            makeItemDefault(holder)
                    }
                } else makeItemDisabled(holder)
            else makeItemDisabled(holder)
        else makeItemDisabled(holder)

        val sdfCompare = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
        val formattedDateCompare = sdfCompare.format(cal.time)

        val eventForDate = events.find { it.eventDate == formattedDateCompare }

        if (eventForDate != null) {
            holder.eventContent.text = eventForDate.eventDescription
            if (eventForDate.eventAlarm != null) {
                holder.alarmTime.text = eventForDate.eventAlarm
            }
        } else {
            holder.eventContent.text = " "
        }
    }

    inner class ViewHolder(itemView: View,val listener: OnItemClickListener): RecyclerView.ViewHolder(itemView) {
        var txtDay = itemView.findViewById<TextView>(R.id.txt_date)
        var txtDayInWeek = itemView.findViewById<TextView>(R.id.txt_day)
        var eventContent = itemView.findViewById<TextView>(R.id.eventContent)
        var alarmTime = itemView.findViewById<TextView>(R.id.alarmTime)
        var linearLayout = itemView.findViewById<LinearLayout>(R.id.calendar_linear_layout)
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    private fun makeItemDisabled(holder: ViewHolder) {
        holder.txtDay!!.setTextColor(ContextCompat.getColor(context, R.color.ThemeColor2))
        holder.txtDayInWeek!!.setTextColor(ContextCompat.getColor(context, R.color.ThemeColor2))  // theme color 2
        holder.linearLayout!!.setBackgroundColor(Color.WHITE)
        holder.linearLayout!!.isEnabled = false
    }

    private fun makeItemSelected(holder: ViewHolder) {
        holder.txtDay!!.setTextColor(Color.parseColor("#FFFFFF"))
        holder.txtDayInWeek!!.setTextColor(Color.parseColor("#FFFFFF"))
        holder.linearLayout!!.setBackgroundResource(R.drawable.background_top) // theme color 1
        holder.linearLayout!!.isEnabled = false
    }

    private fun makeItemDefault(holder: ViewHolder) {
        holder.txtDay!!.setTextColor(Color.BLACK)
        holder.txtDayInWeek!!.setTextColor(Color.BLACK)
        holder.linearLayout!!.setBackgroundColor(Color.WHITE)
        holder.linearLayout!!.isEnabled = true
    }

    private fun writeEvent(holder: ViewHolder) {

//        if (holder.adapterPosition >= 0 && holder.adapterPosition < data.size){

//            val selectedDate = data[holder.adapterPosition]
//
//            // Format the selected date to string
//            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//            val formattedDate = sdf.format(selectedDate)
//
//            // Open WriteEvent activity with the selected date information
//            val intent = Intent(context, WriteEvents::class.java)
//            intent.putExtra("selected_date", formattedDate)
//            context.startActivity(intent)
            val date = holder.txtDay.text.toString()
            val month = selectedMonth.toString()

            val cal = Calendar.getInstance()
            cal.set(Calendar.MONTH, selectedMonth)
            cal.set(Calendar.DAY_OF_MONTH, holder.txtDay.text.toString().toInt())

            val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
            val formattedDate = sdf.format(cal.time)

            val intent = Intent(context, WriteEvents::class.java)
            intent.putExtra("EXTRA_DATE", formattedDate)
//            intent.putExtra("EXTRA_MONTH", month)
            Log.e("intentExtra", "intent extra date which is $date has been sent and month is $month")
            context.startActivity(intent)

//        }

    }
}