package com.runner.presenter
import android.content.Context
import android.util.Log
import com.runner.RuheApp
import com.runner.R
import com.runner.View.LoginViewNew
import com.runner.View.OnspotView
import com.runner.View.RedemptionView
import com.runner.extras.appUtils
import com.runner.model.LoginModelNew
import com.runner.model.OnspotModel
import com.runner.model.RedemptionListModel
import com.runner.model.VerifyResponce
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
class LoginePresenterNew : BasePresenter<LoginViewNew?>() {

    fun LoginNew(activity: Context, map: HashMap<String, RequestBody>, header: HashMap<String, String>, progress: Boolean,objectUser :String) {
        view!!.enableLoadingBar(activity, progress, activity.getResources().getString(R.string.loading))
        RuheApp.instance
            ?.apiService
            ?.loginnew(objectUser,header)?.enqueue(object : Callback<LoginModelNew?> {
                override fun onResponse(call: Call<LoginModelNew?>, response: Response<LoginModelNew?>) {
                    view?.enableLoadingBar(activity, false, "")
                    if(response.code()==200 && response.body()!=null)
                    {
                        view?.onLoginComplete(response.body(), response.code())
                    }
                    else{
                        appUtils.ConstantError(response.errorBody()!!.string(),activity,response.message(),response.code())
                    }
                }
                override fun onFailure(call: Call<LoginModelNew?>, t: Throwable) {
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



