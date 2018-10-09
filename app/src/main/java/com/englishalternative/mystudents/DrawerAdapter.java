package com.englishalternative.mystudents;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Roman on 21.04.2017.
 * This is an adapter to populate DrawerLayout
 */

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    DrawerItem[] drawerItems;
    Context context;
    int layoutResourceId;


    public DrawerAdapter(Context c, int layoutResourceId, DrawerItem[] drawerItems) {
        super(c, layoutResourceId, drawerItems);
        this.drawerItems = drawerItems;
        this.context = c;
        this.layoutResourceId = layoutResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Create item holder to store View references if none is present
        DrawerItemHolder itemHolder;
        View view = convertView;

        if (view == null) {
            view = ((Activity) context).getLayoutInflater().inflate(
                    layoutResourceId, parent, false);


            itemHolder = new DrawerItemHolder();
            itemHolder.itemName = (TextView) view.findViewById(R.id.drawer_itemName);
            itemHolder.itemImage = (ImageView) view.findViewById(R.id.drawer_icon);

            view.setTag(itemHolder);
        } else {
            itemHolder = (DrawerItemHolder) view.getTag();
        }

        // Populate item with data
        itemHolder.itemName.setText(drawerItems[position].getText());
        itemHolder.itemImage.setImageResource(drawerItems[position].getImageResourceId());

        return view;
    }

    private static class DrawerItemHolder {
        TextView itemName;
        ImageView itemImage;
    }
}
