package com.example.anew

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_joinmembership.*
import kotlinx.android.synthetic.main.activity_login.*

class joinmembership : AppCompatActivity() {
    private var firebaseAuth: FirebaseAuth? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joinmembership)
        firebaseAuth = FirebaseAuth.getInstance()

        member_button.setOnClickListener {
            if(join_id.text.toString().equals("") || join_id.text.toString() == null) {
            } else {
                if (join_pw.text.toString().equals("") || join_pw.text.toString() == null) {
                } else {
                    if (!join_pw.text.toString().equals(join_pwch.text.toString())) {
                        Toast.makeText(this, "비밀번호가 일치하지않습니다.", Toast.LENGTH_LONG).show()
                        join_pwch.setText("")
                    } else {
                        firebaseAuth!!.createUserWithEmailAndPassword(
                            join_id.text.toString(),
                            join_pw.text.toString()
                        )
                            .addOnCompleteListener(this) {
                                if (it.isSuccessful) {
                                    val user = firebaseAuth?.currentUser
                                    Toast.makeText(this, "회원가입 완료", Toast.LENGTH_LONG).show()
                                    finish()
                                } else {
                                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                }
            }
        }
    }
}




