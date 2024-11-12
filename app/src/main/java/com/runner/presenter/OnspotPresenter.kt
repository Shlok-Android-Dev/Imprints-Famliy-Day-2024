package com.runner.presenter
import android.content.Context
import android.util.Log
import com.runner.RuheApp
import com.runner.R
import com.runner.View.OnspotView
import com.runner.extras.appUtils
import com.runner.model.OnspotModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
class OnspotPresenter : BasePresenter<OnspotView?>() {

    fun SaveUser(activity: Context, map: HashMap<String, RequestBody>, header: HashMap<String, String>, progress: Boolean,username :String,api:String) {
        view!!.enableLoadingBar(activity, progress, activity.getResources().getString(R.string.loading))
        RuheApp.instance?.apiService?.SaveUser(api,map,header)?.enqueue(object : Callback<OnspotModel?> {
            override fun onResponse(call: Call<OnspotModel?>, response: Response<OnspotModel?>) {
                view?.enableLoadingBar(activity, false, "")
                Log.d("API Response", response.body()?.toString() ?: "No response body")

                if(response.code()==200 && response.body()!=null)
                {
                    view?.OnSavedUSer(response.body(), response.code())
                }
                else{
                    appUtils.ConstantError(response.errorBody()!!.string(),activity,response.message(),response.code())
                }
            }
            override fun onFailure(call: Call<OnspotModel?>, t: Throwable) {
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



