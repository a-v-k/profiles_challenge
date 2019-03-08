package org.digicraft.profiles.ui.profilelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.digicraft.profiles.R;
import org.digicraft.profiles.data.model.Person;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static org.digicraft.profiles.data.model.Person.GENDER_FEMALE;
import static org.digicraft.profiles.data.model.Person.GENDER_MALE;

/**
 * Created by Andrey Koryazhkin on 03-03-2019.
 */

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ProfileViewHolder> {
    private List<Person> mDataset;
    private final Context mContext;
    private final OnItemClickListener mClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    ProfileListAdapter(Context context, List<Person> dataset, OnItemClickListener listener) {
        mDataset = dataset;
        mContext = context;
        mClickListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ProfileListAdapter.ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_list_item, parent, false);
        return new ProfileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        Person person = mDataset.get(position);
        holder.tvId.setText(String.valueOf(person.getId()));
        holder.tvName.setText(person.getName());
        holder.tvAge.setText(String.valueOf(person.getAge()));
        holder.tvHobbies.setText(person.getHobbies());
        holder.person = person;
        // holder.imgPerson. /// todo: set image
        //holder.container.setBackgroundColor(mContext.getColor(R.color.colorItemBackgroundFemale));
        if (GENDER_MALE.equals(person.getGender())) {
            holder.container.setBackgroundColor(mContext.getResources().getColor(R.color.colorItemBackgroundMale));
        } else if (GENDER_FEMALE.equals(person.getGender())) {
            holder.container.setBackgroundColor(mContext.getResources().getColor(R.color.colorItemBackgroundFemale));
        } else {
            holder.container.setBackgroundColor(mContext.getResources().getColor(R.color.colorItemBackgroundWhite));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    void updateDataset(List<Person> dataset) {
        mDataset = dataset;
    }

    public interface OnItemClickListener {
        void onItemClick(Person person);
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        Person person = null;
        TextView tvId;
        TextView tvName;
        TextView tvAge;
        TextView tvHobbies;
        ImageView imgPerson;
        View container;

        ProfileViewHolder(View v) {
            super(v);
            tvId = v.findViewById(R.id.tvId);
            tvName = v.findViewById(R.id.tvName);
            tvAge = v.findViewById(R.id.tvAge);
            tvHobbies = v.findViewById(R.id.tvHobbies);
            imgPerson = v.findViewById(R.id.imgPerson);
            container = v.findViewById(R.id.item_container);
            container.setClickable(true);
            container.setFocusable(true);
            container.setOnClickListener(vv -> mClickListener.onItemClick(person));
        }
    }

}