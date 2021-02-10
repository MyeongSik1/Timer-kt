package com.example.anew

import Decorator.*
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.synthetic.main.fragment_stats.*
import kotlinx.android.synthetic.main.fragment_time.*
import kotlinx.android.synthetic.main.work_rv_item.*
import org.w3c.dom.Text
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList

class Fragment_stats : Fragment() {
   private var StDataList = ArrayList<Statement_data>()
    private lateinit var auth: FirebaseAuth// ...
    private var database : FirebaseDatabase? = null
    var WorktimeList = arrayListOf<WorkTime_>()
    var Timeloglist = arrayListOf<TimeLog>()
    var dateday :String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       var mAuth = FirebaseAuth.getInstance()
        val view = inflater.inflate(R.layout.fragment_stats, null)
        var calendar_view = view.findViewById<MaterialCalendarView>(R.id.main_calendar_view)
        var textsum = view.findViewById<TextView>(R.id.text_sum)
        var timelog = view.findViewById<TextView>(R.id.text_timelog)
        var daystats = view.findViewById<TextView>(R.id.textView)
        var ymd_text = view.findViewById<TextView>(R.id.textView9)
        var userName = view.findViewById<TextView>(R.id.textView4)




        var user = mAuth.currentUser?.email        // 이메일 @ 앞에만 사용
        var idx = user?.indexOf("@")
        var user_nametext = user?.substring(0, idx!!)
        userName.setText(user_nametext)


        calendar_view.state().edit()
            .setFirstDayOfWeek(Calendar.SUNDAY)
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()
        calendar_view?.addDecorators(SundayDecorator(),
            SaturdayDecorator(), BoldDecorator()
        )



        if (database == null) {
            database = FirebaseDatabase.getInstance()
        }
        auth = FirebaseAuth.getInstance()
        val myRef: DatabaseReference = database!!.getReference("timedata").child(auth.uid.toString())


