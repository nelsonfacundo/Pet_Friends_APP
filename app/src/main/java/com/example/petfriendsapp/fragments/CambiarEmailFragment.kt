
package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.petfriendsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider

class CambiarEmailFragment : Fragment() {
    lateinit var viewCambiarEmail: View
    private lateinit var viejoEmailEditText: EditText
    private lateinit var nuevoEmailEditText: EditText
    private lateinit var passwordEmailEditText: EditText
    private lateinit var buttonCambiarEmail: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var backButton: ImageView

    companion object {
        val BACK_BUTTON_ID = R.id.ic_back_cambiar_email
        val EMAIL_VIEJO = R.id.text_viejo_email
        val EMAIL_NUEVO = R.id.text_nuevo_email
        val PASSWORD_EMAIL_NUEVO = R.id.text_cambiar_email_password
        val CHAGE_EMAIL_BUTTON = R.id.button_reset_email
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewCambiarEmail = inflater.inflate(R.layout.fragment_cambiar_email, container, false)

        initViews()
        initFirebase()

        return viewCambiarEmail
    }
    override fun onStart() {
        super.onStart()
        initListeners()

    }
    private fun initViews() {
        backButton = viewCambiarEmail.findViewById(BACK_BUTTON_ID)
        viejoEmailEditText = viewCambiarEmail.findViewById(EMAIL_VIEJO)
        nuevoEmailEditText = viewCambiarEmail.findViewById(EMAIL_NUEVO)
        passwordEmailEditText = viewCambiarEmail.findViewById(PASSWORD_EMAIL_NUEVO)
        buttonCambiarEmail = viewCambiarEmail.findViewById(CHAGE_EMAIL_BUTTON)
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
    }

    private fun initListeners() {
        backButton.setOnClickListener { navigateToProfile() }
        buttonCambiarEmail.setOnClickListener { changeEmail() }
    }
    private fun changeEmail() {
        val viejoEmail = viejoEmailEditText.text.toString().trim()
        val nuevoEmail = nuevoEmailEditText.text.toString().trim()
        val password = passwordEmailEditText.text.toString().trim()

        if (validateInputs(viejoEmail, nuevoEmail, password)) {
            val user = auth.currentUser
            if (user != null) {
                // Reauthenticate the user
                val credential = EmailAuthProvider.getCredential(viejoEmail, password)
                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            // Update the email
                            val errorMessage = reauthTask.exception?.message
                            Log.e("ReauthError", "Error during reauthentication: $errorMessage")
                            user. verifyBeforeUpdateEmail(nuevoEmail)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(requireContext(), R.string.email_changed_successfully, Toast.LENGTH_LONG).show()
                                        navigateToProfile()
                                    } else {
                                        Toast.makeText(requireContext(), R.string.email_change_failed, Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            Toast.makeText(requireContext(), R.string.reauthentication_failed, Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), R.string.user_not_logged_in, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateInputs(viejoEmail: String, nuevoEmail: String, password: String): Boolean {
        if (viejoEmail.isEmpty() || nuevoEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), R.string.msj_campos_vacios, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun navigateToProfile() {
        val action = CambiarEmailFragmentDirections.actionCambiarEmailToPerfil()
        viewCambiarEmail.findNavController().navigate(action)

    }
}