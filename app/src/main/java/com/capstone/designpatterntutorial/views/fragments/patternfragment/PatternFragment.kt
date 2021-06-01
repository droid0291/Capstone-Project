package com.capstone.designpatterntutorial.views.fragments.patternfragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.capstone.designpatterntutorial.R
import com.capstone.designpatterntutorial.model.mainscreen.Pattern
import com.capstone.designpatterntutorial.services.FavoriteDbService
import com.capstone.designpatterntutorial.views.activities.HomeActivity
import com.capstone.designpatterntutorial.views.fragments.BaseFragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Created by gubbave on 5/19/2017.
 */
class PatternFragment : BaseFragment() {
    var description: WebView? = null
    var favorite: FloatingActionButton? = null
    var collapsingToolbar: CollapsingToolbarLayout? = null
    private var patternData: Pattern? = null
    override fun getLayoutId(): Int {
        return R.layout.detail_fragment
    }

    override fun loadFragmentArguments() {
        if (arguments != null) {
            patternData = arguments.getParcelable(PATTERN_DETAILS)
        }
    }

    override fun initFragment(rootView: View?) {
        super.initFragment(rootView)
        (activity as HomeActivity?)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)

/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
*/collapsingToolbar.setTitle(patternData.getName())
        collapsingToolbar.setExpandedTitleColor(resources.getColor(android.R.color.transparent))
        description.loadData(patternData.getDescription(), "text/html", null)
        favorite.setImageResource(if (patternData.isFavorite()) R.drawable.icon_favorite_selected else R.drawable.icon_favorite_unselected)
        favorite.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, FavoriteDbService::class.java)
            if (patternData.isFavorite()) {
                favorite.setImageResource(R.drawable.icon_favorite_unselected)
                patternData.setFavorite(false)
                intent.putExtra(FavoriteDbService.Companion.OPERATION_FAVORITE, FavoriteDbService.Companion.OPERATION_REMOVE)
            } else {
                favorite.setImageResource(R.drawable.icon_favorite_selected)
                patternData.setFavorite(true)
                intent.putExtra(FavoriteDbService.Companion.OPERATION_FAVORITE, FavoriteDbService.Companion.OPERATION_ADD)
            }
            intent.putExtra(FavoriteDbService.Companion.PATTERN, patternData)
            activity.startService(intent)
        })
    }

    override fun handleNavigationClick() {
        activity.onBackPressed()
    }

    companion object {
        private val PATTERN_DETAILS: String? = "PatternDetails"
        fun newInstance(pattern: Pattern?): Fragment? {
            requireNotNull(pattern) { "Pattern cannot be NULL" }
            val args = Bundle()
            args.putParcelable(PATTERN_DETAILS, pattern)
            val fragment: Fragment = PatternFragment()
            fragment.arguments = args
            return fragment
        }
    }
}