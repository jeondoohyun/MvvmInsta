package com.jdh.mvvminsta.model

data class ContentModel(
    var explain: String? = null,    // 사진 설명
    var imageUrl: String? = null,   // 사진 주소
    var uid: String? = null,    // 유저 시퀀스값
    var userId: String? = null,   // 이메일
    var timestamp: Long? = null,   // 업로드 시간
    var favoriteCount: Int? = null,   // 좋아요 갯수
    var favorites: MutableMap<String, Boolean> = HashMap(),   // 좋아요 취소 할때 누가 어떤 유저의 좋아요를 숫자에서 빼야할지 알기 위함

) {
    // ContentModel에서 관리 하는 댓글? data class
    data class Comment(
        var uid: String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timestamp: Long? = null
    )
}
