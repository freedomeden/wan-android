package com.dzenm.wanandroid.api

import android.content.Context
import android.widget.Toast
import com.dzenm.wanandroid.model.SuccessModel
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

object CollectHelper {

    /**
     * 收藏
     */
    fun collect(context: Context, id: Int) {
        Api.getService()
            .collectArticle(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SuccessModel> {
                override fun onError(e: Throwable) {
                    Toast.makeText(context, "收藏失败: " + e, Toast.LENGTH_SHORT).show()
                }

                override fun onNext(t: SuccessModel) {
                    if (t?.errorCode == 0) {
                        Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "收藏失败: " + t?.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

            })
    }

    /**
     * 取消收藏
     */
    fun uncollect(context: Context, id: Int) {
        Api.getService()
            .uncollectArticle(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SuccessModel> {
                override fun onError(e: Throwable) {
                    Toast.makeText(context, "取消收藏失败: " + e, Toast.LENGTH_SHORT).show()
                }

                override fun onNext(t: SuccessModel) {
                    if (t?.errorCode == 0) {
                        Toast.makeText(context, "取消收藏", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "取消收藏失败: " + t?.errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

            })
    }
}