package com.abonesma.core
import org.junit.Assert.*
import org.junit.Test
class CoreTest {
    val sample = """
Chapter 1 Motion
Lesson 1 Speed
Speed is defined as the distance travelled per unit of time in a motion.
v = d / t
Velocity refers to speed in a given direction with magnitude included.
Acceleration means the rate of change of velocity over a period of time.
Force: a push or pull acting upon an object that changes its motion.
الفصل الثاني الطاقة
الدرس الأول الشغل
الشغل هو القوة المبذولة مضروبة في الإزاحة في اتجاه القوة نفسها.
"""
    @Test fun chapters() { val c = ChapterDetector.detect(sample); assertEquals(2, c.size) }
    @Test fun defs() { assertTrue(DefinitionExtractor.extract(sample).size >= 3) }
    @Test fun formula() { assertEquals("v = d / t", FormulaExtractor.extract(sample).first().expr) }
    @Test fun questions() {
        val q = QuestionGenerator.generate(DefinitionExtractor.extract(sample))
        assertTrue(q.mcq.all { it.options.size == 4 && it.options[it.correct] == it.source })
    }
    @Test fun grade() { assertEquals(50.0, ExamGrader.grade(listOf(0, 1), mapOf(0 to 0, 1 to 2)).percent, 0.01) }
}
