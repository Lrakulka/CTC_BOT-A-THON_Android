package com.google.android.gms.samples.vision.barcodereader.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.samples.vision.barcodereader.R;
import com.google.android.gms.samples.vision.barcodereader.dto.DataModel;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener{

    private ArrayList<DataModel> dataList;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView productName;
        TextView productBarcode;
        EditText productQuantity;
        ImageView product_image;
    }
 
    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.list_view_item, data);
        this.dataList = data;
        this.mContext=context;
 
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;

        switch (v.getId())
        {
            case R.id.product_image:
                Snackbar.make(v, dataModel.getProductInfo(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }
 
    private int lastPosition = -1;
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
 
        final View result;
 
        if (convertView == null) {
 
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_view_item, parent, false);
            viewHolder.product_image = (ImageView) convertView.findViewById(R.id.product_image);
            viewHolder.productName = (TextView) convertView.findViewById(R.id.product_name);
            viewHolder.productBarcode = (TextView) convertView.findViewById(R.id.product_barcode);
            viewHolder.productQuantity = (EditText) convertView.findViewById(R.id.product_quantity);
 
            result=convertView;
 
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
 
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ?
                R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
 
        viewHolder.productName.setText(dataModel.getProductName());
        viewHolder.productBarcode.setText(dataModel.getProductBarcode());
        Resources resources = mContext.getResources();
        viewHolder.product_image.setImageResource(getProductImgId(dataModel, resources));
        viewHolder.product_image.setOnClickListener(this);
        viewHolder.product_image.setTag(position);
        String initialQuantity = viewHolder.productQuantity.getText().toString();
        viewHolder.productQuantity.setText(dataModel.getProductQuantity().toString());
        viewHolder.productQuantity.setId(position);
        viewHolder.productQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    final int id = view.getId();
                    DataModel item = getItem(id);
                    final EditText field = ((EditText) view);
                    item.setProductQuantity(Integer.valueOf(field.getText().toString()));
                }
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    private int getProductImgId(DataModel dataModel, Resources resources) {
        int productImgId = 0;
        if (dataModel.getProductImageURI() != null) {
            productImgId = resources.getIdentifier(
                    dataModel.getProductImageURI().replace(".jpg", ""),
                    "drawable",
                    mContext.getPackageName());
        }
        if (productImgId == 0) {
            productImgId = R.drawable.default_img;
        }
        return productImgId;
    }

    public ArrayList<DataModel> getDataList() {
        return dataList;
    }
}