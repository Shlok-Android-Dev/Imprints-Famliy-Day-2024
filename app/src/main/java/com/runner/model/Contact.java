package com.runner.model;

public class Contact {



    // private variables
    public int _id;
    public String mfirstname;
    public String mlastname;
    public String mEmail;
    public String mPhone;
    public String mDate;
    public String mSpouseName;
    public String mMember1,mMember2;
    public String _status;
    public String _statusUpload;

    public Contact() {

    }


    // constructor
    public Contact(String date, String f_name, String l_name, String email, String phone, String spouseName, String memberName1, String memberName2 , String _status, String _statusUpload) {
	    this.mfirstname = f_name;
	    this.mlastname = l_name;
        this.mEmail = email;
        this.mPhone = phone;
	    this.mSpouseName = spouseName;
	    this.mMember1 = memberName1;
	    this.mMember2 = memberName2;
	    this.mDate = date;
	    this._status = _status;
        this._statusUpload=_statusUpload;
    }

    public String get_status() {
        return _status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getMfirstname() {
        return mfirstname;
    }

    public void setMfirstname(String mfirstname) {
        this.mfirstname = mfirstname;
    }

    public String getMlastname() {
        return mlastname;
    }

    public void setMlastname(String mlastname) {
        this.mlastname = mlastname;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmSpouseName() {
        return mSpouseName;
    }

    public void setmSpouseName(String mSpouseName) {
        this.mSpouseName = mSpouseName;
    }

    public String getmMember1() {
        return mMember1;
    }

    public void setmMember1(String mMember1) {
        this.mMember1 = mMember1;
    }

    public String getmMember2() {
        return mMember2;
    }

    public void setmMember2(String mMember2) {
        this.mMember2 = mMember2;
    }

    public String get_statusUpload() {
        return _statusUpload;
    }

    public void set_statusUpload(String _statusUpload) {
        this._statusUpload = _statusUpload;
    }

    // constructor
    /*public Contact(int id,String date,String f_name,String l_name,String email,String phone,String spouseName,String memberName1,String memberName2 ,String _status,String _statusUpload) {
        this._id = id;
        this.mfirstname = f_name;
        this.mlastname = l_name;
        this.mEmail = email;
        this.mPhone = phone;
        this.mSpouseName = spouseName;
        this.mMember1 = memberName1;
        this.mMember2 = memberName2;
        this.mDate = date;
        this._status = _status;
        this._statusUpload=_statusUpload;
    }*/






}