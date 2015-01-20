package ru.perm.trubnikov.gps2sms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RepoListAdapter extends ArrayAdapter<String> {

    protected final Context context;
    protected final String[] firstlines;
    protected final String[] secondlines;

    public RepoListAdapter(Context context, String[] firstlines, String[] secondlines) {
        super(context, R.layout.choose_fav_list_item, secondlines); // !!!
        this.context = context;
        this.firstlines = firstlines;
        this.secondlines = secondlines;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.choose_fav_list_item, parent, false);
        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondLine = (TextView) rowView.findViewById(R.id.secondLine);

        firstLine.setText(firstlines[position]);
        secondLine.setText(secondlines[position]);
        //imageView.setImageDrawable(icons[position]);

        return rowView;
    }


}
