package com.runner.ui.activity.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.ruhe.utils.NetworkAlertUtility;
import com.runner.R;
import com.runner.View.RedemptionView;
import com.runner.extras.appUtils;
import com.runner.manager.CheckInternetConection;
import com.runner.model.RedemptionListModel;
import com.runner.model.VerifyResponce;
import com.runner.presenter.RedemptionPresenter;
import com.runner.ui.activity.BaseActivity;

import org.chromium.base.MemoryPressureLevel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MemberRegistration1 extends BaseActivity implements RedemptionView {

    SharedPreferences sharedPreferences;

    ProgressDialog progressDialog=null;
    TextView add_more;
    LinearLayout container;
    ImageView iv_back;
    Button btn_onsite_submit;
    int count = 0;
    String parent_id = "";
    String category_recv = "";
    RedemptionPresenter presenter =null;

    ArrayList<EditText> first_name_list = new ArrayList<EditText>();
    ArrayList<TextView> warning_firstname = new ArrayList<TextView>();
    //   ArrayList<EditText> Last_name_list = new ArrayList<EditText>();
    ArrayList<EditText> email_list = new ArrayList<EditText>();
    ArrayList<EditText> ed_spinnerList = new ArrayList<EditText>();
    ArrayList<TextView> warning_email = new ArrayList<TextView>();
    ArrayList<EditText> phone_list = new ArrayList<EditText>();
    ArrayList<TextView> warning_phone = new ArrayList<TextView>();
    ArrayList<EditText> city_list = new ArrayList<EditText>();
    ArrayList<EditText> employee_code_list = new ArrayList<EditText>();
    ArrayList<EditText> Location_list = new ArrayList<EditText>();
    ArrayList<EditText> VehicleNO_list = new ArrayList<EditText>();
    ArrayList<EditText> Hotel_name_list = new ArrayList<EditText>();
    ArrayList<EditText> Relation_list = new ArrayList<EditText>();
    ArrayList<TextView> warning_city = new ArrayList<TextView>();
    ArrayList<TextView> warning_Employee = new ArrayList<TextView>();
    ArrayList<TextView> warning_Location = new ArrayList<TextView>();
    ArrayList<TextView> warning_VehicleNO = new ArrayList<TextView>();
    ArrayList<TextView> warning_HotelName = new ArrayList<TextView>();
    ArrayList<TextView> warning_Relation = new ArrayList<TextView>();
    ArrayList<ImageView> crossImageView = new ArrayList<ImageView>();
    ArrayList<String> relation_value = new ArrayList<String>();
    ArrayList<String> country_codeList = new ArrayList<String>();
    ArrayList<CountryCodePicker> country_codepickerList = new ArrayList<CountryCodePicker>();
    ArrayList<TextView> warning_relation_value = new ArrayList<TextView>();
    ArrayList<Integer> position = new ArrayList<Integer>();

    Integer remaining_qty;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member1);

        presenter = new  RedemptionPresenter();
        presenter.setView(this);

        parent_id = (String) getIntent().getSerializableExtra("id");
        category_recv = (String) getIntent().getSerializableExtra("category");
        remaining_qty = getIntent().getIntExtra("remaining_qty",0);

        container = findViewById(R.id.container);
        add_more = findViewById(R.id.add_more);
        iv_back = findViewById(R.id.iv_back);
        btn_onsite_submit = findViewById(R.id.btn_onsite_submit);
        Add_form();

        add_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("@@@", "Here..");
                if(remaining_qty>count){
                    Add_form();
                }else {
                    Toast.makeText(MemberRegistration1.this, "you can't add more members", Toast.LENGTH_SHORT).show();
                }


            }
        });


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_back_dialog();
            }
        });

        btn_onsite_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });


    }

    private void submitForm() {

        JSONArray array = new JSONArray();


        for (int i = 0; i < crossImageView.size(); i++) {

           /* if (first_name_list.get(i).getText().toString().trim().isEmpty()) {
                warning_firstname.get(i).setVisibility(View.VISIBLE);
                return;
            }
            warning_firstname.get(i).setVisibility(View.GONE);*/

           /* if(Last_name_list.get(i).getText().toString().trim().isEmpty()){
                warning_lastname.get(i).setVisibility(View.VISIBLE);
                return;
            }
            warning_lastname.get(i).setVisibility(View.GONE);*/

            /*if (email_list.get(i).getText().toString().trim().isEmpty()) {
                warning_email.get(i).setVisibility(View.VISIBLE);
                return;
            }
            warning_email.get(i).setVisibility(View.GONE);*/

         /*   if (phone_list.get(i).getText().toString().isEmpty()) {
                warning_phone.get(i).setVisibility(View.VISIBLE);
                return;
            }
            warning_phone.get(i).setVisibility(View.GONE);*/

           /* if (city_list.get(i).getText().toString().trim().isEmpty()) {
                warning_city.get(i).setVisibility(View.VISIBLE);
                return;
            }
            warning_city.get(i).setVisibility(View.GONE);

            if (employee_code_list.get(i).getText().toString().trim().isEmpty()) {
                warning_Employee.get(i).setVisibility(View.VISIBLE);
                return;
            }
            warning_Employee.get(i).setVisibility(View.GONE);

            if (Location_list.get(i).getText().toString().trim().isEmpty()) {
                warning_Location.get(i).setVisibility(View.VISIBLE);
                return;
            }
            warning_Location.get(i).setVisibility(View.GONE);


            if (VehicleNO_list.get(i).getText().toString().trim().isEmpty()) {
                warning_VehicleNO.get(i).setVisibility(View.VISIBLE);
                return;
            }
            warning_VehicleNO.get(i).setVisibility(View.GONE);*/
/*
            if (VehicleNO_list.get(i).getText().toString().trim().isEmpty()) {
                warning_VehicleNO.get(i).setVisibility(View.VISIBLE);
                return;
            }
            warning_VehicleNO.get(i).setVisibility(View.GONE);*/


            if (ed_spinnerList.get(i).getText().toString().equals("--select--")) {
                warning_relation_value.get(i).setVisibility(View.VISIBLE);
                return;
            }
            warning_relation_value.get(i).setVisibility(View.GONE);
           /* if (Hotel_name_list.get(i).getText().toString().trim().isEmpty()) {
                warning_HotelName.get(i).setVisibility(View.VISIBLE);
                return;
            }
            warning_HotelName.get(i).setVisibility(View.GONE);*/



        }

        Log.e("#@SIZE",crossImageView.size()+"");

        for (int i = 0; i < crossImageView.size(); i++) {
            JSONObject object = new JSONObject();
            try {
               // object.put("parent_id", parent_id);
               // object.put("name", first_name_list.get(i).getText().toString());
               // object.put("age", VehicleNO_list.get(i).getText());
                //object.put("email", email_list.get(i).getText().toString());
               // object.put("phone", phone_list.get(i).getText().toString());
               // object.put("country_code", country_codeList.get(i));
               // object.put("company", "");
              //  object.put("employee_code", "");
             //   object.put("location","");
             //   object.put("travel_plan", "");
            //    object.put("hotel_name", "");
            //    object.put("vehicle_no", "");
               object.put("name", ed_spinnerList.get(i).getText());
               object.put("is_lunch_allotted", "0");
            //    object.put("source", "Child");
                array.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Exception",e.getMessage());
            }

        }
        Log.e("@@", array.toString());
        saveuser(array.toString());
    }

    public void saveuser(String jsonobjectMember) {
        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");

        if (!CheckInternetConection.isInternetConnection(MemberRegistration1.this)) {

            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(MemberRegistration1.this);
            sweetAlertDialog.setTitleText("Alert!!")
                    .setContentText("please check Internet Connection...")
                    .show();
            return;
        }


        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Adding Guest Member...");
        progressDialog.setCancelable(false);
        progressDialog.show();



            HashMap<String, RequestBody> map = new HashMap<String, RequestBody>();
            HashMap<String, String> header = new HashMap<String, String>();

        map.put("parent_id", RequestBody.create(MediaType.parse("text/plain"), parent_id.toString()));
        map.put("guests", RequestBody.create(MediaType.parse("text/plain"),jsonobjectMember.toString()));




        presenter.Add_guest(this, map, header, true, "","","");

           // presenter!!.Allot(this, map, header, true, id,code,"")





      /*  try {


            WebServiceHandler webServiceHandler = new WebServiceHandler(MemberRegistration1.this);

            webServiceHandler.webServiceListener = new WebServiceListener() {
                @Override
                public void onRequestCompleted(String response) {
                    Log.e("Web Response", response);
                    btn_onsite_submit.setEnabled(true);
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        Log.e("@@", jsonObject.toString());
                        String success = jsonObject.getString("status");
                        if (success.equalsIgnoreCase("true")) {
                            ImageView ivQr = findViewById(R.id.ivQr_sunmi);
                            Log.e("@@@", "inher");
                            JSONObject dataobjects = jsonObject.getJSONObject("data");
                            Log.e("@APTH1", dataobjects.getString("qrcode_path"));
                            *//*mImageLoader = ImageLoader.getInstance();
                            mImageLoader.init(ImageLoaderConfiguration.createDefault(OnspotRegistrationActivity.this));
                            mImageLoader.displayImage(dataobjects.getString("qrcode_path"),ivQr);*//*

                            Dialog dialogMain = new Dialog(MemberRegistration1.this, R.style.my_dialog);
                            dialogMain.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialogMain.setContentView(R.layout.dialog_success1);
                            dialogMain.setCancelable(false);
                            TextView tv_msg = dialogMain.findViewById(R.id.msg);
                            ImageView iv_cross = dialogMain.findViewById(R.id.iv_cross);
                            Button btn_ok = dialogMain.findViewById(R.id.btn_ok);
                            btn_ok.setText("Ok");
                            Button btn_cancel = dialogMain.findViewById(R.id.btn_cancel);
                            btn_cancel.setVisibility(View.GONE);
                            String strmsg = jsonObject.getString("message");

                            iv_cross.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // dialogMain.dismiss();
                                    // finish();
                                }
                            });


                            if (strmsg.toString().contains("[")) {
                                JSONArray array = new JSONArray(strmsg);
                                strmsg = array.getString(0);


                            }

                           *//* strmsg = strmsg.toString().replace("\"", "");
                            strmsg = strmsg.toString().replace("[", "");*//*
                            strmsg = strmsg.replace("[", "").replace("]", "");


                            tv_msg.setText(strmsg);
                            dialogMain.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogMain.show();
                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                   *//* Intent intent = new Intent(MemberRegistration1.this, ViewMemberActivity.class);
                                    intent.putExtra("Camera", -1);
                                    intent.putExtra("parent_id", parent_id);
                                    intent.putExtra("Flag", "member_register");
                                    startActivity(intent);*//*
                                    finish();


                                }
                            });


                        }
                        else {
                            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(MemberRegistration1.this);
                            sweetAlertDialog.setTitleText("Alert!!")
                                    .setContentText(jsonObject.getString("message"))
                                    .show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };


            Log.e("@@jsonMember", jsonobjectMember.toString());

            try {

                JSONArray jsonObject = new JSONArray(jsonobjectMember);
                webServiceHandler.RegisterMember(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onBackPressed() {
        open_back_dialog();
    }

    private void open_back_dialog() {

        Dialog dialog = new Dialog(this, R.style.my_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_back);
        dialog.setCancelable(false);
        TextView tv_msg = dialog.findViewById(R.id.msg);

        Button btn_ok = dialog.findViewById(R.id.btn_yes);
        Button btn_no = dialog.findViewById(R.id.btn_no);
        btn_no.setVisibility(View.VISIBLE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void Add_form() {

        count = count + 1;


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_member, null);

        TextView member_qty = view.findViewById(R.id.member_qty);
        CountryCodePicker ccp = view.findViewById(R.id.ccp);
        EditText ed_spinner = view.findViewById(R.id.ed_spinner);
        ImageView cross = view.findViewById(R.id.cross);
        member_qty.setText("Guest " + count);
        country_codepickerList.add(ccp);
        country_codeList.add("+91");
        first_name_list.add(view.findViewById(R.id.ed_name));
        email_list.add(view.findViewById(R.id.ed_email));
        ed_spinnerList.add(ed_spinner);
        phone_list.add(view.findViewById(R.id.ed_phone));
        city_list.add(view.findViewById(R.id.ed_city));
        employee_code_list.add(view.findViewById(R.id.EmployeeCode));
        Location_list.add(view.findViewById(R.id.Location));
        VehicleNO_list.add(view.findViewById(R.id.VehicleNO));
        Hotel_name_list.add(view.findViewById(R.id.HotelName));
        Relation_list.add(view.findViewById(R.id.Relation));
        crossImageView.add(cross);
        position.add(count);
        warning_firstname.add(view.findViewById(R.id.warning_name));
        warning_email.add(view.findViewById(R.id.warning_email));
        warning_phone.add(view.findViewById(R.id.warning_phone));
        warning_city.add(view.findViewById(R.id.warning_AddGSTNO));
        warning_Employee.add(view.findViewById(R.id.warning_Employee));
        warning_Location.add(view.findViewById(R.id.warning_Location));
        warning_VehicleNO.add(view.findViewById(R.id.warning_VehicleNO));
        warning_HotelName.add(view.findViewById(R.id.warning_HotelName));
        warning_Relation.add(view.findViewById(R.id.warning_Relation));
        warning_relation_value.add(view.findViewById(R.id.warning_Region));


        ArrayList<String> region_list = new ArrayList<String>();

        Spinner spinner_Region = view.findViewById(R.id.spinner_Region);
        region_list.add("--select--");
        region_list.add("Kid (0-4 Years)");
        region_list.add("Kid (5-14 Years)");
        region_list.add("Adults");


       /* try {
            SharedPreferences    prefeMain = MemberRegistration1.this.getSharedPreferences("FRIENDS", Context.MODE_PRIVATE);
            JSONArray array = new JSONArray(prefeMain.getString("CATAGORY", ""));
            for(int i=0;i<array.length();i++){
                region_list.add(array.getString(i));
            }

        }catch (Exception ex){

        }*/





        ArrayAdapter RegionAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, region_list);
        spinner_Region.setAdapter(RegionAdapter);


        spinner_Region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ed_spinner.setText(region_list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (count == 1) {
                    Toast.makeText(getApplicationContext(), "Minimum one Guest required to Add..", Toast.LENGTH_SHORT).show();
                    return;
                }

                count = count - 1;
                container.removeView(view);

                first_name_list.remove(first_name_list.size() - 1);
                email_list.remove(email_list.size() - 1);
                phone_list.remove(phone_list.size() - 1);
                city_list.remove(city_list.size() - 1);
                employee_code_list.remove(employee_code_list.size() - 1);
                Location_list.remove(Location_list.size() - 1);
                VehicleNO_list.remove(VehicleNO_list.size() - 1);
                Hotel_name_list.remove(Hotel_name_list.size() - 1);
                Relation_list.remove(Relation_list.size() - 1);
                ed_spinnerList.remove(ed_spinnerList.size() - 1);
                crossImageView.remove(crossImageView.size() - 1);
                country_codeList.remove(country_codeList.size() - 1);
                country_codepickerList.remove(country_codepickerList.size() - 1);


                warning_firstname.remove(warning_firstname.size() - 1);
                warning_email.remove(warning_email.size() - 1);
                warning_phone.remove(warning_phone.size() - 1);
                warning_city.remove(warning_city.size() - 1);
                warning_Employee.remove(warning_Employee.size() - 1);
                warning_Location.remove(warning_Location.size() - 1);
                warning_VehicleNO.remove(warning_VehicleNO.size() - 1);
                warning_HotelName.remove(warning_HotelName.size() - 1);
                warning_Relation.remove(warning_Relation.size() - 1);
                warning_relation_value.remove(warning_relation_value.size() - 1);


                Log.e("@@name size", first_name_list.size() + "");
                Log.e("@@email size", email_list.size() + "");
                Log.e("@@phone size", phone_list.size() + "");
                Log.e("@@city size", city_list.size() + "");
                Log.e("@@relation size", ed_spinnerList.size() + "");
                Log.e("@@crossImageView size", crossImageView.size() + "");



                HideCross();
            }
        });


        container.addView(view);

        HideCross();
        Mangegecountry_code();
    }

    private void Mangegecountry_code() {

        for (int i = 0; i < country_codepickerList.size(); i++) {
            int finalI = i;
            country_codepickerList.get(i).setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                @Override
                public void onCountrySelected(Country country) {
                    country_codeList.set(finalI, "+" + country.getPhoneCode().toString());
                }
            });


        }

    }

    private void HideCross() {
        for (int i = 0; i < crossImageView.size(); i++) {
            if (i == crossImageView.size() - 1) {
                crossImageView.get(i).setVisibility(View.VISIBLE);
            } else {
                crossImageView.get(i).setVisibility(View.GONE);
            }
        }
    }


    @Nullable
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void enableLoadingBar(@Nullable Context context, boolean enable, @Nullable String s) {

    }

    @Override
    public void onError(@Nullable String reason) {

    }

    @Override
    public void OnSearchUser(@Nullable RedemptionListModel model, int errCode) {

    }

    @Override
    public void OnUpdateToServer(@Nullable VerifyResponce model, int errCode) {

        progressDialog.dismiss();
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(MemberRegistration1.this);
        sweetAlertDialog.setTitleText("Alert!!")
                .setContentText(model.getMessage())
                .show();

        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                if(!category_recv.isEmpty()){
                    finish();
                }else{
                    Intent intent = new Intent(MemberRegistration1.this,DiyOnspotActicvity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }



            }
        });


    }

    @Override
    public void selectFileButtonOnClick() {

    }

    @Override
    public void printButtonOnClick() {

    }
}