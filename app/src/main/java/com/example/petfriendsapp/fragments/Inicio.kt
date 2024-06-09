package fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.lifecycle.ViewModelProvider

import android.widget.Button
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.MascotaFirestoreRecyclerAdapter
import com.example.petfriendsapp.entities.Mascota
import com.example.petfriendsapp.fragments.FiltrosFragment
import com.example.petfriendsapp.viewmodels.ListViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class Inicio : Fragment() {

    private lateinit var viewInicio: View
    private lateinit var buttonCambiarPerfil: Button
    lateinit var recMascotas: RecyclerView
    private lateinit var buttonDarEnAdopcion: Button
    private lateinit var viewModel: ListViewModel
    val db = Firebase.firestore
    private lateinit var btnFiltros: Button
    private lateinit var mascotaClickListener: (Mascota, String) -> Unit
    private var especieSeleccionada: String? = null
    private var sexoSeleccionado: String? = null
    private var edadMin: Int = 0
    private var edadMax: Int = Int.MAX_VALUE

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

        buttonDarEnAdopcion = viewInicio.findViewById(R.id.button_dar_en_adopcion)

        recMascotas = viewInicio.findViewById(R.id.rec_mascota)

        prepareFragment()
        mascotaClickListener = { mascota, mascotaId ->
            redirigir(mascota, mascotaId)
        }

        //Abro bottomSheet de filtros
        btnFiltros = viewInicio.findViewById(R.id.btn_filtros)
        btnFiltros.setOnClickListener { showFiltrosBottomSheet() }

        return viewInicio

    }

    private fun prepareFragment() {
        recMascotas.setHasFixedSize(true)
        recMascotas.layoutManager = LinearLayoutManager(context)
    }

    override fun onStart() {
        super.onStart()

        //Recibo filtros de FiltrosFragment y actualizo el recycler
        setFragmentResultListener("requestKey") { _, bundle ->
            especieSeleccionada = bundle.getString("especieSeleccionada")
            sexoSeleccionado = bundle.getString("sexoSeleccionado")
            edadMin = bundle.getInt("edadMin")
            edadMax = bundle.getInt("edadMax")
            fillRecycler()
        }

        fillRecycler()

        initListeners()


    }

    private fun fillRecycler() {

        val rootRef = FirebaseFirestore.getInstance()

        var query: Query = rootRef.collection("mascotas")

        especieSeleccionada?.let {
            if (!it.isNullOrEmpty()) query = query.whereEqualTo("especie", it)
        }

        sexoSeleccionado?.let {
            if (!it.isNullOrEmpty()) query = query.whereEqualTo("sexo", it)
        }

        query = query.whereGreaterThanOrEqualTo("edad", edadMin)
            .whereLessThanOrEqualTo("edad", edadMax)

        val options = FirestoreRecyclerOptions.Builder<Mascota>()
            .setQuery(query, Mascota::class.java)
            .build()

        val adapter = MascotaFirestoreRecyclerAdapter(options, mascotaClickListener)
        adapter.startListening()
        recMascotas.adapter = adapter
    }

    private fun redirigir(mascota: Mascota, mascotaId: String) {
        val action = InicioDirections.actionInicioToDetailsAdoptar(
            mascota,
            mascotaId
        ) // Pasar el id a DetailsAdoptar
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

    private fun initListeners() {
        buttonDarEnAdopcion.setOnClickListener { navigateToDarAdopcionMascota() }
    }

    private fun navigateToDarAdopcionMascota() {
        val action = InicioDirections.actionInicioToDarAdopcionMascotaFragment()
        viewInicio.findNavController().navigate(action)
    }

    private fun showFiltrosBottomSheet() {
        val filtrosBottomSheet = FiltrosFragment()
        filtrosBottomSheet.show(parentFragmentManager, "FiltrosFragment")
    }

}




