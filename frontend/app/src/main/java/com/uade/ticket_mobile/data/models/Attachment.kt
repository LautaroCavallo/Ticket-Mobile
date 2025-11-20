package com.uade.ticket_mobile.data.models

import com.google.gson.annotations.SerializedName

data class Attachment(
    val id: Int,
    @SerializedName("originalFilename")
    val originalFilename: String,
    @SerializedName("fileUrl")
    val fileUrl: String,
    @SerializedName("fileSize")
    val fileSize: Int,
    @SerializedName("mimeType")
    val mimeType: String,
    @SerializedName("uploadedBy")
    val uploadedBy: User,
    @SerializedName("ticketId")
    val ticketId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("isPrivate")
    val isPrivate: Boolean
)

data class AttachmentListResponse(
    val count: Int,
    val attachments: List<Attachment>
)

data class AttachmentUploadResponse(
    val msg: String,
    val attachment: Attachment
)

