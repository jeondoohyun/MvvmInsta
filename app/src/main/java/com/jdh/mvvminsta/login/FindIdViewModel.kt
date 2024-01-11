package com.jdh.mvvminsta.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FindIdViewModel: ViewModel() {

    var auth = FirebaseAuth.getInstance()
    var firestore = FirebaseFirestore.getInstance()
    var id = ""
    var phoneNumber = ""
    var toastMessage = MutableLiveData("")


    // 원래 파이어베이스 db 접근 하려면 파이어베이스 인증 하고 난다음에 접근해야되는데 id 찾는건 로그인 없이도 진행이 가능해야 하기 때문에
    fun findMyId() {
        firestore.collection("findIds").whereEqualTo("phoneNumber", phoneNumber).get().addOnCompleteListener {
            if(it.isSuccessful && it.result.documents.size > 0) {
                // it.result.document 로 넘어오는 데이터는 배열이라서 조건에 맞았을때 첫번째것을 가져와야 찾는 데이터가 옴
                var findIdModel = it.result.documents.first().toObject(FindIdModel::class.java) // FindIdModel 로 캐스팅
                toastMessage.value = "당신의 id는 " + findIdModel?.id + "입니다"
            } else {
                toastMessage.value = "정보가 정확하지 않습니다"
            }
        }


    }

    // db 이용하지 않고 email을 이용한 비밀번호 리셋
    fun findMyPassword() {
        if (id.isNotEmpty()) {
            auth.sendPasswordResetEmail(id).addOnCompleteListener {
                if(it.isSuccessful) {
                    toastMessage.value = "비밀번호를 초기화 했습니다"
                } else {
                    toastMessage.value = "정보가 정확하지 않습니다"
                }
            }
        }
    }
}