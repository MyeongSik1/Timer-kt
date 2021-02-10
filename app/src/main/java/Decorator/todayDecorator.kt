package Decorator

import android.content.Context
import android.util.Log
import com.example.anew.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class todayDecorator(context : Context) : DayViewDecorator {
    private var date = CalendarDay.today()
    val drawable = context.resources.getDrawable(R.drawable.stat)
    /**
     * Determine if a specific day should be decorated
     *
     * @param day [CalendarDay] to possibly decorate
     * @return true if this decorator should be applied to the provided day
     */
    override fun shouldDecorate(day: CalendarDay): Boolean {
        var ts = CalendarDay(2021, 0, 20)
        var ta = CalendarDay(2021, 0, 18)
        var st = "CalendarDay{2021-0-20}"
        Log.d("투데이", ts.toString() + "/" + date.toString())
        return day?.equals(ts)
    }

    /**
     * Set decoration options onto a facade to be applied to all relevant days
     *
     * @param view View to decorate
     */
    override fun decorate(view: DayViewFacade?) {
        view?.setBackgroundDrawable(drawable)
    }
}