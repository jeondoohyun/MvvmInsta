package com.jdh.mvvminsta.login

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jdh.mvvminsta.R

class LoginViewModel(application: Application) : AndroidViewModel(application){

    // LoginViewModel은 4가지 변수 사용. email, pw, 로그인버튼, 아이디찾기버튼


    var id : MutableLiveData<String> = MutableLiveData("")
    var password : MutableLiveData<String> = MutableLiveData("")

    var showInputNumberActivity : MutableLiveData<Boolean> = MutableLiveData(false)
    var showFindIdActivity : MutableLiveData<Boolean> = MutableLiveData(false)

    var auth = FirebaseAuth.getInstance()    // firebase 회원가입 객체(google, facebook 사용)
    var context = getApplication<Application>().applicationContext

    var googleSignInClient: GoogleSignInClient

    init {
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    // email 로그인
    fun loginWithSignupEmail() {
        println("Emial")

        // tostring()하기 전에 value 써줘야됨
        auth.createUserWithEmailAndPassword(id.value.toString(), password.value.toString()).addOnCompleteListener {   // 회원가입 완료되면 .addOnCompleteListener 이벤트 발생
            if (it.isSuccessful) {
                showInputNumberActivity.value = true
            } else {
                // 아이디가 있을경우
            }
        }
    }

    fun loginGoogle(view: View) {
        var i = googleSignInClient.signInIntent
        (view.context as? LoginActivity)?.googleLoginResult?.launch(i)  // 로그인 성공하면 LoginActivity의 googleLoginResult registerForActivityResult 여기에 이벤트 발생
    }

    // 구글 로그인
    fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { // google 로그인
            if (it.isSuccessful) {
                showInputNumberActivity.value = true
            } else {
                // 아이디가 있을경우
            }
        }
    }

    fun firebaseAuthWithFacebook(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        auth.signInWithCredential(credential).addOnCompleteListener { // facebook 로그인
            if (it.isSuccessful) {
                showInputNumberActivity.value = true
            } else {
                // 아이디가 있을경우
            }
        }
    }
}