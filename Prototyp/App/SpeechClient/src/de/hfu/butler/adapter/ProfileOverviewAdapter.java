package de.hfu.butler.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.hfu.butler.R;
import de.hfu.butler.activities.SignInActivity;
import de.hfu.butler.activities.SignUpActivity;

public class ProfileOverviewAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public ProfileOverviewAdapter(Context context, String[] values) {
        super(context, R.layout.profile_overview_row, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.profile_overview_row, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        Button button = (Button) rowView.findViewById(R.id.button);
        textView.setText(values[position]);

        return rowView;
    }
}
