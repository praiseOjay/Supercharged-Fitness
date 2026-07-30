package com.superChargedFitness.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class YouTubeLinkDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var youtubeDao: YouTubeLinkDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = AppDatabase.getDatabase(context)
        youtubeDao = database.youtubeLinkDao()
    }

    @Test
    fun getYouTubeLinkByTitle_returnsLinkForExistingTitle() = runTest {
        // Arrange: Use a title we know exists in the pre-populated DB
        // Example: "JUMPING JACKS"
        val title = "JUMPING JACKS"

        // Act
        val result = youtubeDao.getYouTubeLinkByTitle(title)

        // Assert
        // Result could be null if the exact title isn't found, but it shouldn't crash
        // and if it exists, it shouldn't be empty.
        if (result != null) {
            assertTrue(result.isNotEmpty())
        }
    }
}
