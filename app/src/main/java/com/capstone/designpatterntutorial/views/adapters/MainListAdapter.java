package com.capstone.designpatterntutorial.views.adapters;

import androidx.appcompat.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.designpatterntutorial.R;
import com.capstone.designpatterntutorial.model.mainscreen.Pattern;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gubbave on 5/8/2017.
 */

public class MainListAdapter extends RecyclerView.Adapter <MainListAdapter.DetailsViewHolder> {

    private List<Pattern> mPatternList;
    private OnItemClickListener mListener;

    public MainListAdapter(List<Pattern> patternList) {
        this.mPatternList = patternList;
    }

    public void setmPatternList(List<Pattern> mPatternList) {
        this.mPatternList = mPatternList;
    }

    @Override
    public DetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new DetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailsViewHolder holder, int position) {
        holder.bindData(getItemData(position), position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    private Pattern getItemData(int position) {
        return mPatternList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_design_pattern;
    }

    @Override
    public int getItemCount() {
        return mPatternList.size();
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_design_pattern_title)
        TextView mTitle;

        @BindView(R.id.item_design_pattern_description)
        TextView mIntent;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(Pattern pattern, final int position) {
            mTitle.setText(pattern.getName());
            mIntent.setText(pattern.getIntent());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mListener != null) {
                        mListener.onItemClick(v, position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
