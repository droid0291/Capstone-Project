package com.capstone.designpatterntutorial.di

import com.capstone.designpatterntutorial.presenters.FavoritePresenter
import com.capstone.designpatterntutorial.presenters.HomePresenter
import com.capstone.designpatterntutorial.views.activities.HomeActivity
import com.capstone.designpatterntutorial.views.fragments.FavoriteListFragment
import com.capstone.designpatterntutorial.views.fragments.NavigationMenuFragment
import com.capstone.designpatterntutorial.views.fragments.categoryfragment.CategoryFragment
import dagger.Component
import javax.inject.Singleton

/**
 * Created by venugopalraog on 4/27/17.
 */
@Singleton
@Component(modules = [BaseModuleApplication::class])
interface AppComponent {
    fun inject(activity: HomeActivity?)
    fun inject(homePresenter: HomePresenter?)
    fun inject(categoryFragment: CategoryFragment?)
    fun inject(navigationMenuFragment: NavigationMenuFragment?)
    fun inject(favoritePresenter: FavoritePresenter?)
    fun inject(favoriteListFragment: FavoriteListFragment?)
}