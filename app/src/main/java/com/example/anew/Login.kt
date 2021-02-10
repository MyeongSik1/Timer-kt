package com.example.anew

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {
    private var firebaseAuth : FirebaseAuth? = null;
    val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
       var user : FirebaseUser? = FirebaseAuth.getInstance().currentUser        // 로그인 유지
        if(user !=null) {
                startActivity(Intent(this, MainActivity::class.java))
                  finish()
        } else {

        }
        firebaseAuth = FirebaseAuth.getInstance()
        var gso : GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // 구글로그인
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        google_login.setOnClickListener {                            // 구글로그인
            var signInIntent : Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }


        join.setOnClickListener {         //회원가입
            val memberShip = Intent(this, joinmembership::class.java)
            startActivity(memberShip)
        }


        login_button.setOnClickListener {        //이메일로그인
            if(login_id.text.toString().equals("") || login_id.text.toString() == null) {     // 아이디 or 패스워드가 null일경우
            } else {
                if (login_pw.text.toString().equals("") || login_pw.text.toString() == null) {
                } else {
                firebaseAuth!!.signInWithEmailAndPassword(
                    login_id.text.toString(),
                    login_pw.text.toString()
                )
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            var idToken: String? = null;
                            val mUser2 = FirebaseAuth.getInstance().currentUser
                            mUser2!!.getIdToken(true)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        idToken = task.result!!.token
                                        val nextIntent = Intent(this, MainActivity::class.java)
                                        val pref =
                                            getSharedPreferences("pref", Context.MODE_PRIVATE)
                                        val editor = pref.edit()
                                        editor.putString("Token", idToken).apply()
                                        nextIntent.putExtra("토큰", idToken)
                                        startActivity(nextIntent)
                                        finish()
                                    }
                                }


                        } else {
                            Toast.makeText(this, "실패", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }





    }

    fun fun_MoveNextPage() { // 구글로그인
        var currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
        }
    }

    fun fun_FirebaseAuthWithGoogle(acct : GoogleSignInAccount?) { // 구글로그인
        var credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                fun_MoveNextPage()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN) {              // 구글로그인
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)
                fun_FirebaseAuthWithGoogle(account)
            }
            catch (e: ApiException){

            }
        }
   else {
        }
    }



    }



