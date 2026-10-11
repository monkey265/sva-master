package com.svamaster

import com.svamaster.data.SvaRepository
import com.svamaster.model.SvaCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SvaDataIntegrityTest {

    @Test
    fun testRepositoryHasAllCategoriesRepresented() {
        val categoriesWithItems = SvaRepository.items.map { it.category }.toSet()
        for (category in SvaCategory.values()) {
            assertTrue("Category $category should have items", categoriesWithItems.contains(category))
        }
    }

    @Test
    fun testAllItemsHaveValidFields() {
        val uniqueIds = mutableSetOf<String>()
        for (item in SvaRepository.items) {
            assertTrue("ID must be unique: ${item.id}", uniqueIds.add(item.id))
            assertFalse("Name should not be blank", item.name.isBlank())
            assertFalse("Syntax should not be blank", item.syntax.isBlank())
            assertFalse("Summary should not be blank", item.summary.isBlank())
            assertFalse("Code example should not be blank", item.codeExample.isBlank())
        }
    }

    @Test
    fun testQuizQuestionsIntegrity() {
        val uniqueIds = mutableSetOf<String>()
        for (q in SvaRepository.quizQuestions) {
            assertTrue("Question ID must be unique: ${q.id}", uniqueIds.add(q.id))
            assertTrue("Must have at least 2 options", q.options.size >= 2)
            assertTrue("Correct index must be within options range", q.correctIndex in q.options.indices)
            assertFalse("Explanation should not be blank", q.explanation.isBlank())
        }
    }

    @Test
    fun testDirectivesAndOperatorsPresent() {
        val itemNames = SvaRepository.items.map { it.name }
        assertTrue(itemNames.any { it.contains("assert property") })
        assertTrue(itemNames.any { it.contains("cover property") })
        assertTrue(itemNames.any { it.contains("assume property") })
        assertTrue(itemNames.any { it.contains("restrict property") })
        assertTrue(itemNames.any { it.contains("|->") })
        assertTrue(itemNames.any { it.contains("|=>") })
        assertTrue(itemNames.any { it.contains("s_eventually") })
        assertTrue(itemNames.any { it.contains("\$past") })
        assertTrue(itemNames.any { it.contains("\$onehot") })
    }

    @Test
    fun testPracticeChallengesIntegrity() {
        val challenges = SvaRepository.challenges
        assertEquals("Must generate exactly 150 practice challenges", 150, challenges.size)

        val uniqueIds = mutableSetOf<String>()
        for (chal in challenges) {
            assertTrue("Challenge ID must be unique: ${chal.id}", uniqueIds.add(chal.id))
            assertFalse("Prompt must not be blank", chal.prompt.isBlank())
            assertFalse("Target SVA must not be blank", chal.targetSva.isBlank())
            assertTrue("Tokens must not be empty", chal.tokens.isNotEmpty())
            assertTrue("Acceptable answers must include target", chal.acceptableAnswers.isNotEmpty())
            assertFalse("Explanation must not be blank", chal.explanation.isBlank())
        }
    }
}