        val mutablemap = mutableMapOf<String,Long>()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    for (dSnapshot in snapshot.children) {
                        var str = dSnapshot.value.toString()
                       WorktimeList.add(WorkTime_(snapshot.key,dSnapshot.ref.key,str))     //년월일 / 작업 / 시간 저장
                        StDataList.add(Statement_data(snapshot.key,str))         // 파이어베이스 데이터 Arraylist에저장
                    }
                }

                for (i in StDataList.indices) {
                    Log.d("StDataList-key", StDataList.get(i).key + "/" + StDataList.get(i).arr)
                    val map = StDataList.groupBy { it.key }
                    val YMD =
                        map[StDataList.get(i).key]!!.sumByLong { it.arr!!.toLong() } // 년월일 중복되는 데이터끼리 더하기
                    mutablemap.put(      // 더한값을 mutablemap에 put
                        StDataList.get(i).key.toString(),
                        YMD
                    )
                }

                var datelist_key = ArrayList<String>(mutablemap.keys)         // map을 arrylist로 변환
                var datelist_value = ArrayList<Long>(mutablemap.values)

                for (i in datelist_key.indices) {
                    Log.d("datelist", datelist_key.get(i) + "=" + datelist_value.get(i).toString())
                }


            var dates = ArrayList<CalendarDay>()
            for (i in datelist_key.indices) {
                var cal1 = Calendar.getInstance()
                var st1 = datelist_key.get(i)
                var st2 = st1.split("-")
                var y0 = st2[0].toInt()
                var m0 = st2[1].toInt()
                var d0 = st2[2].toInt()
                cal1.set(y0,m0-1,d0)      // 캘린더 0 ~ 11월까지기라서 -1을함
                var day = CalendarDay.from(cal1)
                dates.add(day)
                calendar_view.addDecorator(EventDecorator(Color.RED,dates,requireActivity()))   // 공부한날짜에 네모표시하기
            }

                calendar_view.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->
                    var st = date.toString()
                    var st1 = st.substring(st.lastIndexOf("{"))
                    var st2 = st1.replace("{","").replace("}","").trim()
                    var st3 = st2.split("-")
                    var y0 = st3[0]
                    var m0 = st3[1].toInt()
                    var d0 = st3[2].toInt()
                    var stkey : String? = null
                    var ymd : String ? = null
                    ymd = y0 + "년" + m0 + "월" + d0 + "일"
                    ymd_text.setText(ymd)
                    m0 += 1                      // 월은 0~11이기때문에 +1을줌
                    stkey = y0 + "-" + (if(m0 < 10) "0$m0" else m0).toString() + "-" + (if(d0 < 10) "0$d0" else d0) // 월 / 날짜는 한자릿수일때 앞에 0붙이게
                    Log.d("날짜클릭", stkey)
                    Log.d("맵키", mutablemap[stkey].toString())
                    if(mutablemap[stkey] == null) {
                        textsum.setText(" 공부 기록이 없습니다")
                        timelog.visibility = View.INVISIBLE
                        view5.visibility=View.INVISIBLE
                        mRecyclerview.visibility=View.INVISIBLE
                        val mAdapter = WorklistAdapter(requireContext(),Timeloglist,1)
                        mRecyclerview.adapter = mAdapter
                        val lm = LinearLayoutManager(requireContext())
                        mRecyclerview.layoutManager = lm
                        mRecyclerview.setHasFixedSize(true)
                    } else {
                        view5.visibility = View.VISIBLE
                        mRecyclerview.visibility=View.VISIBLE
                        Timeloglist = ArrayList()
                        timelog.visibility = View.VISIBLE
                        textView10.visibility = View.VISIBLE
                        textsum.setText(mutablemap[stkey].toString())
                        textsum.setText(timer(mutablemap[stkey]!!.toInt(),0))
                        for(i in WorktimeList.indices) {
                            if(WorktimeList.get(i).date == stkey) {
                                Timeloglist.add(TimeLog(WorktimeList.get(i).refkey,timer(WorktimeList.get(i).value!!.toInt(),i)))
                                    val mAdapter = WorklistAdapter(requireContext(),Timeloglist,2)
                                      mRecyclerview.adapter = mAdapter
                                    val lm = LinearLayoutManager(requireContext())
                                    mRecyclerview.layoutManager = lm
                                    mRecyclerview.setHasFixedSize(true)
                                mAdapter.setItemClickListener(object : WorklistAdapter.ItemClickListener {
                                    override fun onClick(view: View, position: Int) {
                                       val warningBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                        warningBuilder.setTitle("삭제 확인")
                                        warningBuilder.setMessage("삭제후 복구가 불가능합니다 \n 정말로 삭제하시겠습니까?")
                                        warningBuilder.setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                                        })
                                        warningBuilder.setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                                            Log.d("테스터확인", Timeloglist.get(position).refkey)
                                            myRef.child(stkey).child(Timeloglist.get(position).refkey.toString()).removeValue()
                                            Timeloglist.removeAt(position)
                                            mAdapter.notifyDataSetChanged()
                                        })
                                            .show()

                                    }
                                })

                            }
                        }

                    }

                })
if(mutablemap.size == 0) {             // 저장된 데이터가 없을경우
    daystats.setText("환영합니다!")
} else {
    var mapsize = mutablemap.size
    daystats.setText("$mapsize"+"일째")
}


            }













fun timer(time : Int, i : Int) : String {
    val h = (time / 3600000).toInt()
    val m = (time - h * 3600000).toInt() / 60000
    val s = (time - h * 3600000 - m * 60000).toInt() / 1000
    val a = s* -1
    val t =
        (if (h*-1 < 10) "0$h" else h).toString() + ":" + (if (m*-1 < 10) "0$m" else m) + ":" + if (s*-1 < 10) "0$s" else s
    var q = t.replace("-","")
    return q

            }





            inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {    // sumByLong 반환하기
                var sum = 0L
                for (element in this) {
                    sum += selector(element)
                }
                return sum
            }


        });



















        return view
    }




}


