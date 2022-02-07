package com.engineer.android.mini.ipc.aidl.self;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IBookInterface2 extends IInterface {

    int ADD_CODE = IBinder.FIRST_CALL_TRANSACTION + 1;
    int GET_CODE = IBinder.FIRST_CALL_TRANSACTION + 2;

    void add(int a) throws RemoteException;

    int get() throws RemoteException;


    abstract class Stub extends Binder implements IBookInterface2 {

        private static final java.lang.String DESCRIPTOR = "com.engineer.android."
                + "mini.ipc.aidl.self.IBookInterface2";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IBookInterface2 asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface inn = obj.queryLocalInterface(DESCRIPTOR);
            if (inn != null && (inn instanceof IBookInterface2)) {
                return (IBookInterface2) inn;
            }
            return new Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        protected boolean onTransact(int code, @NonNull Parcel data,
                                     @Nullable Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IBookInterface2 {

            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                mRemote = iBinder;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }


            @Override
            public void add(int a) throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();

                try {
                    data.writeInterfaceToken(DESCRIPTOR);
                    data.writeInt(a);
                    boolean status = mRemote.transact(ADD_CODE, data, reply, 0);

                    reply.readException();
                } finally {
                    data.recycle();
                    reply.recycle();
                }

            }

            @Override
            public int get() throws RemoteException {
                return 0;
            }
        }
    }
}
