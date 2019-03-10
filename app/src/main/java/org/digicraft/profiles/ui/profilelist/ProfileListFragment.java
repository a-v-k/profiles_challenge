package org.digicraft.profiles.ui.profilelist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.digicraft.profiles.R;
import org.digicraft.profiles.data.model.Profile;
import org.digicraft.profiles.data.viewmodel.ProfileListViewModel;
import org.digicraft.profiles.ui.profileadd.ProfileAddFragment;
import org.digicraft.profiles.ui.profileview.ProfileViewFragment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileListFragment extends Fragment implements ProfileListAdapter.OnItemClickListener {

    private ProfileListViewModel mViewModel;
    private RecyclerView mRwProfileList;
    private ProfileListAdapter mProfileListAdapter;

    // TODO: 3/7/19 correct icon on FAB
    // TODO: 3/7/19 change colors

    public static ProfileListFragment newInstance() {
        return new ProfileListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //noinspection ConstantConditions
        mViewModel = ViewModelProviders.of(getActivity()).get(ProfileListViewModel.class);

        mViewModel.getProfileListLiveData().observe(this, profileList -> {
            if (profileList != null) {
                mProfileListAdapter.updateDataset(profileList);
                mProfileListAdapter.notifyDataSetChanged();
            }
        });

        mViewModel.refreshProfileList();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_list_fragment, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        //noinspection ConstantConditions
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> showProfileAddFragment());

        mRwProfileList = view.findViewById(R.id.rw_profile_list);
        mRwProfileList.setHasFixedSize(true); // todo: check it
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRwProfileList.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.white_separator));
        mRwProfileList.addItemDecoration(dividerItemDecoration);
        mRwProfileList.setLayoutManager(layoutManager);
        mProfileListAdapter = new ProfileListAdapter(getContext(), new ArrayList<>(), this);
        mRwProfileList.setAdapter(mProfileListAdapter);

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // TODO: 3/10/19 make icons for filter and sort
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (id == R.id.action_sort) {
            showSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        new FilterDialogBuilder().build(getContext(), mViewModel.getFilterType(),
                (dialog, filterType) -> mViewModel.setFilterType(filterType)).show();
    }

    private void showSortDialog() {
        new SortDialogBuilder().build(getContext(), mViewModel.getSortType(),
                (dialog, sortType) -> mViewModel.setSortType(sortType)).show();
    }

    private void showProfileAddFragment() {
        //noinspection ConstantConditions
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ProfileAddFragment fragment = ProfileAddFragment.newInstance();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.content, fragment, "fragment_add_profile");
        transaction.addToBackStack("fragment_add_profile");
        transaction.commit();
    }

    @Override
    public void onItemClick(Profile profile) {
        if (profile != null && profile.getId() != null) {
            showProfileViewFragment(profile.getId());
        }
    }

    private void showProfileViewFragment(Integer profileId) {
        Fragment fragment = ProfileViewFragment.newInstance(profileId);
        //noinspection ConstantConditions
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.content, fragment, "fragment_add_profile");
        transaction.addToBackStack("fragment_add_profile");
        transaction.commit();
    }


}
