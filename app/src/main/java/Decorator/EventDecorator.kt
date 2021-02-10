package Decorator

import android.content.Context
import android.util.Log
import com.example.anew.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class EventDecorator(color : Int, dates : ArrayList<CalendarDay>, activity : Context) :
    DayViewDecorator {
    var dates : ArrayList<CalendarDay> = dates
    val drawable = activity.resources.getDrawable(R.drawable.stat)
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        Log.d("dates-contains", dates.contains(day).toString())
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setBackgroundDrawable(drawable)
    }
}