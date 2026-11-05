package com.example.mylibrary.data

import android.content.Context
import com.example.mylibrary.data.BookDbHelper.Companion.COLUMN_AUTHOR
import com.example.mylibrary.data.BookDbHelper.Companion.COLUMN_GENRE
import com.example.mylibrary.data.BookDbHelper.Companion.COLUMN_ID
import com.example.mylibrary.data.BookDbHelper.Companion.COLUMN_IS_READ
import com.example.mylibrary.data.BookDbHelper.Companion.COLUMN_TITLE
import com.example.mylibrary.data.BookDbHelper.Companion.COLUMN_YEAR
import com.example.mylibrary.data.BookDbHelper.Companion.TABLE_BOOKS
import com.example.mylibrary.data.BookDbHelper.Companion.toContentValues
import com.example.mylibrary.model.Book

/** Слой доступа к данным: все запросы к SQLite собраны в одном месте. */
class BookRepository(context: Context) {
    private val helper = BookDbHelper(context.applicationContext)

    fun getBooks(searchText: String = ""): List<Book> {
        val db = helper.readableDatabase
        val normalized = searchText.trim()
        val selection = if (normalized.isEmpty()) {
            null
        } else {
            "$COLUMN_TITLE LIKE ? OR $COLUMN_AUTHOR LIKE ? OR $COLUMN_GENRE LIKE ?"
        }
        val pattern = "%$normalized%"
        val selectionArgs = if (selection == null) null else arrayOf(pattern, pattern, pattern)

        val books = mutableListOf<Book>()
        db.query(
            TABLE_BOOKS,
            arrayOf(COLUMN_ID, COLUMN_TITLE, COLUMN_AUTHOR, COLUMN_YEAR, COLUMN_GENRE, COLUMN_IS_READ),
            selection,
            selectionArgs,
            null,
            null,
            "$COLUMN_TITLE COLLATE NOCASE ASC"
        ).use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
            val titleIndex = cursor.getColumnIndexOrThrow(COLUMN_TITLE)
            val authorIndex = cursor.getColumnIndexOrThrow(COLUMN_AUTHOR)
            val yearIndex = cursor.getColumnIndexOrThrow(COLUMN_YEAR)
            val genreIndex = cursor.getColumnIndexOrThrow(COLUMN_GENRE)
            val readIndex = cursor.getColumnIndexOrThrow(COLUMN_IS_READ)

            while (cursor.moveToNext()) {
                books += Book(
                    id = cursor.getLong(idIndex),
                    title = cursor.getString(titleIndex),
                    author = cursor.getString(authorIndex),
                    year = if (cursor.isNull(yearIndex)) null else cursor.getInt(yearIndex),
                    genre = cursor.getString(genreIndex),
                    isRead = cursor.getInt(readIndex) == 1
                )
            }
        }
        return books
    }

    fun addBook(book: Book): Long = helper.writableDatabase.insert(
        TABLE_BOOKS,
        null,
        book.toContentValues()
    )

    fun updateBook(book: Book): Int = helper.writableDatabase.update(
        TABLE_BOOKS,
        book.toContentValues(),
        "$COLUMN_ID = ?",
        arrayOf(book.id.toString())
    )

    fun deleteBook(bookId: Long): Int = helper.writableDatabase.delete(
        TABLE_BOOKS,
        "$COLUMN_ID = ?",
        arrayOf(bookId.toString())
    )

    fun setReadStatus(bookId: Long, isRead: Boolean): Int {
        val values = android.content.ContentValues().apply {
            put(COLUMN_IS_READ, if (isRead) 1 else 0)
        }
        return helper.writableDatabase.update(
            TABLE_BOOKS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(bookId.toString())
        )
    }

    fun close() = helper.close()
}
