package com.example.kuchtuj_v2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.bumptech.glide.Glide
import java.util.UUID


class RecipeDetailsFragment: Fragment() {
    private lateinit var recipeName: EditText
    private lateinit var recipeComponents: EditText
    private lateinit var recipeContent: EditText
    private lateinit var chooseImageButton: Button
    private lateinit var saveRecipeButton: Button
    private lateinit var deleteRecipeButton: TextView
    private lateinit var editButton: ImageButton
    private lateinit var recipeImage: ImageView
    private var image: String? = null
    private var name: String? = null
    private var component: String? = null
    private var content: String? = null
    private var recipeID: String? = null
    private var isCreationMode = false
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    fun enterEditMode() {
        deleteRecipeButton.visibility = View.VISIBLE
        saveRecipeButton.visibility = View.VISIBLE
        chooseImageButton.visibility = View.VISIBLE
        editButton.visibility = View.GONE
    }

    fun enterCreationMode() {
        deleteRecipeButton.visibility = View.GONE
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
        recipeImage = view.findViewById(R.id.recipe_image)

        image = arguments?.getString("image")
        name = arguments?.getString("name")
        component = arguments?.getString("component")
        content = arguments?.getString("content")
        recipeID = arguments?.getString("recipeID")

        recipeName.setText(name)
        recipeComponents.setText(component)
        recipeContent.setText(content)

        if (!image.isNullOrEmpty()) {
            Glide.with(this).load(image).into(recipeImage)
        }

        if (recipeID.isNullOrEmpty()) {
            isCreationMode = true
            enterCreationMode()
        }

        if (isCreationMode) {
            recipeID = getCollectionReferenceForRecipes().document().id
        }

        saveRecipeButton.setOnClickListener { saveRecipe() }
        deleteRecipeButton.setOnClickListener { deleteRecipeFromFirebase() }
        chooseImageButton.setOnClickListener { selectImage() }
        editButton.setOnClickListener {
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

    private fun saveRecipeToFirestore(recipe: Recipe) {
        val documentReference: DocumentReference = if (isCreationMode) {
            getCollectionReferenceForRecipes().document(recipeID!!)
        } else {
            getCollectionReferenceForRecipes().document(recipeID!!)
        }

        documentReference.set(recipe).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "The recipe has been successfully added", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            } else {
                Toast.makeText(requireContext(), "Failed to add the recipe", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveRecipeToFirebase(recipe: Recipe) {
        if (filePath != null) {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef = storageRef.child("recipe_images/${UUID.randomUUID()}\"")
            val uploadTask = imageRef.putFile(filePath!!)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    recipe.image = downloadUri.toString()

                    saveRecipeToFirestore(recipe)
                }
                else {
                    val text = "Failed to upload the image"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(requireContext(), text, duration)
                    toast.show()
                }
            }
        }
        else {
            saveRecipeToFirestore(recipe)
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

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null) {

            filePath = data.data
            recipeImage.setImageURI(filePath)
        }
    }

}