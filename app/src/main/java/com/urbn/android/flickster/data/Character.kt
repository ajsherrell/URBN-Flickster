package com.urbn.android.flickster.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "characters")
data class Character(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val details: String,
    val imageUrl: String? = null,
    var isFavorite: Boolean = false
): Parcelable

data class FlicksterResponse(
    val RelatedTopics: List<RelatedTopic>
) {
    val characters = this.RelatedTopics.mapNotNull { topic ->
        val characterName = topic.Text?.substringBefore(" -")
        val characterDetails = topic.Text?.substringAfter("- ")

        if (characterName != null && characterDetails != null) {
            Character(
                name = characterName,
                details = characterDetails.trim(),
                imageUrl = IMAGE_URL + topic.Icon.URL
            )
        } else {
            null
        }
    }
}

data class RelatedTopic(
    val FirstURL: String,
    val Icon: Icon,
    val Result: String,
    val Text: String?
)

data class Icon(
    val Height: String?,
    val URL: String?,
    val Width: String?
)
