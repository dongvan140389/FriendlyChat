package com.example.dongvan.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dongvan.friendlychat.R;
import com.example.dongvan.model.UserChat;

/**
 * Created by VoNga on 26-Feb-17.
 */

public class UserAdapter extends ArrayAdapter<UserChat> {

    Activity context;
    int resource;

    public UserAdapter(Activity context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        //Dùng cache view và viewholer
        if (convertView == null) {
            //Create inflater layout
            LayoutInflater inflater = this.context.getLayoutInflater();
            convertView = inflater.inflate(this.resource, null);

            //findViewByID
            holder = new ViewHolder();
            holder.txtOneChar = (TextView) convertView.findViewById(R.id.txtOneChar);
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtEmail = (TextView) convertView.findViewById(R.id.txtEmail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final UserChat userChat = (UserChat) getItem(position);
        String name = userChat.getName().toString();
        String oneChar = name.charAt(0)+"";
        holder.txtName.setText(name);
        holder.txtEmail.setText(userChat.getEmail().toString());
        holder.txtOneChar.setText(oneChar.toUpperCase());

        return convertView;
    }

    class ViewHolder {
        private TextView txtOneChar;
        private TextView txtName;
        private TextView txtEmail;
    }
}
