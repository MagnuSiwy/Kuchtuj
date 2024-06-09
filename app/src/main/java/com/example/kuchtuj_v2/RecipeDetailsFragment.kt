package com.example.kuchtuj_v2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class RecipeDetailsFragment: Fragment() {
    private lateinit var recipeName: EditText
    private lateinit var recipeComponents: EditText
    private lateinit var recipeContent: EditText
    private lateinit var chooseImageButton: Button
    private lateinit var saveRecipeButton: Button
    private lateinit var deleteRecipeButton: TextView
    private lateinit var editButton: ImageButton
    private var name: String? = null
    private var component: String? = null
    private var content: String? = null
    private var recipeID: String? = null
    private var isEditMode = false

    fun enterEditMode() {
        deleteRecipeButton.visibility = View.VISIBLE
        saveRecipeButton.visibility = View.VISIBLE
        chooseImageButton.visibility = View.VISIBLE
        editButton.visibility = View.GONE
    }

    fun getCollectionReferenceForRecipes(): CollectionReference {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return FirebaseFirestore.getInstance().collection("recipes")
            .document(currentUser!!.uid).collection("my_recipes")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recipe_details, container, false)

        recipeName = view.findViewById(R.id.recipe_name)
        recipeComponents = view.findViewById(R.id.recipe_components)
        recipeContent = view.findViewById(R.id.recipe_content)
        chooseImageButton = view.findViewById(R.id.choose_image_button)
        saveRecipeButton = view.findViewById(R.id.save_button)
        deleteRecipeButton = view.findViewById(R.id.delete_recipe_button)
        editButton = view.findViewById(R.id.edit_button)

        name = arguments?.getString("name")
        component = arguments?.getString("component")
        content = arguments?.getString("content")
        recipeID = arguments?.getString("recipeID")

        recipeName.setText(name)
        recipeComponents.setText(component)
        recipeContent.setText(content)

        if (recipeID.isNullOrEmpty()) {
            isEditMode = true
            enterEditMode()
        }

        saveRecipeButton.setOnClickListener { saveRecipe() }
        deleteRecipeButton.setOnClickListener { deleteRecipeFromFirebase() }
        editButton.setOnClickListener {
            isEditMode = true
            enterEditMode()
        }

        return view
    }

    private fun saveRecipe() {
        val recipeNameVal = recipeName.text.toString()
        val recipeComponentsVal = recipeComponents.text.toString()
        val recipeContentVal = recipeContent.text.toString()

        if (recipeNameVal.isEmpty()) {
            recipeName.error = "Recipe name is required"
            return
        }

        val recipe = Recipe().apply {
            name = recipeNameVal
            component = recipeComponentsVal
            content = recipeContentVal
            timestamp = Timestamp.now()
        }

        saveRecipeToFirebase(recipe)
    }

    private fun saveRecipeToFirebase(recipe: Recipe) {
        val documentReference: DocumentReference = if (isEditMode) {
            getCollectionReferenceForRecipes().document(recipeID!!)
        } else {
            getCollectionReferenceForRecipes().document()
        }

        documentReference.set(recipe).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val text = "The recipe has been succesfully added"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(requireContext(), text, duration)
                toast.show()
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            } else {
                val text = "Failed to add the recipe"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(requireContext(), text, duration)
                toast.show()
            }
        }
    }

    private fun deleteRecipeFromFirebase() {
        val documentReference = getCollectionReferenceForRecipes().document(recipeID!!)
        documentReference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val text = "The recipe has been deleted"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(requireContext(), text, duration)
                toast.show()
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
            } else {
                val text = "Failed to delete the recipe"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(requireContext(), text, duration)
                toast.show()
            }
        }
    }
}