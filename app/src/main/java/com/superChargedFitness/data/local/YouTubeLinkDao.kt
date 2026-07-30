
package com.superChargedFitness.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface YouTubeLinkDao {
    @Query("SELECT youtube_link FROM tbl_youtube_link WHERE Title = :title")
    suspend fun getYouTubeLinkByTitle(title: String): String?
}
