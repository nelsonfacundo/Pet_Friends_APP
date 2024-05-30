package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.lifecycle.ViewModelProvider

import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.MascotaFirestoreRecyclerAdapter
import com.example.petfriendsapp.entities.Mascota
import com.example.petfriendsapp.viewmodels.ListViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Inicio : Fragment() {


    private lateinit var viewInicio: View
    private lateinit var buttonCambiarPerfil: Button
    lateinit var recMascotas: RecyclerView
    private lateinit var buttonDarEnAdopcion : Button
    private lateinit var viewModel: ListViewModel
    val db = Firebase.firestore

    private lateinit var mascotaClickListener: (Mascota) -> Unit

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)

        checkRecords()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {

        viewInicio = inflater.inflate(R.layout.fragment_inicio, container, false)

        buttonDarEnAdopcion=viewInicio.findViewById(R.id.button_dar_en_adopcion)

        recMascotas = viewInicio.findViewById(R.id.rec_mascota)

        prepareFragment()
        mascotaClickListener = ::redirigir

        return viewInicio

    }

    private fun prepareFragment() {
        recMascotas.setHasFixedSize(true)
        recMascotas.layoutManager = LinearLayoutManager(context)


    }


    override fun onStart() {
        super.onStart()

        fillRecycler()

        initListeners()



    }

    private fun fillRecycler() {

        val rootRef = FirebaseFirestore.getInstance()

        val query = rootRef.collection("mascotas")

        val options = FirestoreRecyclerOptions.Builder<Mascota>()
            .setQuery(query, Mascota::class.java)
            .build()

        val adapter = MascotaFirestoreRecyclerAdapter(options,mascotaClickListener)
        adapter.startListening()
        recMascotas.adapter = adapter
    }

 private fun redirigir(mascota:Mascota){
         val action = InicioDirections.actionInicioToDetailsAdoptar(mascota)
         findNavController().navigate(action)
     }



    private fun checkRecords() {
        db.collection("mascotas").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                        for (mascota in viewModel.mascotas) {
                            db.collection("mascotas").document().get()

                        }
                    }

            }
    }

    private fun initListeners(){
        buttonDarEnAdopcion.setOnClickListener{navigateToDarAdopcionMascota()}
    }

    private fun navigateToDarAdopcionMascota() {
        val action = InicioDirections.actionInicioToDarAdopcionMascotaFragment()
        viewInicio.findNavController().navigate(action)
    }

}




