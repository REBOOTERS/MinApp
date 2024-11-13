// IBookInfoCallback.aidl
package com.engineer.android.mini.ipc.aidl;
import com.engineer.android.mini.ipc.aidl.Book;
// Declare any non-default types here with import statements

interface IBookInfoCallback {
    void notifyBookInfo(in List<Book> books);

    void operationSuccess(in String action);
}