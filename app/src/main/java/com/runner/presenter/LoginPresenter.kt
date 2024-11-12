package com.runner.presenter
import android.content.Context
import com.runner.RuheApp
import com.runner.R
import com.runner.View.LoginView
import com.runner.extras.appUtils
import com.runner.model.LoginModel
import com.runner.model.VerifyPinModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
class LoginPresenter : BasePresenter<LoginView?>() {


    fun LoginUser(activity: Context, map: HashMap<String, RequestBody>, header: HashMap<String, String>, progress: Boolean,username :String) {
        view!!.enableLoadingBar(activity, progress, activity.getResources().getString(R.string.loading))
        RuheApp.instance?.apiService?.login(map,header)?.enqueue(object : Callback<LoginModel?> {
                override fun onResponse(call: Call<LoginModel?>, response: Response<LoginModel?>) {
                    view?.enableLoadingBar(activity, false, "")
                    if(response.code()==200 && response.body()!=null)
                    {
                        view?.onLoginComplete(response.body(), response.code())
                    }
                    else{
                        appUtils.ConstantError(response.errorBody()!!.string(),activity,response.message(),response.code())
                    }
                }
                override fun onFailure(call: Call<LoginModel?>, t: Throwable) {
                    view?.enableLoadingBar(activity, false, "")
                    try {
                        t.printStackTrace()
                        view?.onError(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } }
            })
    }


    fun verifyPin(activity: Context, map: HashMap<String, RequestBody>, header: HashMap<String, String>, progress: Boolean,username :String) {
        view!!.enableLoadingBar(activity, progress, activity.getResources().getString(R.string.loading))
        RuheApp.instance
            ?.apiService
            ?.verifyPin(map,header)?.enqueue(object : Callback<VerifyPinModel?> {
                override fun onResponse(call: Call<VerifyPinModel?>, response: Response<VerifyPinModel?>) {
                    view?.enableLoadingBar(activity, false, "")

                    if(response.code()==200 && response.body()!=null)
                    {
                        view?.OnVerifryPin(response.body(), response.code())
                    }
                    else{
                        appUtils.ConstantError(response.errorBody()!!.string(),activity,response.message(),response.code())
                    }
                }
                override fun onFailure(call: Call<VerifyPinModel?>, t: Throwable) {
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



