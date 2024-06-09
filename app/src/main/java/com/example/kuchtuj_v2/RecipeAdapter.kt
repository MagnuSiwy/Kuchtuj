package com.example.kuchtuj_v2

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import java.util.Locale

class RecipeAdapter(options: FirestoreRecyclerOptions<Recipe>, private val listener: OnRecipeClickListener) : FirestoreRecyclerAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(options) {

    interface OnRecipeClickListener {
        fun onRecipeClick(recipe: Recipe, position: Int)
    }

    fun timestampToString(timestamp: Timestamp): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp.toDate())
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int, recipe: Recipe) {
        if (!recipe.image.isNullOrEmpty()) {
            Glide.with(holder.itemView.context).load(recipe.image).into(holder.recipeImage)
        }
        holder.recipeName.text = recipe.name
        holder.recipeTimestamp.text = timestampToString(recipe.timestamp!!)

        holder.itemView.setOnClickListener {
            listener.onRecipeClick(recipe, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeImage: ImageView = itemView.findViewById(R.id.recipe_image)
        val recipeName: TextView = itemView.findViewById(R.id.recipe_name)
        val recipeTimestamp: TextView = itemView.findViewById(R.id.recipe_timestamp)
    }

}