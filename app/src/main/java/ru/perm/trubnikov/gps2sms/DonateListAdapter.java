package ru.perm.trubnikov.gps2sms;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DonateListAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] titles;
    private final String[] descs;
    private final Drawable[] icons;
    private final Integer[] states; // purchases states
    private final Drawable icon2;

    public DonateListAdapter(Context context, String[] product_ids, String[] titles, String[] descs, Drawable[] icons, Integer[] states, Drawable icon2) {
        super(context, R.layout.choose_fav_list_item, product_ids); // !!!
        this.context = context;
        this.titles = titles;
        this.descs = descs;
        this.icons = icons;
        this.states = states;
        this.icon2 = icon2;
    }

    public void setStates(int idx, int val) {
        this.states[idx] = val;
    }

    public void setDescs(int idx, String val) {
        this.descs[idx] = val;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.choose_fav_list_item, parent, false);
        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondLine = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        firstLine.setText(titles[position]);
        if (!descs[position].equalsIgnoreCase("")) {
            secondLine.setText(descs[position]);
        }
        imageView.setImageDrawable(icons[position]);

        if (states[position] > 0) {
            ImageView imageView2 = (ImageView) rowView.findViewById(R.id.icon2);
            imageView2.setImageDrawable(icon2);
        }

        return rowView;
    }

}