package com.example.anew
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_time.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class FragmentTime: Fragment() {
    private var timeWhenStopped: Long = 0
    private var stopClicked = false
    var mutable: MutableList<String> = mutableListOf() //할일목록
    private lateinit var auth: FirebaseAuth// ...
    private var database : FirebaseDatabase? = null
    var WorkList = ArrayList<databa>()
    private var sttimer : String? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (database == null) {
            database = FirebaseDatabase.getInstance()
        }
        val view = inflater.inflate(R.layout.fragment_time, null)
        val mChronometer = view.findViewById<Chronometer>(R.id.chronometer2)
        val start_Butoon = view.findViewById<Button>(R.id.start)
        val Workadd_Button = view.findViewById<ImageView>(R.id.Workadd_Button)
        val save_Button = view.findViewById<Button>(R.id.save)
        var spinner = view.findViewById<Spinner>(R.id.work_list)
        val delbutton = view.findViewById<ImageView>(R.id.del_button)
        var text8 = view.findViewById<TextView>(R.id.textView8)



        save_Button.isClickable = false
        save_Button.isEnabled = false
        save_Button.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_reversal);
        text8.setText(LocalDate.now().toString())

        val pref2 = requireActivity().getPreferences(0) // 타이머 시간을 저장함
        val editor = pref2?.edit()





        auth = FirebaseAuth.getInstance()
        val myRef: DatabaseReference = database!!.getReference("worklist").child(auth.uid.toString())
        val myRefCh: DatabaseReference = database!!.getReference("check").child(auth.uid.toString()).child("date")
        val myRef4: DatabaseReference = database!!.getReference("timedata").child(auth.uid.toString()).child(LocalDate.now().toString())// LocalDate.now().toString()


        myRef.addValueEventListener(object : ValueEventListener {         //  작업목록을 WorkList<databa>에 add함
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                var adapter = ArrayAdapter<String>(container!!.context,android.R.layout.simple_spinner_dropdown_item)
                for (snapshot in p0.children) {
                        WorkList.add(databa((snapshot.key)!!, (snapshot.value.toString())))
                }
                for(i in WorkList.indices) {
                    Log.d("WorkListsize", WorkList.size.toString())
                        adapter.add(WorkList.get(i).value)
                }
                spinner.adapter = adapter
            }
        })


        myRefCh.addListenerForSingleValueEvent(object : ValueEventListener {       // 최종접속한 날짜와 오늘 접속한 날짜가 다를경우 최종접속한날짜로 시간을 저장할수있게
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.value.toString() == LocalDate.now().toString()) {
                    Log.d("myRefCh", "날짜가같음") // 날짜가 같다면 상관없고
                }
                else {    //for(i in WorkList.indices
                    Log.d("myRefCh", "날짜가 다름") // 날짜가 다르니 지금까지했던 기록 저장후 초기화
                    var s = spinner.adapter.count
                    for (i in 0..s-1) {
                        var sppostion = spinner.getItemAtPosition(i).toString()
                        var Stlong = pref2.getLong(sppostion, 0)
                        Log.d("sppostion", sppostion) // 목록 하나씩 가져옴
                        Log.d("Stlong", Stlong.toString())
                        var long1 : Long = 0
                        if (Stlong !== long1) {
                            val myRef3: DatabaseReference = database!!.getReference("timedata").child(auth.uid.toString()).child(p0.value.toString())
                            Data_Save(Stlong.toString(), 2, myRef3, null, null, sppostion)
                        }
                    }
                    editor?.clear()
                    editor?.commit();
                    myRefCh.setValue(LocalDate.now().toString())
                }
            }

        })

spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { // 스피너 변경시 그에따른 작업
        Log.d("spinnerclick", spinner.getItemAtPosition(position).toString())
        var spPostion = spinner.getSelectedItemPosition()
        var spPostion2 = spinner.getItemAtPosition(spPostion).toString()
        var Stlong = pref2.getLong(spPostion2, 0)
        timeWhenStopped = Stlong
        mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);

    }
}




        Workadd_Button.setOnClickListener {      //작업목록 추가 다이얼로그
            var builder = AlertDialog.Builder(context)
            builder.setTitle("목록 추가")
            var v1 = layoutInflater.inflate(R.layout.workdialog, null)
            builder.setView(v1)
            var listener = DialogInterface.OnClickListener { p0, p1 ->
                var alert = p0 as AlertDialog
                var workedit: EditText? = alert.findViewById<EditText>(R.id.work_edit)
                Data_Save("${workedit?.text}", 1, myRef, null,null,null)
            }
            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", null)
            builder.show()
        }


        start_Butoon.setOnClickListener {
            myRefCh.setValue(LocalDate.now().toString())  // 스타트버튼누를때 날짜 저장 날짜가 다를때 저장하기위함
            start_Butoon.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_reversal);
            save_Button.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_round);
            start_Butoon.isEnabled = false
            start_Butoon.isClickable = false
            save_Button.isClickable = true
            save_Button.isEnabled = true
            mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            mChronometer.start()
            sttimer = st_end_timer()
            Log.d("return", st_end_timer())
            stopClicked = false;
        }

        save_Button.setOnClickListener {
            save_Button.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_reversal);
            start_Butoon.background = ContextCompat.getDrawable(requireContext(), R.drawable.button_round);
            start_Butoon.isEnabled = true
            start_Butoon.isClickable = true
            save_Button.isClickable = false
            save_Button.isEnabled = false
            var spPostion = spinner.getSelectedItemPosition()
            timeWhenStopped = mChronometer.getBase() - SystemClock.elapsedRealtime();
            if (editor != null) {
                editor.putLong(spinner.getItemAtPosition(spPostion).toString(),timeWhenStopped).apply()
            }

            var enTimer = st_end_timer()
            Data_Save(timeWhenStopped.toString(),2,myRef4, sttimer,enTimer,spinner.getItemAtPosition(spPostion).toString())
            if (!stopClicked) {
                timeWhenStopped = mChronometer.getBase() - SystemClock.elapsedRealtime();
                Log.d("여기가", timeWhenStopped.toString())
                mChronometer.stop();
                stopClicked = true;
            }
        }



        delbutton.setOnClickListener {              // 삭제부분
            var spPostion = spinner.getSelectedItemPosition()
            var dataref : DatabaseReference = database!!.getReference("worklist").child(auth.uid.toString())
            var dataref2 : DatabaseReference = database!!.getReference("timedata").child(auth.uid.toString())
            Log.d("포지션", spPostion.toString())
            if (spPostion !== -1) {
                Log.d("WorkList-key", WorkList.get(spPostion).key)
                Log.d("WorkList-value", WorkList.get(spPostion).value)
                dataref.child(WorkList.get(spPostion).key).removeValue()
                dataref2.child(WorkList.get(spPostion).value.toString()).removeValue()
                WorkList = ArrayList<databa>()
            }




            }

        mChronometer.setOnChronometerTickListener(Chronometer.OnChronometerTickListener { chronometer ->
            val time = SystemClock.elapsedRealtime() - chronometer.base
            val h = (time / 3600000).toInt()
            val m = (time - h * 3600000).toInt() / 60000
            val s = (time - h * 3600000 - m * 60000).toInt() / 1000
            val t =
                (if (h < 10) "0$h" else h).toString() + ":" + (if (m < 10) "0$m" else m) + ":" + if (s < 10) "0$s" else s
            chronometer.text = t
        })


        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.setText("00:00:00");




        return view


    }

    fun Data_Save(item: String, i: Int, myRef: DatabaseReference, stTimer: String?, EnTimer: String?, list : String?) {
            WorkList = ArrayList<databa>()            // 초기화를 시켜주지않으면 스피너에 중복적으로 데이터가 추가됨
            Log.d("초기화", WorkList.size.toString())
        if (i == 1) {
            myRef.push().setValue(item)
           //Toast.makeText(context, "추가 완료되었습니다"+item, Toast.LENGTH_SHORT).show()
        }
        if (i == 2) {
            if (list != null) {
                myRef.child(list).setValue(item)
            }
          //  Toast.makeText(context, "저장 완료되었", Toast.LENGTH_SHORT).show()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun st_end_timer(): String {
        val startTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
        val curTime = dateFormat.format(Date())
        Log.d("테스트시간", LocalDate.now().toString())
        return curTime
    }



}





