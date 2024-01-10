package com.jdh.mvvminsta.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


data class FindIdModel(var id: String? = null, var phoneNumber: String? = null)
class InputNumberViewModel: ViewModel() {

    var auth = FirebaseAuth.getInstance()   // 로그인 객체
    var firestore = FirebaseFirestore.getInstance()     // db 객체
    var nextPage = MutableLiveData<Boolean>(false)
    var inputNumber = ""

    fun savePhoneNumber() {
        var findIdModel = FindIdModel(auth.currentUser?.email, inputNumber)
        firestore.collection("findIds").document().set(findIdModel).addOnCompleteListener {// db 요청후 응답값 이벤트 받기. collection findIds 에 findIdModel(id, phoneNumber) 저장하기
            if(it.isSuccessful) {
                nextPage.value = true
                auth.currentUser?.sendEmailVerification()   // 너 이메일 진짜 맞냐. 이메일로 인증메일 한번더 보냄
            }
        }
    }
}