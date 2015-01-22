package ru.perm.trubnikov.gps2sms;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by A on 22.01.2015.
 */
public class DonateListAdapter  extends ArrayAdapter<String> {
    private final Context context;
    private final String[] titles;
    private final String[] descrs;
    private final Drawable[] icons;

    public DonateListAdapter(Context context, String[] product_ids, String[] titles, String[] descrs, Drawable[] icons) {
        super(context, R.layout.choose_fav_list_item, product_ids); // !!!
        this.context = context;
        this.titles = titles;
        this.descrs = descrs;
        this.icons = icons;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.choose_fav_list_item, parent, false);
        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondLine = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        firstLine.setText(titles[position]);
        secondLine.setText(descrs[position]);
        imageView.setImageDrawable(icons[position]);

        return rowView;
    }

}