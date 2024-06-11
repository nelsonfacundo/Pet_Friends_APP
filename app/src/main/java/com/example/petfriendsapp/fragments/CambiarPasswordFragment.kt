package com.example.petfriendsapp.fragments

import android.os.Bundle
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
import com.example.petfriendsapp.components.LoadingDialog
import com.google.firebase.auth.FirebaseAuth


class CambiarPasswordFragment : Fragment() {
    lateinit var viewCambiarPassword: View
    private lateinit var viejaPassword: EditText
    private lateinit var nuevaPassowrd: EditText
    private lateinit var confirmarNuevaPassowrd: EditText
    private lateinit var buttonCambiarPassword: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var backButton: ImageView
    private lateinit var loadingDialog: LoadingDialog


    companion object {
        val BACK_BUTTON_ID = R.id.ic_back_cambiar_password
        val PASSWORD_VIEJA = R.id.text_viejo_password
        val PASSWORD_NUEVA = R.id.text_nuevo_password
        val CONFIRME_PASSWORD_NUEVA = R.id.text_confirm_nuevo_password
        val CHAGE_PASSWORD_BUTTON = R.id.button_reset_password
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewCambiarPassword = inflater.inflate(R.layout.fragment_cambiar_password, container, false)

        initViews()
        initFirebase()
        loadingDialog = LoadingDialog(requireContext())
        return viewCambiarPassword
    }
    override fun onStart() {
        super.onStart()
        initListeners()

    }
    private fun initViews() {
        backButton = viewCambiarPassword.findViewById(BACK_BUTTON_ID)
        viejaPassword = viewCambiarPassword.findViewById(PASSWORD_VIEJA)
        nuevaPassowrd = viewCambiarPassword.findViewById(PASSWORD_NUEVA)
        confirmarNuevaPassowrd = viewCambiarPassword.findViewById(CONFIRME_PASSWORD_NUEVA)
        buttonCambiarPassword = viewCambiarPassword.findViewById(CHAGE_PASSWORD_BUTTON)
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
    }

    private fun initListeners() {
        backButton.setOnClickListener { navigateToProfile() }
        buttonCambiarPassword.setOnClickListener { changePassword() }
    }

    private fun changePassword() {
        val viejaPass = viejaPassword.text.toString().trim()
        val nuevaPass = nuevaPassowrd.text.toString().trim()
        val confirmNuevaPass = confirmarNuevaPassowrd.text.toString().trim()

        if (validateInputs(viejaPass, nuevaPass, confirmNuevaPass)) {
            loadingDialog.show()
            val user = auth.currentUser //auth.currentUser -> recupera el usuario autenticado
            if (user != null) {
                user.updatePassword(nuevaPass)
                    .addOnCompleteListener { task ->
                        loadingDialog.dismiss()
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), R.string.password_changed_successfully, Toast.LENGTH_LONG).show()
                            navigateToProfile()
                        } else {
                            Toast.makeText(requireContext(), R.string.password_change_failed, Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                loadingDialog.dismiss()
                Toast.makeText(requireContext(), R.string.user_not_logged_in_password, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateInputs(viejaPass: String, nuevaPass: String, confirmPass: String): Boolean {
        if (viejaPass.isEmpty() || nuevaPass.isEmpty()|| confirmPass.isEmpty()) {
            Toast.makeText(requireContext(), R.string.msj_campos_vacios, Toast.LENGTH_LONG).show()
            return false
        }else if (nuevaPass != confirmPass) {
            Toast.makeText(requireContext(), R.string.msj_pass_no_coinciden, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun navigateToProfile() {
        val action = CambiarPasswordFragmentDirections.actionCambiarPasswordToPerfil()
        viewCambiarPassword.findNavController().navigate(action)
    }
}