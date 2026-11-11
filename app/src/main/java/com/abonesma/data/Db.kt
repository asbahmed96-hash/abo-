package com.abonesma.data
import android.content.Context
import androidx.room.*
@Entity data class BookE(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val path: String)
@Entity data class ChapterE(@PrimaryKey(autoGenerate = true) val id: Long = 0, val bookId: Long, val title: String, val pos: Int)
@Entity data class LessonE(@PrimaryKey(autoGenerate = true) val id: Long = 0, val chapterId: Long, val title: String, val text: String, val pos: Int)
@Entity data class AttemptE(@PrimaryKey(autoGenerate = true) val id: Long = 0, val chapterId: Long, val score: Int, val total: Int, val at: Long)
@Dao interface Dao {
    @Insert suspend fun book(b: BookE): Long
    @Insert suspend fun chapter(c: ChapterE): Long
    @Insert suspend fun lesson(l: LessonE): Long
    @Insert suspend fun attempt(a: AttemptE): Long
    @Query("SELECT * FROM BookE") suspend fun books(): List<BookE>
    @Query("SELECT * FROM ChapterE WHERE bookId=:b ORDER BY pos") suspend fun chapters(b: Long): List<ChapterE>
    @Query("SELECT * FROM LessonE WHERE chapterId=:c ORDER BY pos") suspend fun lessons(c: Long): List<LessonE>
    @Query("UPDATE ChapterE SET title=:t WHERE id=:id") suspend fun renameChapter(id: Long, t: String)
    @Query("DELETE FROM ChapterE WHERE id=:id") suspend fun deleteChapter(id: Long)
}
@Database(entities = [BookE::class, ChapterE::class, LessonE::class, AttemptE::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun dao(): Dao
    companion object { fun get(c: Context) = Room.databaseBuilder(c, AppDb::class.java, "nesma.db").build() }
}
