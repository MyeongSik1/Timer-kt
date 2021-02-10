package com.example.anew

import android.app.Activity
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var isNavigationOpen = false
    val manager = supportFragmentManager
    val fa = Fragment_stats()
    private lateinit var auth: FirebaseAuth// ...
    private var database : FirebaseDatabase? = null
   var  backKeyPressedTime : Long = 0


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()
        navigationView.setNavigationItemSelectedListener(this)
       val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragment, fa)
        transaction.addToBackStack(null)
        transaction.commit()


        if(!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance()
                .setPersistenceEnabled(true)
        }

        if (database == null) {
            database = FirebaseDatabase.getInstance()
            FirebaseDatabase.getInstance()
                .setPersistenceEnabled(true)
        }



    }







    // 툴바 사용 설정
    private fun setToolbar(){
        setSupportActionBar(toolbar)

        // 툴바 왼쪽 버튼 설정
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)  // 왼쪽 버튼 사용 여부 true
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_list)  // 왼쪽 버튼 이미지 설정
        supportActionBar!!.setDisplayShowTitleEnabled(false)    // 타이틀 안보이게 하기
    }

    // 툴바 메뉴 버튼을 설정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)       // main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
        return true
    }

    // 툴바 메뉴 버튼이 클릭 됐을 때 콜백
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // 클릭된 메뉴 아이템의 아이디 마다 when 구절로 클릭시 동작을 설정한다.
        when(item!!.itemId){
            android.R.id.home->{ // 메뉴 버튼
                drawerLayout.openDrawer(GravityCompat.START)    // 네비게이션 드로어 열기
            }
        }
        return super.onOptionsItemSelected(item)
    }


    // 네비게이션 드로어 메뉴 클릭 리스너
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){  // 네비게이션 메뉴가 클릭되면 스낵바가 나타난다.
            R.id.Timer->supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, FragmentTime()).addToBackStack(null).commit()
            R.id.stats->supportFragmentManager.beginTransaction()
                .replace(R.id.fragment,Fragment_stats()).addToBackStack(null).commit()
            R.id.text_instruction->supportFragmentManager.beginTransaction()
                .replace(R.id.fragment,Fragment_instruction()).addToBackStack(null).commit()
            R.id.logout-> loginout()
            R.id.setting -> revokeAccess()
        }
        drawerLayout.closeDrawers() // 기능을 수행하고 네비게이션을 닫아준다.
        return false
    }

    fun loginout() {
        FirebaseAuth.getInstance().signOut()
        finish()
        var aintent = Intent(this, Login::class.java)
        startActivity(aintent)
    }

    fun revokeAccess() {

        val warningBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
        warningBuilder.setTitle("회원탈퇴")
        warningBuilder.setMessage("회원탈퇴시 복구가 불가능합니다 \n 그래도 하시겠습니까?")
        warningBuilder.setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
        })
        warningBuilder.setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
            FirebaseAuth.getInstance().currentUser!!.delete()
            FirebaseAuth.getInstance().signOut()
            this.finish()
        })
            .show()
    }

    /*
     * 뒤로가기 버튼으로 네비게이션 닫기
     *
     * 네비게이션 드로어가 열려 있을 때 뒤로가기 버튼을 누르면 네비게이션을 닫고,
     * 닫혀 있다면 기존 뒤로가기 버튼으로 작동한다.
     */
    override fun onBackPressed() {
        var toast = Toast(this)
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers()
        }else{
           // super.onBackPressed()
            if(System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis()
               toast = Toast.makeText(this,"뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG)
                toast.show()
                return
            }
            if(System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                this.finish()
                toast.cancel()
            }

        }
    }


    override fun onDestroy() {         //파이어베이스 로그인 오류때문에 완전히죽임
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy()
    }
}
