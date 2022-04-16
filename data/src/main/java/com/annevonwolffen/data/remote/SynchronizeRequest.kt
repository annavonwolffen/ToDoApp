package com.annevonwolffen.data.remote

import com.google.gson.annotations.SerializedName

class SynchronizeRequest(
    @SerializedName("deleted") val deleted: List<String>,
    @SerializedName("other") val other: List<TaskServerModel>
)