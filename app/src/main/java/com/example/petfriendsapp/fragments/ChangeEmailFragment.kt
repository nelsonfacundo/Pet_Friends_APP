
package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.petfriendsapp.R
import com.example.petfriendsapp.components.LoadingDialog
import com.example.petfriendsapp.databinding.FragmentCambiarEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider

class ChangeEmailFragment : Fragment() {

    private lateinit var bindingFragment: FragmentCambiarEmailBinding
    private val binding get() = bindingFragment

    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingFragment = FragmentCambiarEmailBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFirebase()
        loadingDialog = LoadingDialog(requireContext())
    }

    override fun onStart() {
        super.onStart()
        initListeners()
    }
    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
    }

    private fun initListeners() {
        binding.icBackCambiarEmail.setOnClickListener { navigateToProfile() }
        binding.buttonResetEmail.setOnClickListener { changeEmail() }
    }

    private fun changeEmail() {
        val viejoEmail = binding.textViejoEmail.text.toString().trim()
        val nuevoEmail = binding.textNuevoEmail.text.toString().trim()
        val password = binding.textCambiarEmailPassword.text.toString().trim()

        if (validateInputs(viejoEmail, nuevoEmail, password)) {
            loadingDialog.show()
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
                            user.verifyBeforeUpdateEmail(nuevoEmail)
                                .addOnCompleteListener { updateTask ->
                                    loadingDialog.dismiss()
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(requireContext(), R.string.email_changed_successfully, Toast.LENGTH_LONG).show()
                                        navigateToProfile()
                                    } else {
                                        Toast.makeText(requireContext(), R.string.email_change_failed, Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            loadingDialog.dismiss()
                            Toast.makeText(requireContext(), R.string.reauthentication_failed, Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                loadingDialog.dismiss()
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
        val action = ChangeEmailFragmentDirections.actionCambiarEmailToPerfil()
        binding.root.findNavController().navigate(action)
    }

}