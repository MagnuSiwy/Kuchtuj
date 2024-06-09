package com.example.kuchtuj_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RecipesFragment: Fragment(), RecipeAdapter.OnRecipeClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter

    fun getCollectionReferenceForRecipes(): CollectionReference {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return FirebaseFirestore.getInstance().collection("recipes")
            .document(currentUser!!.uid).collection("my_recipes")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recipes, container, false)

        recyclerView = view.findViewById(R.id.recipes_recycler)

        setupRecycleViewer()

        return view
    }

    private fun setupRecycleViewer() {
        val query: Query = getCollectionReferenceForRecipes().orderBy("timestamp", Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<Recipe>()
            .setQuery(query, Recipe::class.java)
            .build()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipeAdapter = RecipeAdapter(options, this)
        recyclerView.adapter = recipeAdapter
    }

    override fun onStart() {
        super.onStart()
        recipeAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        recipeAdapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        recipeAdapter.notifyDataSetChanged()
    }

    override fun onRecipeClick(recipe: Recipe, position: Int) {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.replaceFragment(RecipeDetailsFragment().apply {
            arguments = Bundle().apply {
                putString("name", recipe.name)
                putString("component", recipe.component)
                putString("content", recipe.content)
                val recipeID = recipeAdapter.snapshots.getSnapshot(position).id
                putString("recipeID", recipeID)
            }
        })
    }
}