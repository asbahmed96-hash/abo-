package com.abonesma.ui
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abonesma.core.*
import com.abonesma.data.*
import com.abonesma.pdf.PdfImporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        val dao = AppDb.get(this).dao()
        setContent { MaterialTheme { Surface(Modifier.fillMaxSize()) { Home(dao) } } }
    }
    @Composable fun Home(dao: Dao) {
        val scope = rememberCoroutineScope()
        var status by remember { mutableStateOf("") }
        var books by remember { mutableStateOf(listOf<BookE>()) }
        var defs by remember { mutableStateOf(listOf<Definition>()) }
        LaunchedEffect(Unit) { books = dao.books() }
        val pick = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri ?: return@rememberLauncherForActivityResult
            scope.launch {
                try {
                    val text = withContext(Dispatchers.IO) {
                        val f = PdfImporter.copyToAppStorage(this@MainActivity, uri)
                        PdfImporter.extractPages(this@MainActivity, f) { p, n -> status = "Page $p / $n" }.joinToString("\n")
                    }
                    if (text.isBlank()) { status = "No text found: scanned PDF needs OCR (assets missing)"; return@launch }
                    val chapters = ChapterDetector.detect(text)
                    val bid = dao.book(BookE(title = "Book ${books.size + 1}", path = uri.toString()))
                    chapters.forEachIndexed { ci, c ->
                        val cid = dao.chapter(ChapterE(bookId = bid, title = c.title, pos = ci))
                        c.lessons.forEachIndexed { li, l -> dao.lesson(LessonE(chapterId = cid, title = l.title, text = l.text, pos = li)) }
                    }
                    defs = DefinitionExtractor.extract(text)
                    books = dao.books(); status = "Done: ${chapters.size} chapters, ${defs.size} definitions"
                } catch (e: Exception) { status = "Error: ${e.message}" }
            }
        }
        Column(Modifier.padding(16.dp)) {
            Text("ABO NESMA 🦅", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = { pick.launch(arrayOf("application/pdf")) }) { Text("Import PDF") }
            Text(status)
            LazyColumn { items(defs) { Text("• ${it.term}: ${it.meaning}", Modifier.padding(vertical = 4.dp)) } }
        }
    }
}
