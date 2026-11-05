package com.example.mylibrary

import android.app.Activity
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.example.mylibrary.data.BookRepository
import com.example.mylibrary.model.Book

class MainActivity : Activity() {
    private lateinit var repository: BookRepository
    private lateinit var searchInput: EditText
    private lateinit var bookListContainer: LinearLayout
    private lateinit var statisticsText: TextView

    private val pageBackground = Color.rgb(243, 245, 248)
    private val navy = Color.rgb(36, 50, 74)
    private val teal = Color.rgb(47, 143, 131)
    private val primaryText = Color.rgb(31, 41, 55)
    private val secondaryText = Color.rgb(101, 113, 132)
    private val danger = Color.rgb(198, 83, 83)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = BookRepository(this)
        buildScreen()
        refreshScreen()
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(pageBackground)
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(26), dp(22), dp(22))
            setBackgroundColor(navy)
        }
        header.addView(TextView(this).apply {
            text = "МОЯ БИБЛИОТЕКА"
            textSize = 23f
            setTextColor(Color.WHITE)
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            letterSpacing = 0.04f
        })
        header.addView(TextView(this).apply {
            text = "Личная коллекция книг — всегда под рукой"
            textSize = 14f
            setTextColor(Color.rgb(213, 221, 232))
            setPadding(0, dp(6), 0, 0)
        })
        statisticsText = TextView(this).apply {
            textSize = 13f
            setTextColor(Color.WHITE)
            setPadding(0, dp(16), 0, 0)
        }
        header.addView(statisticsText)
        root.addView(header)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(10))
        }

        searchInput = EditText(this).apply {
            hint = "Поиск по названию, автору или жанру"
            textSize = 14f
            isSingleLine = true
            inputType = InputType.TYPE_CLASS_TEXT
            setTextColor(primaryText)
            setHintTextColor(secondaryText)
            setPadding(dp(14), dp(11), dp(14), dp(11))
            background = roundedBackground(Color.WHITE, 14)
        }
        content.addView(searchInput, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(50)
        ))

        val addButton = Button(this).apply {
            text = "+  Добавить книгу"
            isAllCaps = false
            textSize = 15f
            setTextColor(Color.WHITE)
            backgroundTintList = ColorStateList.valueOf(teal)
            setOnClickListener { showBookDialog() }
        }
        val addParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(50)
        ).apply { topMargin = dp(10); bottomMargin = dp(8) }
        content.addView(addButton, addParams)
        root.addView(content)

        val scrollView = ScrollView(this).apply {
            clipToPadding = false
            setPadding(dp(16), dp(2), dp(16), dp(16))
        }
        bookListContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        scrollView.addView(bookListContainer)
        root.addView(scrollView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                refreshBookList(s?.toString().orEmpty())
            }
        })

        setContentView(root)
    }

    private fun refreshScreen() {
        refreshStatistics()
        refreshBookList(searchInput.text.toString())
    }

    private fun refreshStatistics() {
        val allBooks = repository.getBooks()
        val readCount = allBooks.count { it.isRead }
        statisticsText.text = "Всего книг: ${allBooks.size}   •   Прочитано: $readCount"
    }

    private fun refreshBookList(query: String) {
        bookListContainer.removeAllViews()
        val books = repository.getBooks(query)
        if (books.isEmpty()) {
            bookListContainer.addView(TextView(this).apply {
                text = if (query.isBlank()) "Пока нет книг. Добавьте первую запись." else "По вашему запросу ничего не найдено."
                textSize = 15f
                gravity = Gravity.CENTER
                setTextColor(secondaryText)
                setPadding(dp(18), dp(34), dp(18), dp(34))
            })
            return
        }
        books.forEach { book -> bookListContainer.addView(createBookCard(book)) }
    }

    private fun createBookCard(book: Book): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(15), dp(16), dp(14))
            background = roundedBackground(Color.WHITE, 16)
            elevation = dp(2).toFloat()
        }
        val cardParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(12) }

        val titleRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        titleRow.addView(TextView(this).apply {
            text = book.title
            textSize = 17f
            setTextColor(primaryText)
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        })
        titleRow.addView(TextView(this).apply {
            text = if (book.isRead) "ПРОЧИТАНА" else "В ПЛАНАХ"
            textSize = 9f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(if (book.isRead) teal else secondaryText)
            setPadding(dp(7), dp(5), dp(7), dp(5))
            background = roundedBackground(
                if (book.isRead) Color.rgb(225, 242, 238) else Color.rgb(238, 241, 245),
                8
            )
        })
        card.addView(titleRow)

        card.addView(TextView(this).apply {
            text = book.author
            textSize = 14f
            setTextColor(secondaryText)
            setPadding(0, dp(7), 0, dp(4))
        })
        val details = listOfNotNull(book.year?.toString(), book.genre.takeIf { it.isNotBlank() })
            .joinToString("  •  ")
        if (details.isNotBlank()) {
            card.addView(TextView(this).apply {
                text = details
                textSize = 12f
                setTextColor(secondaryText)
                setPadding(0, 0, 0, dp(12))
            })
        } else {
            card.addView(View(this).apply { layoutParams = LinearLayout.LayoutParams(1, dp(8)) })
        }

        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        actions.addView(actionButton(
            if (book.isRead) "Не прочитана" else "Прочитана",
            if (book.isRead) secondaryText else teal
        ) {
            repository.setReadStatus(book.id, !book.isRead)
            refreshScreen()
        }, weightedButtonParams())
        actions.addView(actionButton("Изменить", navy) {
            showBookDialog(book)
        }, weightedButtonParams().apply { leftMargin = dp(6) })
        actions.addView(actionButton("Удалить", danger) {
            confirmDelete(book)
        }, weightedButtonParams().apply { leftMargin = dp(6) })
        card.addView(actions)

        card.layoutParams = cardParams
        return card
    }

    private fun actionButton(label: String, color: Int, onClick: () -> Unit): Button =
        Button(this).apply {
            text = label
            isAllCaps = false
            textSize = 11f
            minHeight = dp(40)
            minimumHeight = dp(40)
            setPadding(dp(2), 0, dp(2), 0)
            setTextColor(Color.WHITE)
            backgroundTintList = ColorStateList.valueOf(color)
            setOnClickListener { onClick() }
        }

    private fun weightedButtonParams() = LinearLayout.LayoutParams(
        0,
        dp(42),
        1f
    )

    private fun showBookDialog(existing: Book? = null) {
        val fields = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(8), dp(22), 0)
        }

        fun input(hintText: String, inputType: Int = InputType.TYPE_CLASS_TEXT): EditText =
            EditText(this).apply {
                hint = hintText
                textSize = 15f
                setSingleLine(true)
                this.inputType = inputType
                setTextColor(primaryText)
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(50)
                ).apply { bottomMargin = dp(8) }
                layoutParams = params
            }

        val titleInput = input("Название книги").apply { setText(existing?.title.orEmpty()) }
        val authorInput = input("Автор").apply { setText(existing?.author.orEmpty()) }
        val yearInput = input("Год издания (необязательно)", InputType.TYPE_CLASS_NUMBER)
            .apply { setText(existing?.year?.toString().orEmpty()) }
        val genreInput = input("Жанр (необязательно)").apply { setText(existing?.genre.orEmpty()) }
        val readCheckBox = CheckBox(this).apply {
            text = "Книга уже прочитана"
            textSize = 14f
            isChecked = existing?.isRead ?: false
        }
        fields.addView(titleInput)
        fields.addView(authorInput)
        fields.addView(yearInput)
        fields.addView(genreInput)
        fields.addView(readCheckBox)

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (existing == null) "Новая книга" else "Редактирование книги")
            .setView(fields)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Сохранить", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = titleInput.text.toString().trim()
                val author = authorInput.text.toString().trim()
                val yearText = yearInput.text.toString().trim()
                val year = yearText.takeIf { it.isNotEmpty() }?.toIntOrNull()

                if (title.isBlank()) {
                    titleInput.error = "Введите название"
                    titleInput.requestFocus()
                    return@setOnClickListener
                }
                if (author.isBlank()) {
                    authorInput.error = "Введите автора"
                    authorInput.requestFocus()
                    return@setOnClickListener
                }
                if (yearText.isNotEmpty() && (year == null || year !in 0..3000)) {
                    yearInput.error = "Укажите корректный год (0–3000)"
                    yearInput.requestFocus()
                    return@setOnClickListener
                }

                val book = Book(
                    id = existing?.id ?: 0L,
                    title = title,
                    author = author,
                    year = year,
                    genre = genreInput.text.toString().trim(),
                    isRead = readCheckBox.isChecked
                )
                val result = if (existing == null) repository.addBook(book) else repository.updateBook(book).toLong()
                if (result > 0L) {
                    Toast.makeText(this, "Книга сохранена", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    refreshScreen()
                } else {
                    Toast.makeText(this, "Не удалось сохранить запись", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    private fun confirmDelete(book: Book) {
        AlertDialog.Builder(this)
            .setTitle("Удалить книгу?")
            .setMessage("Запись «${book.title}» будет удалена из библиотеки.")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Удалить") { _, _ ->
                repository.deleteBook(book.id)
                Toast.makeText(this, "Книга удалена", Toast.LENGTH_SHORT).show()
                refreshScreen()
            }
            .show()
    }

    private fun roundedBackground(color: Int, radiusDp: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp).toFloat()
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        repository.close()
        super.onDestroy()
    }
}
