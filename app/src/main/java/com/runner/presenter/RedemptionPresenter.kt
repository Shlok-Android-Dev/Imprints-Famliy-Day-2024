package com.runner.presenter

import android.content.Context
import android.util.Log
import com.runner.R
import com.runner.RuheApp
import com.runner.View.RedemptionView
import com.runner.extras.appUtils
import com.runner.model.RedemptionListModel
import com.runner.model.VerifyResponce
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RedemptionPresenter : BasePresenter<RedemptionView?>() {


    fun SearchUSer(
        activity: Context,
        map: HashMap<String, RequestBody>,
        header: HashMap<String, String>,
        progress: Boolean,
        objectUser: String,
        api: String
    ) {
        view!!.enableLoadingBar(
            activity,
            progress,
            activity.getResources().getString(R.string.loading)
        )
        RuheApp.instance
            ?.apiService
            ?.SearchUSer(api, objectUser, header)?.enqueue(object : Callback<RedemptionListModel?> {
                override fun onResponse(
                    call: Call<RedemptionListModel?>,
                    response: Response<RedemptionListModel?>
                ) {
                    view?.enableLoadingBar(activity, false, "")
                    if (response.code() == 200 && response.body() != null) {
                        view?.OnSearchUser(response.body(), response.code())
                    } else {
                        appUtils.ConstantError(
                            response.errorBody()!!.string(),
                            activity,
                            response.message(),
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<RedemptionListModel?>, t: Throwable) {
                    view?.enableLoadingBar(activity, false, "")
                    try {
                        t.printStackTrace()
                        view?.onError(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    fun Allot(
        activity: Context,
        map: HashMap<String, RequestBody>,
        header: HashMap<String, String>,
        progress: Boolean,
        objectUser: String,
        code: String,
        api: String
    ) {
        view!!.enableLoadingBar(
            activity,
            progress,
            activity.getResources().getString(R.string.loading)
        )
        Log.e("@@Code.... ", code)
        RuheApp.instance
            ?.apiService
            ?.Allot(map, header)?.enqueue(object : Callback<VerifyResponce?> {
                override fun onResponse(
                    call: Call<VerifyResponce?>,
                    response: Response<VerifyResponce?>
                ) {
                    view?.enableLoadingBar(activity, false, "")
                    if (response.code() == 200 && response.body() != null) {
                        view?.OnUpdateToServer(response.body(), response.code())
                    } else {
                        appUtils.ConstantError(
                            response.errorBody()!!.string(),
                            activity,
                            response.message(),
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<VerifyResponce?>, t: Throwable) {
                    view?.enableLoadingBar(activity, false, "")
                    try {
                        t.printStackTrace()
                        view?.onError(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    fun Allot1(
        activity: Context,
        map: HashMap<String, RequestBody>,
        header: HashMap<String, String>,
        progress: Boolean,
        objectUser: String,
        code: String,
        api: String
    ) {
        view!!.enableLoadingBar(
            activity,
            progress,
            activity.getResources().getString(R.string.loading)
        )
        Log.e("@@Code.... ", code)
        RuheApp.instance
            ?.apiService
            ?.Allot1(map, header)?.enqueue(object : Callback<VerifyResponce?> {
                override fun onResponse(
                    call: Call<VerifyResponce?>,
                    response: Response<VerifyResponce?>
                ) {
                    view?.enableLoadingBar(activity, false, "")
                    if (response.code() == 200 && response.body() != null) {
                        view?.OnUpdateToServer(response.body(), response.code())
                    } else {
                        appUtils.ConstantError(
                            response.errorBody()!!.string(),
                            activity,
                            response.message(),
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<VerifyResponce?>, t: Throwable) {
                    view?.enableLoadingBar(activity, false, "")
                    try {
                        t.printStackTrace()
                        view?.onError(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }
    fun Add_guest(
        activity: Context,
        map: HashMap<String, RequestBody>,
        header: HashMap<String, String>,
        progress: Boolean,
        objectUser: String,
        code: String,
        api: String
    ) {
        view!!.enableLoadingBar(
            activity,
            progress,
            activity.getResources().getString(R.string.loading)
        )
        Log.e("@@Code.... ", code)
        RuheApp.instance
            ?.apiService
            ?.add_guest(map, header)?.enqueue(object : Callback<VerifyResponce?> {
                override fun onResponse(
                    call: Call<VerifyResponce?>,
                    response: Response<VerifyResponce?>
                ) {
                    view?.enableLoadingBar(activity, false, "")
                    if (response.code() == 200 && response.body() != null) {
                        view?.OnUpdateToServer(response.body(), response.code())
                    } else {
                        appUtils.ConstantError(
                            response.errorBody()!!.string(),
                            activity,
                            response.message(),
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<VerifyResponce?>, t: Throwable) {
                    view?.enableLoadingBar(activity, false, "")
                    try {
                        t.printStackTrace()
                        view?.onError(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }


    fun SearchUSerk2Mini(
        activity: Context,
        map: HashMap<String, RequestBody>,
        header: HashMap<String, String>,
        progress: Boolean,
        objectUser: String,
        api: String
    ) {
        view!!.enableLoadingBar(
            activity,
            progress,
            activity.getResources().getString(R.string.loading)
        )
        RuheApp.instance
            ?.apiService
            ?.SearchUSerk2mini(api, objectUser, header, "type_qr")
            ?.enqueue(object : Callback<RedemptionListModel?> {
                override fun onResponse(
                    call: Call<RedemptionListModel?>,
                    response: Response<RedemptionListModel?>
                ) {
                    view?.enableLoadingBar(activity, false, "")
                    if (response.code() == 200 && response.body() != null) {
                        view?.OnSearchUser(response.body(), response.code())
                    } else {
                        appUtils.ConstantError(
                            response.errorBody()!!.string(),
                            activity,
                            response.message(),
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<RedemptionListModel?>, t: Throwable) {
                    view?.enableLoadingBar(activity, false, "")
                    try {
                        t.printStackTrace()
                        view?.onError(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }


    fun UpdateToServer(
        activity: Context,
        map: HashMap<String, RequestBody>,
        header: HashMap<String, String>,
        progress: Boolean,
        objectUser: String,
        code: String,
        api: String
    ) {
        view!!.enableLoadingBar(
            activity,
            progress,
            activity.getResources().getString(R.string.loading)
        )

        Log.e("@@Code.... ", code)

        RuheApp.instance
            ?.apiService
            ?.UpdateToServer(api, code, header)?.enqueue(object : Callback<VerifyResponce?> {
                override fun onResponse(
                    call: Call<VerifyResponce?>,
                    response: Response<VerifyResponce?>
                ) {
                    view?.enableLoadingBar(activity, false, "")
                    if (response.code() == 200 && response.body() != null) {
                        view?.OnUpdateToServer(response.body(), response.code())
                    } else {
                        appUtils.ConstantError(
                            response.errorBody()!!.string(),
                            activity,
                            response.message(),
                            response.code()
                        )
                    }
                }

                override fun onFailure(call: Call<VerifyResponce?>, t: Throwable) {
                    view?.enableLoadingBar(activity, false, "")
                    try {
                        t.printStackTrace()
                        view?.onError(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }


}



