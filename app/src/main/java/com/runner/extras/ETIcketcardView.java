package com.runner.extras;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;

import com.runner.R;
import com.runner.model.TicketModel;
import com.squareup.picasso.Picasso;



@Layout(R.layout.item_koloda)
public class ETIcketcardView {

    TicketModel model;
    @View(R.id.ivuser)
    private ImageView ivuser;
    private SwipecardCallback swipecardCallback;
    private Context mContext;
    private SwipePlaceHolderView mSwipePlaceHolderView;


    public ETIcketcardView(Context context, SwipePlaceHolderView swipePlaceHolderView, TicketModel model) {
        this.mContext = context;
        this.swipecardCallback = (SwipecardCallback) context;
        this.mSwipePlaceHolderView = swipePlaceHolderView;
        this.model = model;

    }

    @Resolve
    private void onResolve(){
        /*Glide.with(mContext).load(mMovie.getImageUrl()).apply(RequestOptions.centerCropTransform()).into(imageView);
        textViewName.setText(mMovie.getName());*/
        Picasso.with(mContext).load(model.getE_ticket()).into(ivuser);
    }

    @SwipeIn
    private void onSwipeIn(){
        swipecardCallback.onSwipeIn();
    }

    @SwipeOut
    private void onSwipeOut(){
        swipecardCallback.onSwipeOut();
    }

}