package org.digicraft.profiles.ui.profilelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.digicraft.profiles.R;
import org.digicraft.profiles.data.model.Profile;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static org.digicraft.profiles.data.model.Profile.GENDER_FEMALE;
import static org.digicraft.profiles.data.model.Profile.GENDER_MALE;

/**
 * Created by Andrey Koryazhkin on 03-03-2019.
 */

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.ProfileViewHolder> {
    private List<Profile> mDataset;
    private final Context mContext;
    private final OnItemClickListener mClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    ProfileListAdapter(Context context, List<Profile> dataset, OnItemClickListener listener) {
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
        Profile profile = mDataset.get(position);
        holder.tvId.setText(String.valueOf(profile.getId()));
        holder.tvName.setText(profile.getName());
        holder.tvAge.setText(String.valueOf(profile.getAge()));
        holder.tvHobbies.setText(profile.getHobbies());
        holder.mProfile = profile;
        // holder.imgPerson. /// todo: set image
        //holder.container.setBackgroundColor(mContext.getColor(R.color.colorItemBackgroundFemale));
        if (GENDER_MALE.equals(profile.getGender())) {
            holder.container.setBackgroundColor(mContext.getResources().getColor(R.color.colorItemBackgroundMale));
        } else if (GENDER_FEMALE.equals(profile.getGender())) {
            holder.container.setBackgroundColor(mContext.getResources().getColor(R.color.colorItemBackgroundFemale));
        } else {
            holder.container.setBackgroundColor(mContext.getResources().getColor(R.color.colorItemBackgroundWhite));
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    void updateDataset(List<Profile> dataset) {
        mDataset = dataset;
    }

    public interface OnItemClickListener {
        void onItemClick(Profile profile);
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        Profile mProfile = null;
        TextView tvId;
        TextView tvName;
        TextView tvAge;
        TextView tvHobbies;
        ImageView imgPerson;
        View container;

        ProfileViewHolder(View v) {
            super(v);
            tvId = v.findViewById(R.id.tv_id);
            tvName = v.findViewById(R.id.tv_name);
            tvAge = v.findViewById(R.id.tv_age);
            tvHobbies = v.findViewById(R.id.tv_hobbies);
            imgPerson = v.findViewById(R.id.img_person);
            container = v.findViewById(R.id.item_container);
            container.setClickable(true);
            container.setFocusable(true);
            container.setOnClickListener(vv -> mClickListener.onItemClick(mProfile));
        }
    }

}