package com.uade.ticket_mobile.data.models

import com.google.gson.annotations.SerializedName

data class Comment(
    val id: Int,
    val text: String,
    val author: User,
    @SerializedName("ticketId")
    val ticketId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("isPrivate")
    val isPrivate: Boolean
)

data class CommentListResponse(
    val count: Int,
    val comments: List<Comment>
)

data class CommentCreateRequest(
    val text: String,
    @SerializedName("isPrivate")
    val isPrivate: Boolean = false
)

data class CommentCreateResponse(
    val msg: String? = null,
    val comment: Comment
)

