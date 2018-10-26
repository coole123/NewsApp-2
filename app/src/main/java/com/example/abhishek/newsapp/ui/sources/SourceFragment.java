package com.example.abhishek.newsapp.ui.sources;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import com.example.abhishek.newsapp.R;
import com.example.abhishek.newsapp.adapters.SourceAdapter;
import com.example.abhishek.newsapp.databinding.FragmentSourceBinding;
import com.example.abhishek.newsapp.models.Source;
import com.example.abhishek.newsapp.models.Specification;
import com.example.abhishek.newsapp.utils.RecyclerViewDecoration;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class SourceFragment extends Fragment implements SourceAdapter.SourceAdapterListener {

    private final SourceAdapter sourceAdapter = new SourceAdapter(null, this);
    private FragmentSourceBinding binding;
    private View selectedView = null;

    public SourceFragment() {
        // Required empty public constructor
    }

    public static SourceFragment newInstance() {
        return new SourceFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_source, container, false);

        setupViewModel();
        binding.rvSources.setAdapter(sourceAdapter);
        binding.rvSources.addItemDecoration(new RecyclerViewDecoration(
                Math.round(getResources().getDimension(R.dimen.rv_horizontal_offset)),
                Math.round(getResources().getDimension(R.dimen.rv_vertical_offset))
        ));
        if (getContext() != null) {
            DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));
            binding.rvSources.addItemDecoration(divider);
        }

        return binding.getRoot();
    }

    private void setupViewModel() {
        SourceViewModel viewModel = ViewModelProviders.of(this).get(SourceViewModel.class);
        Specification specification = new Specification();
        specification.setLanguage(Locale.getDefault().getLanguage());
        specification.setCountry(null);
        viewModel.getSource(specification).observe(this, new Observer<List<Source>>() {
            @Override
            public void onChanged(@Nullable List<Source> sources) {
                if (sources != null) {
                    sourceAdapter.setSources(sources);
                }
            }
        });
    }


    @Override
    public void onSourceItemClicked(Source source) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(source.getUrl()));
        startActivity(intent);
    }

    @Override
    public void onSourceDropDownClicked(View view, ConstraintLayout constraintLayout) {
        ConstraintSet constraintSet = new ConstraintSet();
        TextView textView = constraintLayout.findViewById(R.id.tv_source_desc);
        TransitionSet transition = new TransitionSet();
        transition.addTransition(new ChangeBounds());
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.5f));
        if (textView.getHeight() == 0) {
            constraintSet.clone(view.getContext(), R.layout.source_item_rotated);
            constraintSet.setRotation(R.id.imageButton, 180);
            transition.setDuration(950);
        } else {
            constraintSet.clone(view.getContext(), R.layout.source_item);
            constraintSet.setRotation(R.id.imageButton, 0);
            transition.setDuration(500);
        }
        constraintSet.applyTo(constraintLayout);
        TransitionManager.beginDelayedTransition(constraintLayout, transition);
    }
}
