package com.runner.presenter
import android.content.Context
import com.runner.RuheApp
import com.runner.R
import com.runner.View.OnspotView
import com.runner.View.RedemptionView
import com.runner.View.ZappingView
import com.runner.extras.appUtils
import com.runner.model.OnspotModel
import com.runner.model.RedemptionListModel
import com.runner.model.VerifyResponce
import com.runner.model.ZappingResponce
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
class ZappingPresenter : BasePresenter<ZappingView?>() {


    fun Zapping(activity: Context, map: HashMap<String, RequestBody>, header: HashMap<String, String>, progress: Boolean,api :String,code : String,loc:String) {
        view!!.enableLoadingBar(activity, progress, activity.getResources().getString(R.string.loading))
        RuheApp.instance
            ?.apiService
            ?.Zapping(api,loc,code,header)?.enqueue(object : Callback<ZappingResponce?> {
                override fun onResponse(call: Call<ZappingResponce?>, response: Response<ZappingResponce?>) {
                    view?.enableLoadingBar(activity, false, "")
                    if(response.code()==200 && response.body()!=null)
                    {
                        view?.OnUSerZapped(response.body(), response.code())
                    }
                    else{
                        appUtils.ConstantError(response.errorBody()!!.string(),activity,response.message(),response.code())
                    }
                }
                override fun onFailure(call: Call<ZappingResponce?>, t: Throwable) {
                    view?.enableLoadingBar(activity, false, "")
                    try {
                        t.printStackTrace()
                        view?.onError(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } }
            })
    }


    fun ZappingwithSession(activity: Context, map: HashMap<String, RequestBody>, header: HashMap<String, String>, progress: Boolean,api :String,code : String,loc:String,session: String) {
        view!!.enableLoadingBar(activity, progress, activity.getResources().getString(R.string.loading))
        RuheApp.instance
            ?.apiService
            ?.Zappingwithsession(api,loc,code,session,header)?.enqueue(object : Callback<ZappingResponce?> {
                override fun onResponse(call: Call<ZappingResponce?>, response: Response<ZappingResponce?>) {
                    view?.enableLoadingBar(activity, false, "")
                    if(response.code()==200 && response.body()!=null)
                    {
                        view?.OnUSerZapped(response.body(), response.code())
                    }
                    else{
                        appUtils.ConstantError(response.errorBody()!!.string(),activity,response.message(),response.code())
                    }
                }
                override fun onFailure(call: Call<ZappingResponce?>, t: Throwable) {
                    view?.enableLoadingBar(activity, false, "")
                    try {
                        t.printStackTrace()
                        view?.onError(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } }
            })
    }


    fun ZappingUpdate(activity: Context, map: HashMap<String, RequestBody>, header: HashMap<String, String>, progress: Boolean,api :String,code : String,loc:String) {
        view!!.enableLoadingBar(activity, progress, activity.getResources().getString(R.string.loading))
        RuheApp.instance
            ?.apiService
            ?.ZappingUpdate(api,loc,code,header)?.enqueue(object : Callback<VerifyResponce?> {
                override fun onResponse(call: Call<VerifyResponce?>, response: Response<VerifyResponce?>) {
                    view?.enableLoadingBar(activity, false, "")

                    if(response.code()==200 && response.body()!=null)
                    {
                        view?.OnZappingUpdate(response.body(), response.code())
                    }
                    else{
                        appUtils.ConstantError(response.errorBody()!!.string(),activity,response.message(),response.code())
                    }
                }
                override fun onFailure(call: Call<VerifyResponce?>, t: Throwable) {
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



