package com.runner.presenter

import android.content.Context
import android.util.Log
import com.runner.R
import com.runner.RuheApp
import com.runner.View.ChooseFormView
import com.runner.extras.appUtils
import com.runner.model.ChooseCategoryModel
import com.runner.model.VerifyResponce
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap


class ChooseFormPresenter : BasePresenter<ChooseFormView?>() {

    fun ChooseFormFields(activity: Context, map: HashMap<String, RequestBody>, header: HashMap<String, String>, progress: Boolean, objectUser :String, selectedid:String, api:String) {
        view!!.enableLoadingBar(activity, progress, activity.getResources().getString(R.string.loading))

        Log.e("@@Code.... ",selectedid)

        RuheApp.instance
            ?.apiService
            ?.ChooseFormField(api,selectedid,header)?.enqueue(object :Callback<ChooseCategoryModel?> {
                override fun onResponse(call: Call<ChooseCategoryModel?>, response: Response<ChooseCategoryModel?>) {
                    view?.enableLoadingBar(activity, false, "")
                    if(response.code()==200 && response.body()!=null)
                    {
                        view?.OnFormSuccess(response.body(), response.code())
                    }
                    else{
                        appUtils.ConstantError(response.errorBody()!!.string(),activity,response.message(),response.code())
                    }
                }
                override fun onFailure(call: Call<ChooseCategoryModel?>, t: Throwable) {
                    view?.enableLoadingBar(activity, false, "")
                    try {
                        t.printStackTrace()
                        view?.onError(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } }
            })
    }

}