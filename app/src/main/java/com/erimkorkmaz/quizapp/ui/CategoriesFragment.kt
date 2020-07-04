package com.erimkorkmaz.quizapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.erimkorkmaz.quizapp.R
import com.erimkorkmaz.quizapp.viewmodel.CategoriesViewModel
import kotlinx.android.synthetic.main.fragment_categories.*

class CategoriesFragment : Fragment() {

    private lateinit var categoriesViewModel: CategoriesViewModel
    private lateinit var categoriesAdapter: CategoriesAdapter

    private var images = arrayOf(R.drawable.general, R.drawable.book, R.drawable.films, R.drawable.music, R.drawable.theatre,
                            R.drawable.tv, R.drawable.videogames,R.drawable.boardgames, R.drawable.nature, R.drawable.computer,
                            R.drawable.maths, R.drawable.mythology, R.drawable.sportss, R.drawable.geography, R.drawable.history,
                            R.drawable.politics, R.drawable.art, R.drawable.celebrities, R.drawable.animals, R.drawable.vehicle,
                            R.drawable.comics, R.drawable.gadgets, R.drawable.anime, R.drawable.cartoons)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        return inflater.inflate(R.layout.fragment_categories,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        categoriesViewModel = ViewModelProvider(this).get(CategoriesViewModel::class.java)
        categoriesRecyclerView.layoutManager = GridLayoutManager(context,2,RecyclerView.VERTICAL,false)

        if (::categoriesAdapter.isInitialized.not()){
            loadData()
        } else {
            categoriesRecyclerView.adapter = categoriesAdapter
            hideProgress()
        }
    }

    private fun loadData() : Boolean{
        showProgress()
        categoriesViewModel.categoryLiveData.observe(viewLifecycleOwner, Observer {
            categoriesAdapter = CategoriesAdapter(it.toMutableList(), images)
            categoriesRecyclerView.adapter =categoriesAdapter
            hideProgress()
        })
        return true
    }

    private fun showProgress(){
        progress_categories.visibility = View.VISIBLE
        progress_categories.setAnimation("loading.json")
        progress_categories.playAnimation()
        progress_categories.loop(true)
        categoriesRecyclerView.visibility = View.GONE
    }

    private fun hideProgress() {
        progress_categories.visibility = View.GONE
        categoriesRecyclerView.visibility = View.VISIBLE
    }
}