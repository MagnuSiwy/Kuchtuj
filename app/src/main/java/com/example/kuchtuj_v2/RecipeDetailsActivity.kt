package com.example.kuchtuj_v2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment

class RecipeDetailsActivity: Fragment() {

    lateinit var recipeName: EditText
    lateinit var recipeComponents: EditText
    lateinit var recipeContent: EditText
    lateinit var saveRecipeButton: ImageButton
    lateinit var deleteRecipeButton: TextView
    var title: String? = null
    var content: String? = null
    var docId: String? = null
    var isEditMode = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_recipe_details, container, false)

        recipeName = view.findViewById(R.id.recipe_name)
        recipeComponents = view.findViewById(R.id.recipe_components)
        recipeContent = view.findViewById(R.id.recipe_content)
        saveRecipeButton = view.findViewById(R.id.save_button)
        deleteRecipeButton = view.findViewById(R.id.delete_recipe_button)

        title = arguments?.getString("title")
        content = arguments?.getString("content")
        docId = arguments?.getString("docId")

        if (!docId.isNullOrEmpty()) {
            isEditMode = true
        }

        recipeContent.setText(content)
        if (isEditMode) {
            deleteRecipeButton.visibility = View.VISIBLE
        }

        saveRecipeButton.setOnClickListener { saveNote() }

        deleteRecipeButton.setOnClickListener { deleteNoteFromFirebase() }

        return view
    }

    private fun saveNote() {
        val noteTitle = recipeName.text.toString()
        val noteContent = recipeContent.text.toString()
        if (noteTitle.isNullOrEmpty()) {
            recipeName.error = "Name is required"
            return
        }
        val note = Note().apply {
            setTitle(noteTitle)
            setContent(noteContent)
            setTimestamp(Timestamp.now())
        }

        saveNoteToFirebase(note)
    }

    private fun saveNoteToFirebase(note: Note) {
        val documentReference: DocumentReference = if (isEditMode) {
            // update the note
            Utility.getCollectionReferenceForNotes().document(docId!!)
        } else {
            // create new note
            Utility.getCollectionReferenceForNotes().document()
        }

        documentReference.set(note).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // note is added
                Utility.showToast(requireContext(), "Note added successfully")
                activity?.finish()
            } else {
                Utility.showToast(requireContext(), "Failed while adding note")
            }
        }
    }

    private fun deleteNoteFromFirebase() {
        val documentReference = Utility.getCollectionReferenceForNotes().document(docId!!)
        documentReference.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // note is deleted
                Utility.showToast(requireContext(), "Note deleted successfully")
                activity?.finish()
            } else {
                Utility.showToast(requireContext(), "Failed while deleting note")
            }
        }
    }
}

}