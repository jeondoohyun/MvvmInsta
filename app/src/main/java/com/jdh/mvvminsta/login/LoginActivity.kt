package com.jdh.mvvminsta.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.jdh.mvvminsta.MainActivity
import com.jdh.mvvminsta.R
import com.jdh.mvvminsta.databinding.ActivityLoginBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays


class LoginActivity : AppCompatActivity() {

    // mvvm 특징 : xml에 viewModel(실시간데이터), 함수 달아 주기

    lateinit var binding: ActivityLoginBinding
//    lateinit var loginViewModel: LoginViewModel
    val loginViewModel: LoginViewModel by viewModels()  // lateinit으로 생성해서 ViewModelProvider로 초기화 하던가 byViewModel()로 생성하던가 2가지방법. implementation("androidx.activity:activity-ktx:1.8.2") 라이브러리를 추가 해야 by viewModels() 사용가능
//    lateinit var auth: FirebaseAuth     // firebase 회원가입 객체

    lateinit var callbackManager: CallbackManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // xml이랑 viewmodel 연결하기
//        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]    // 캐스팅하여 ViewModel 만들기. ViewModel 생명주기 LoginActivity와 맞추기
        binding.activity2 = this
        binding.viewModel2 = loginViewModel
        binding.lifecycleOwner = this   // binding과 LoginActivity의 생명주기를 맞춰주기
//        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()
        setObserve()
        printHashKey(this)
    }

    // loginViewModel의 showInputNumberActivity 값이 변환될때 Activity변환이 되도록 하는 함수
    fun setObserve() {
        loginViewModel.showInputNumberActivity.observe(this) {
            if (it) {
                finish()    // 기본 화면 종료
                startActivity(Intent(this, InputNumberActivity::class.java))
            }
        }

        loginViewModel.showFindIdActivity.observe(this) {
            if (it) {
                startActivity(Intent(this, FindIdActivity::class.java))     // 얘는 finish()없어도 되는 화면
            }
        }

        loginViewModel.showMainActivity.observe(this) {
            if (it) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    // 구글로그인이 성공하고 결과값 받는 함수. 이 함수는 model에서 구현이 안되기 때문에 Activity에서 사용. 자바의 ActivityResult 오버라이드
    var googleLoginResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->

        val data = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(ApiException::class.java)

        val idToken = account.idToken // 로그인한 사용자 정보를 암호화한 값
        loginViewModel.firebaseAuthWithGoogle(idToken)
    }



    // 이 함수는 xml에 dataBinding으로 연결되어 있음. logingEmail 버튼을 눌르면 showInputNumberActivity 값이 true로 바뀌면서 setObserve()의 observe()가 데이터가 바뀐것을 감지 하여 화면을 전환함.
    // 화면을 통제하는 함수는 Model이 가지고 있는것이 좋음. model에서 사용
//    fun loginWithSignupEmail() {
//        println("Emial")
//
//        // tostring()하기 전에 value 써줘야됨
//        auth.createUserWithEmailAndPassword(loginViewModel.id.value.toString(), loginViewModel.password.value.toString()).addOnCompleteListener {   // 회원가입 완료되면 .addOnCompleteListener 이벤트 발생
//            if (it.isSuccessful) {
//                loginViewModel.showInputNumberActivity.value = true
//            } else {
//                // 아이디가 있을경우
//            }
//        }
//    }

    // 페이스북 로그인 시도
    fun loginFacebook() {
        var loginManager = LoginManager.getInstance()
        loginManager.setLoginBehavior(LoginBehavior.WEB_ONLY)
        loginManager.logInWithReadPermissions(this, Arrays.asList("email"))
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {

            }

            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken
                loginViewModel.firebaseAuthWithFacebook(token)
            }

        })

    }

    fun findId() {
        println("findId")
        loginViewModel.showFindIdActivity.value = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    // facebook 해시키 구하려면 tool 다운받아서 cmd로 실행시켜야되서 복잡하기 때문에 log로 해시 구한다
    fun printHashKey(pContext: Context) {
        try {
            val info = pContext.packageManager.getPackageInfo(
                pContext.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }
}