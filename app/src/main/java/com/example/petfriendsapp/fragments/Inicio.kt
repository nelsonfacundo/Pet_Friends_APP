package fragments

import android.os.Bundle
import android.util.Log
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
import com.example.petfriendsapp.fragments.FirestoreDataManager
import com.example.petfriendsapp.viewmodels.ListViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class Inicio : Fragment() {

    private lateinit var viewInicio: View
    private lateinit var buttonCambiarPerfil: Button
    lateinit var recMascotas: RecyclerView
    private lateinit var buttonDarEnAdopcion: Button
    private lateinit var viewModel: ListViewModel
    private lateinit var firestoreDataManager: FirestoreDataManager
    val db = Firebase.firestore
    private lateinit var btnFilters: Button
    private lateinit var mascotaClickListener: (Mascota, String) -> Unit
    private var selectedSpecies: String? = null
    private var selectedSex: String? = null
    private var minAge: Int = 0
    private var maxAge: Int = Int.MAX_VALUE
    private var selectedLocation: String? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this)[ListViewModel::class.java]

        checkRecords()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewInicio = inflater.inflate(R.layout.fragment_inicio, container, false)

        buttonDarEnAdopcion = viewInicio.findViewById(R.id.button_dar_en_adopcion)

        recMascotas = viewInicio.findViewById(R.id.rec_mascota)
        firestoreDataManager = FirestoreDataManager()

        prepareFragment()
        mascotaClickListener = { mascota, mascotaId ->
            redirigir(mascota, mascotaId)
        }

        btnFilters = viewInicio.findViewById(R.id.btn_filters)
        btnFilters.setOnClickListener { showFiltrosBottomSheet() }

        return viewInicio

    }

    private fun prepareFragment() {
        recMascotas.setHasFixedSize(true)
        recMascotas.layoutManager = LinearLayoutManager(context)
    }

    override fun onStart() {
        super.onStart()

        setFragmentResultListener("requestKey") { _, bundle ->
            selectedSpecies = bundle.getString("selectedSpecies")
            selectedSex = bundle.getString("selectedSex")
            minAge = bundle.getInt("minAge")
            maxAge = bundle.getInt("maxAge")
            selectedLocation = bundle.getString("selectedLocation")
            fillRecycler()
        }

        fillRecycler()

        initListeners()
    }

    private fun fillRecycler() {
        val rootRef = FirebaseFirestore.getInstance()

        var query: Query = rootRef.collection("mascotas")

        selectedSpecies?.let {
            if (!it.isNullOrEmpty()) query = query.whereEqualTo("especie", it)
        }

        selectedSex?.let {
            if (!it.isNullOrEmpty()) query = query.whereEqualTo("sexo", it)
        }

        query = query.whereGreaterThanOrEqualTo("edad", minAge)
            .whereLessThanOrEqualTo("edad", maxAge)

        selectedLocation?.let {
            if (!it.isNullOrEmpty()) query = query.whereEqualTo("ubicacion", it)
        }

        // Add condition to filter by estado "pendiente"
        query = query.whereEqualTo("estado", "pendiente")

        val options = FirestoreRecyclerOptions.Builder<Mascota>()
            .setQuery(query, Mascota::class.java)
            .build()

        val adapter =
            MascotaFirestoreRecyclerAdapter(options, firestoreDataManager, mascotaClickListener)
        adapter.startListening()
        recMascotas.adapter = adapter
    }

    private fun redirigir(mascota: Mascota, mascotaId: String) {
        Log.d("Fragment", "Redirigiendo a detalles para Mascota ID: $mascotaId + $mascotaId")
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