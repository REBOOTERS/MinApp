// IResponseListener.aidl
package com.engineer.android.mini.ipc.aidl;

// Declare any non-default types here with import statements

interface IResponseListener {
   void onSuccess(String responseJson);

   void onFail(String e);
}