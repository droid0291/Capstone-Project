package com.capstone.designpatterntutorial.views.adapters;

import androidx.appcompat.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.designpatterntutorial.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gubbave on 5/8/2017.
 */

public class NavigationMenuAdapter extends RecyclerView.Adapter<NavigationMenuAdapter.DetailsViewHolder> {

    private String[] menuList;
    private OnItemClickListener mListener;

    private int selectedItem = 0;

    public NavigationMenuAdapter(String[] patternList) {
        this.menuList = patternList;
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

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    private String getItemData(int position) {
        return menuList[position];
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_navigation_menu;
    }

    @Override
    public int getItemCount() {
        return menuList.length;
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.menu_title)
        TextView title;

        @BindView(R.id.menu_selected)
        View selectedMenu;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(String menuTitle, final int position) {

            title.setText(menuTitle);

            if (selectedItem == position)
                selectedMenu.setVisibility(View.VISIBLE);
            else
                selectedMenu.setVisibility(View.GONE);

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
