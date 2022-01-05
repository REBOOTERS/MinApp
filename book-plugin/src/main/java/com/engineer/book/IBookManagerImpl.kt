package com.engineer.book

import com.engineer.book.interfaces.IBookManager
import com.engineer.book.models.Book
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Singleton
class IBookManagerImpl : IBookManager {
    private val books = ArrayList<Book>()

    override fun addBook(book: Book) {
        books.add(book)
    }

    override fun removeBook(name: String) {
        books.forEach {
            if (name.equals(it.name)) {
                books.remove(it)
            }
        }
    }

    override fun getAll(): List<Book> {
        return books
    }

    @Module
    @InstallIn(SingletonComponent::class)
    class BookManagerModule {

        @Singleton
        @Provides
        fun provideBookManager(): IBookManager {
            return IBookManagerImpl()
        }
    }
}