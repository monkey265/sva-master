package com.svamaster.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SvaProgressManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sva_user_progress", Context.MODE_PRIVATE)

    private val _currentChallengeIndex = MutableStateFlow(prefs.getInt(KEY_CURRENT_INDEX, 0))
    val currentChallengeIndex: StateFlow<Int> = _currentChallengeIndex.asStateFlow()

    private val _solvedIds = MutableStateFlow(prefs.getStringSet(KEY_SOLVED_IDS, emptySet())?.toSet() ?: emptySet())
    val solvedIds: StateFlow<Set<String>> = _solvedIds.asStateFlow()

    fun setCurrentChallengeIndex(index: Int) {
        _currentChallengeIndex.value = index
        prefs.edit().putInt(KEY_CURRENT_INDEX, index).apply()
    }

    fun markChallengeSolved(challengeId: String) {
        val updated = _solvedIds.value + challengeId
        _solvedIds.value = updated
        prefs.edit().putStringSet(KEY_SOLVED_IDS, updated).apply()
    }

    fun resetProgress() {
        _currentChallengeIndex.value = 0
        _solvedIds.value = emptySet()
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_CURRENT_INDEX = "pref_current_challenge_index"
        private const val KEY_SOLVED_IDS = "pref_solved_challenge_ids"

        @Volatile
        private var instance: SvaProgressManager? = null

        fun getInstance(context: Context): SvaProgressManager {
            return instance ?: synchronized(this) {
                instance ?: SvaProgressManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
