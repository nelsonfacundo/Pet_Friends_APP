package com.example.petfriendsapp.fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.petfriendsapp.R
import com.example.petfriendsapp.databinding.FragmentDarReviewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class DarReviewFragment : Fragment() {

    private lateinit var bindingFragment: FragmentDarReviewBinding
    private val binding get() = bindingFragment

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
   // private lateinit var userIdOwner: String
    //private lateinit var solicitedId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingFragment = FragmentDarReviewBinding.inflate(inflater, container, false)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.textOpinionReview.filters = arrayOf(InputFilter.LengthFilter(150))


        //val args: DarReviewFragmentArgs by navArgs()
        //userIdOwner = args.ownerId
        //solicitedId = args.soliId

        return binding.root
    }
    override fun onStart() {
        super.onStart()
        initListeners()

    }
    private fun initListeners() {
        binding.btnEnviarReview.setOnClickListener { sendData() }
        binding.btnCancelarReview.setOnClickListener { btnCancel() }
    }

    private fun sendData() {
        val reviewData = obtenerDataDesdeFront()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            if (validationData(reviewData)) return

            addReviewToDatabase(reviewData, userId)
        }
    }

    private fun validationData(reviewData: Map<String, Any>): Boolean {
        val expGeneralRating = reviewData["valoracion"] as Int
        val condicionRating = reviewData["condicionRating"] as Int
        val comunicacionRating = reviewData["comunicacionRating"] as Int
        val opinion = reviewData["opinion"] as String

        if (expGeneralRating == 0 || condicionRating == 0 || comunicacionRating == 0) {
            showToast(R.string.txt_review_rating_error)
            return true
        }

        if (opinion.length < 5) {
            showToast(R.string.txt_opinion_length_min_error)
            return true
        }

        if (opinion.length > 150) {
            showToast(R.string.txt_opinion_length_error)
            return true
        }
        return false
    }

    private fun obtenerDataDesdeFront(): Map<String, Any> {
        val expGeneralRating = binding.expGenRatingBarDarReview.rating.toInt()
        val condicionRating = binding.condicionRatingBarDarReview.rating.toInt()
        val comunicacionRating = binding.comunicacionRatingBarDarReview.rating.toInt()
        val opinion = binding.textOpinionReview.text.toString()

        return hashMapOf(
            "valoracion" to expGeneralRating,
            "condicionRating" to condicionRating,
            "comunicacionRating" to comunicacionRating,
            "opinion" to opinion,
            "timestamp" to FieldValue.serverTimestamp()
        )
    }

    private fun addReviewToDatabase(
        review: Map<String, Any>,
        userIdOwner: String
    ) {
        val userRef = db.collection("users").document(userIdOwner).collection("ratings")

        userRef.add(review)
            .addOnSuccessListener {
                Log.d(TAG, "Reseña agregada correctamente")
                showToast(R.string.txt_review_success)
                navigateToHome()
            }

        // Actualiza el estado de la solicitud
        /*db.collection("peticiones").document(solicitudId)
            .update("Review", "true")
            .addOnSuccessListener {
                Log.d(TAG, "Estado de la solicitud actualizado a completado")
                navigateToHome()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al actualizar el estado de la solicitud", e)
                showToast(R.string.txt_review_error)
            }


            .addOnFailureListener { e ->
                Log.w(TAG, "Error al agregar la reseña", e)
                showToast(R.string.txt_review_error)
            }*/
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(requireContext(), messageResId, Toast.LENGTH_LONG).show()
    }

    private fun btnCancel() {
        alertCancelReview()
    }

    private fun alertCancelReview() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.title_cancel_review)
        builder.setMessage(R.string.txt_pregunta_cancelar_review)
        builder.setPositiveButton(R.string.txt_aceptar_review) { dialog, _ ->
            navigateToHome()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancelar) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
    private fun navigateToHome() {
        val action1 = DarReviewFragmentDirections.actionDarReviewFragmentToInicio()
        binding.root.findNavController().navigate(action1)
    }
}