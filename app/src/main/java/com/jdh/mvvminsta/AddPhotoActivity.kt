package com.jdh.mvvminsta

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.jdh.mvvminsta.databinding.ActivityAddPhotoBinding
import com.jdh.mvvminsta.model.ContentModel
import java.text.SimpleDateFormat
import java.util.Date

class AddPhotoActivity : AppCompatActivity() {
    var photoUri: Uri? = null   // 사진 경로
    var photoResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {     // 사진을 선택 하면 호출되는 콜백
        if(it.resultCode == RESULT_OK) {
            photoUri = it.data?.data
            binding.uploadImageview.setImageURI(photoUri)
        }
    }

    var storage = FirebaseStorage.getInstance() // firebase storage 객체 생성
    var auth = FirebaseAuth.getInstance()
    var firestore = FirebaseFirestore.getInstance()

    lateinit var binding: ActivityAddPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_photo)

        // 이 액티비티가 켜지자 마자 앨범이 열리는 기능
        var i = Intent(Intent.ACTION_PICK)
        i.type = "image/*"  // 이미지 타입
        photoResult.launch(i)   // 앨범 열려라

        binding.addphotoUploadBtn.setOnClickListener {
            contentUpload()
        }

    }

    // firebase storage에 이미지 전송. 스토리지와 db는 다른 저장소. 파이어베이스의 storage는 이미지 저장, firestore는 db
    fun contentUpload() {
        // timestamp를 포함한 이미지 이름 만들기
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + ".png"

        // storage 경로 만들기. imagesMvvm/IMAGE_20240111_130911.png
        var storagePath = storage.reference.child("imagesMvvm").child(imageFileName)

        // 이미지 업로드. 업로드된 이미지의 downloadUrl을 리턴한다.
        storagePath.putFile(photoUri!!).continueWithTask {
            return@continueWithTask storagePath.downloadUrl
        }.addOnCompleteListener {
            var contentModel = ContentModel()
            contentModel.imageUrl = it.result.toString()
            contentModel.explain = binding.addphotoEditEdittext.text.toString()
            contentModel.uid = auth.uid
            contentModel.userId = auth.currentUser?.email
            contentModel.timestamp = System.currentTimeMillis()

            firestore.collection("imagesMvvm").document().set(contentModel).addOnSuccessListener {  // db에 데이터 저장후에 콜백 받기
                Toast.makeText(this, "업로드 성공", Toast.LENGTH_SHORT).show()
                finish()
            }


        }

    }
}