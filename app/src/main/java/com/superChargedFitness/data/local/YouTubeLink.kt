
package com.superChargedFitness.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_youtube_link")
data class YouTubeLink(
    @PrimaryKey val id: Int?,
    val youtube_link: String?,
    @ColumnInfo(name = "Title") val title: String?
)

