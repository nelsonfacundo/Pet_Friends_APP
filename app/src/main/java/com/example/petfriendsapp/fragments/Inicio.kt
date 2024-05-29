package fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.MascotaAdapter
import com.example.petfriendsapp.entities.Mascota

class Inicio : Fragment() {
    private lateinit var viewInicio: View
    private lateinit var buttonCambiarPerfil: Button
    lateinit var recMascotas: RecyclerView
    private lateinit var buttonDarEnAdopcion : Button

    /**En este caso voy a dejar la Lista de mascotas asi hasta conectarla con Firebase */
    var mascotas : MutableList<Mascota> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        viewInicio = inflater.inflate(R.layout.fragment_inicio, container, false)

        buttonDarEnAdopcion=viewInicio.findViewById(R.id.button_dar_en_adopcion)

        recMascotas = viewInicio.findViewById(R.id.rec_mascota)
        val linearLayoutManager = LinearLayoutManager(context)
        // Inflate the layout for this fragment
        return viewInicio
    }

    override fun onStart() {
        super.onStart()
        initListeners()

        for(i in 1 .. 10) {
            Log.e("Example", "Error al cargar pokemones")
            mascotas.add(Mascota(
                "Coli",
                5,
                "choco",
                "Padua",
                "Le falta una mano",
                "Macho"
            ))
            mascotas.add(Mascota(
                "Coli2",
                5,
                "choco0",
                "Padua1",
                "Le falta una oreja",
                "Macho"
            ))
            mascotas.add(Mascota(
                "Coli3",
                5,
                "choco1",
                "Padua2",
                "Le falta un ojo",
                "Hembra"
            ))

        }
        recMascotas.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recMascotas.layoutManager = linearLayoutManager

        val mascotaAdapter = MascotaAdapter(mascotas) { mascota ->
            val action = InicioDirections.actionInicioToDetailsAdoptar(mascota)
            findNavController().navigate(action)
        }

        recMascotas.adapter = mascotaAdapter
    }

    private fun initListeners(){
        buttonDarEnAdopcion.setOnClickListener{navigateToDarAdopcionMascota()}
    }

    private fun navigateToDarAdopcionMascota(){
        val action = InicioDirections.actionInicioToDarAdopcionMascotaFragment()
        viewInicio.findNavController().navigate(action)
    }

}


