// IBookInterface.aidl
package com.engineer.android.mini.ipc.aidl;

// Declare any non-default typs here with import statements

import com.engineer.android.mini.ipc.aidl.Book;
import com.engineer.android.mini.ipc.aidl.IBookInfoCallback;

interface IBookInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
//
    List<Book> getBookList();
    void addBook(in Book book);
    void deleteBook(in Book book);

    void registerCallback(IBookInfoCallback callback);
    void unregisterCallback(IBookInfoCallback callback);

    void addBookToRepo(in Book book);
    List<Book> getBookListFromRepo();

    void startOtherAppService();
    void startOtherApp();
}