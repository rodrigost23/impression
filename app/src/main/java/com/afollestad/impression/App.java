package com.afollestad.impression;

import android.app.Application;
import android.content.Context;

import com.afollestad.impression.accounts.Account;
import com.afollestad.impression.accounts.LocalHelper;
import com.afollestad.impression.utils.PrefUtils;
import com.afollestad.inquiry.Inquiry;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class App extends Application {

    private static Account currentAccount;
    private static Account[] allAccounts;

    public static Single<Account> getCurrentAccount(final Context context) {
        final long currentId = PrefUtils.getCurrentAccountId(context);
        return Observable.create(new Observable.OnSubscribe<Account[]>() {
            @Override
            public void call(Subscriber<? super Account[]> subscriber) {
                if (currentId == -1) {
                    Account account = LocalHelper.newInstance(context);
                    Inquiry.get()
                            .insertInto(Account.TABLE, Account.class)
                            .values(account)
                            .run();
                    currentAccount = account;

                    //Workaround for ID not updated after inserting
                    currentAccount = Inquiry.get()
                            .selectFrom(Account.TABLE, Account.class)
                            .one();
                    currentAccount.updateHelper();
                    //---

                    PrefUtils.setCurrentAccountId(context, currentAccount.getId());

                    subscriber.onNext(new Account[]{currentAccount});
                } else if (currentAccount != null && currentAccount.getId() == currentId) {
                    subscriber.onNext(new Account[]{currentAccount});
                }
                subscriber.onCompleted();
            }
        }).switchIfEmpty(getAllAccounts().toObservable())
                .map(new Func1<Account[], Account>() {
                    @Override
                    public Account call(Account[] accounts) {
                        for (Account a : accounts) {
                            if (a.getId() == currentId) {
                                currentAccount = a;
                                break;
                            }
                        }
                        return currentAccount;
                    }
                }).toSingle();
    }

    public static Single<Account[]> getAllAccounts() {
        return Single.create(new Single.OnSubscribe<Account[]>() {
            @Override
            public void call(SingleSubscriber<? super Account[]> singleSubscriber) {
                if (allAccounts != null) {
                    singleSubscriber.onSuccess(allAccounts);
                    return;
                }

                allAccounts = Inquiry.get()
                        .selectFrom(Account.TABLE, Account.class)
                        .all();

                for (Account account : allAccounts) {
                    account.updateHelper();
                }

                singleSubscriber.onSuccess(allAccounts);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
        Inquiry.init(this, "impression", 1);
        //LeakCanary.install(this);
    }
}