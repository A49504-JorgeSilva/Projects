package pt.isel

import com.fasterxml.jackson.annotation.JsonProperty

data class movie(
    @JsonProperty("id") val id: Int,
    @JsonProperty("title") val title: String,
    @JsonProperty("duration") val duration: Int,
    @JsonProperty("rating") val rating: Double)
