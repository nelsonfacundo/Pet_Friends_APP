package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.example.petfriendsapp.R

class Inicio : Fragment() {
    lateinit var viewInicio: View
    private lateinit var buttonCambiarPerfil: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewInicio = inflater.inflate(R.layout.fragment_inicio, container, false)

        buttonCambiarPerfil=viewInicio.findViewById(R.id.button)

        return viewInicio
    }

    override fun onStart() {
        super.onStart()
        initListeners()

    }
    private fun initListeners() {
        buttonCambiarPerfil.setOnClickListener { navigateToProfile() }

    }
    private fun navigateToProfile() {
        val action1 = InicioDirections.actionInicioToPerfil()
        viewInicio.findNavController().navigate(action1)
    }

}

