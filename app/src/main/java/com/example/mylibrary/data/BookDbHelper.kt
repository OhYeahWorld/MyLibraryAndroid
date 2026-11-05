package com.example.mylibrary.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import com.example.mylibrary.model.Book

/** Создаёт и обслуживает локальную базу данных приложения. */
class BookDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_BOOKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_AUTHOR TEXT NOT NULL,
                $COLUMN_YEAR INTEGER CHECK ($COLUMN_YEAR IS NULL OR $COLUMN_YEAR BETWEEN 0 AND 3000),
                $COLUMN_GENRE TEXT NOT NULL DEFAULT '',
                $COLUMN_IS_READ INTEGER NOT NULL DEFAULT 0 CHECK ($COLUMN_IS_READ IN (0, 1))
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX idx_books_title_author ON $TABLE_BOOKS " +
                "($COLUMN_TITLE COLLATE NOCASE, $COLUMN_AUTHOR COLLATE NOCASE)"
        )

        insertInitialBooks(db)
    }

    /** В учебном проекте схема имеет одну версию; при её изменении сюда добавляются миграции. */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Не удаляем пользовательские записи. Для новых версий добавляйте ALTER TABLE/миграции.
    }

    private fun insertInitialBooks(db: SQLiteDatabase) {
        val examples = listOf(
            Book(title = "Мастер и Маргарита", author = "Михаил Булгаков", year = 1967, genre = "Роман", isRead = true),
            Book(title = "Преступление и наказание", author = "Фёдор Достоевский", year = 1866, genre = "Классика"),
            Book(title = "1984", author = "Джордж Оруэлл", year = 1949, genre = "Антиутопия"),
            Book(title = "Властелин колец", author = "Джон Р. Р. Толкин", year = 1954, genre = "Фэнтези"),
            Book(title = "Цветы для Элджернона", author = "Дэниел Киз", year = 1966, genre = "Научная фантастика")
        )
        examples.forEach { book ->
            db.insert(TABLE_BOOKS, null, book.toContentValues())
        }
    }

    companion object {
        const val DATABASE_NAME = "my_library.db"
        const val DATABASE_VERSION = 1
        const val TABLE_BOOKS = "books"
        const val COLUMN_ID = "_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_YEAR = "published_year"
        const val COLUMN_GENRE = "genre"
        const val COLUMN_IS_READ = "is_read"

        fun Book.toContentValues(): ContentValues = ContentValues().apply {
            put(COLUMN_TITLE, title.trim())
            put(COLUMN_AUTHOR, author.trim())
            if (year == null) putNull(COLUMN_YEAR) else put(COLUMN_YEAR, year)
            put(COLUMN_GENRE, genre.trim())
            put(COLUMN_IS_READ, if (isRead) 1 else 0)
        }
    }
}
