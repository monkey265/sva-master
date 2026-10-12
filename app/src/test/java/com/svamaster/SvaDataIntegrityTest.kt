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
        for (category in SvaCategory.entries) {
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
        assertEquals("Must generate exactly 165 practice challenges", 165, challenges.size)

        val uniqueIds = mutableSetOf<String>()
        for (chal in challenges) {
            assertTrue("Challenge ID must be unique: ${chal.id}", uniqueIds.add(chal.id))
            assertFalse("Prompt must not be blank", chal.prompt.isBlank())
            assertFalse("Target SVA must not be blank", chal.targetSva.isBlank())
            assertTrue("Tokens must not be empty", chal.tokens.isNotEmpty())
            assertTrue("Acceptable answers must include target", chal.acceptableAnswers.isNotEmpty())
            assertFalse("Explanation must not be blank", chal.explanation.isBlank())

            // Verify that the tokens can indeed assemble into an acceptable answer
            val tokenAssembly = chal.tokens.joinToString(" ")
            val normalizedTokens = normalize(tokenAssembly)
            val matchesAny = chal.acceptableAnswers.any { normalize(it) == normalizedTokens }
            assertTrue(
                "Challenge ${chal.id} tokens ['$tokenAssembly'] cannot assemble into any acceptable answer: ${chal.acceptableAnswers}",
                matchesAny
            )
        }

        // Test user's specific property p_valid_hold
        val validHoldChal = challenges.find { it.targetSva.contains("valid && !ready") && it.targetSva.contains("\$stable(data)") }
        assertNotNull("Must contain property p_valid_hold challenge", validHoldChal)
        val fullDef = validHoldChal!!.fullPropertyDefinition
        assertNotNull(fullDef)
        assertTrue(fullDef!!.contains("property p_valid_hold;"))
        assertTrue(fullDef.contains("(valid && !ready) |=> (valid && \$stable(data));"))
        assertTrue(validHoldChal.assertionDirective!!.contains("assert property (p_valid_hold);"))
    }

    @Test
    fun testBadgesIntegrity() {
        val badges = SvaRepository.badges
        assertEquals("Must have 10 verification mastery badges", 10, badges.size)
        val uniqueIds = mutableSetOf<String>()
        for (b in badges) {
            assertTrue("Badge ID must be unique: ${b.id}", uniqueIds.add(b.id))
            assertFalse("Title must not be blank", b.title.isBlank())
            assertFalse("Description must not be blank", b.description.isBlank())
            assertFalse("Icon must not be blank", b.icon.isBlank())
            assertTrue("Target count must be > 0", b.targetCount > 0)
        }
    }

    @Test
    fun testProtocolPacksChallenges() {
        val protocolChallenges = SvaRepository.challenges.filter { it.category == SvaCategory.PROTOCOL_PACKS }
        assertEquals("Must have 15 protocol pack challenges", 15, protocolChallenges.size)
        assertTrue(protocolChallenges.any { it.prompt.contains("AXI") })
        assertTrue(protocolChallenges.any { it.prompt.contains("APB") })
        assertTrue(protocolChallenges.any { it.prompt.contains("FIFO") })
        assertTrue(protocolChallenges.any { it.prompt.contains("Skid Buffer") })
        assertTrue(protocolChallenges.any { it.prompt.contains("Arbiter") })
    }

    private fun normalize(raw: String): String {
        return raw.replace("\\s+".toRegex(), "")
            .replace("1'b0", "0")
            .replace("1'b1", "1")
            .trim()
    }
}
