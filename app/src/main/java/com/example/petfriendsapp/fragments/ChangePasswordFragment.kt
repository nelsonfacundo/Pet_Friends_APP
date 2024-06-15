package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.petfriendsapp.R
import com.example.petfriendsapp.components.LoadingDialog
import com.example.petfriendsapp.databinding.FragmentCambiarPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordFragment : Fragment() {

    private lateinit var bindingFragment: FragmentCambiarPasswordBinding
    private val binding get() = bindingFragment

    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingFragment = FragmentCambiarPasswordBinding.inflate(inflater, container, false)
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
        binding.icBackCambiarPassword.setOnClickListener { navigateToProfile() }
        binding.buttonResetPassword.setOnClickListener { changePassword() }
    }

    private fun changePassword() {
        val viejaPass = binding.textViejoPassword.text.toString().trim()
        val nuevaPass = binding.textNuevoPassword.text.toString().trim()
        val confirmNuevaPass = binding.textConfirmNuevoPassword.text.toString().trim()

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
        val action = ChangePasswordFragmentDirections.actionCambiarPasswordToPerfil()
        binding.root.findNavController().navigate(action)    }
}