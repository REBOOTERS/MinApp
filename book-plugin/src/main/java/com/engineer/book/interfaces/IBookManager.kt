package com.engineer.book.interfaces

import com.engineer.book.models.Book

interface IBookManager {
    fun addBook(book: Book)

    fun removeBook(name: String)

    fun getAll(): List<Book>
}