package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import com.example.petfriendsapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FiltrosFragment : BottomSheetDialogFragment() {

    private lateinit var ageRange1to3: Chip
    private lateinit var ageRange3to6: Chip
    private lateinit var ageRange6to9: Chip
    private lateinit var ageRangeOver9: Chip
    private lateinit var chipGroupSpecie: ChipGroup
    private lateinit var chipGroupSex: ChipGroup
    private lateinit var chipGroupAge: ChipGroup
    private lateinit var spinnerLocation: Spinner
    private var minAge: Int = 0
    private var maxAge: Int = Int.MAX_VALUE
    private lateinit var applyFilters: Button
    private lateinit var clearFilters: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filtros, container, false)

        initChips(view)
        initSpinnerLocation(view)
        restoreFilters()
        initListenerApplyFilters(view)
        initListenerClearFilters(view)

        return view
    }

    private fun initChips(view: View) {
        ageRange1to3  = view.findViewById(R.id.age_1_3_chip)
        ageRange3to6 = view.findViewById(R.id.age_3_6_chip)
        ageRange6to9 = view.findViewById(R.id.age_6_9_chip)
        ageRangeOver9 = view.findViewById(R.id.age_greater_than_9)
        chipGroupSpecie = view.findViewById(R.id.species_chips)
        chipGroupSex = view.findViewById(R.id.sex_chips)
        chipGroupAge = view.findViewById(R.id.age_chips)
    }

    private fun initSpinnerLocation(view: View) {
        spinnerLocation = view.findViewById(R.id.location_spinner_filters)
        //ArrayAdapter con el array de strings de Strings.xml
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_provincias,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerLocation.adapter = adapter
        }
    }

    //Restauro los filtros de acuerdo a la última vez
    private fun restoreFilters() {

        if (selectedSpecieId != View.NO_ID) {
            selectedSpecieId?.let { chipGroupSpecie.check(it) }
        }

        if (selectedSexId != View.NO_ID) {
            selectedSexId?.let { chipGroupSex.check(it) }
        }

        if (selectedAgeId != View.NO_ID) {
            selectedAgeId?.let { chipGroupAge.check(it) }
        }

        if (selectedLocation != null) {
            spinnerLocation.setSelection(resources.getStringArray(R.array.spinner_provincias).indexOf(selectedLocation))
        }
    }

    private fun initListenerApplyFilters(view: View) {
        applyFilters = view.findViewById(R.id.apply_filters_btn)

        //Botón aplicar filtros
        applyFilters.setOnClickListener {
            //Guardo los id de los chips seleccionados para restaurarlos la próxima vez que se inicie el bottomsheet
            selectedSpecieId = chipGroupSpecie.checkedChipId
            selectedSexId = chipGroupSex.checkedChipId
            selectedAgeId = chipGroupAge.checkedChipId

            if (spinnerLocation.selectedItemPosition == 0) {
                selectedLocation = null
            } else {
                selectedLocation = spinnerLocation.selectedItem.toString()
            }

            //Capturo filtros seleccionados para pasarlos al InicioFragment
            val selectedSpecie = selectedSpecieId
                .let {id ->view.findViewById<Chip>(id)?.text?.toString()}

            val selectedSex = selectedSexId
                .let {id ->view.findViewById<Chip>(id)?.text?.toString()}

            //Capturo rango de edad seleccionado
            setAgeRange()

            //Guardo los filtros
            val result = Bundle().apply {
                putString("selectedSpecies", selectedSpecie)
                putString("selectedSex", selectedSex)
                putInt("minAge", minAge)
                putInt("maxAge", maxAge)
                putString("selectedLocation", selectedLocation)
            }

            //Paso de los filtros al inicio
            parentFragmentManager.setFragmentResult("requestKey", result)

            //Cierro bottomSheet
            dismiss()
        }
    }

    private fun setAgeRange(){
        if (ageRange1to3.isChecked){
            minAge = 1
            maxAge = 3
        } else if (ageRange3to6.isChecked){
            minAge = 3
            maxAge = 6
        } else if (ageRange6to9.isChecked){
            minAge = 6
            maxAge = 9
        } else if (ageRangeOver9.isChecked){
            minAge = 9
            maxAge = Int.MAX_VALUE
        }
    }

    private fun initListenerClearFilters(view: View) {
        clearFilters = view.findViewById(R.id.clean_filters_btn)
        //Botón limpiar filtros
        clearFilters.setOnClickListener{

            //Modifico las variables para cuando se inicie el bottomsheet se restablezcan los chips
            selectedSpecieId = -1
            selectedSexId = -1
            selectedAgeId = -1
            selectedLocation = null

            //Bundle para pasar los filtros en null al inicio
            val result = Bundle().apply {
                putString("selectedSpecies", null)
                putString("selectedSex", null)
                putInt("minAge", 0)
                putInt("maxAge", Int.MAX_VALUE)
                putString("selectedLocation", null)
            }

            parentFragmentManager.setFragmentResult("requestKey", result)
            dismiss()
        }
    }

    companion object {
        const val TAG = "FiltrosFragment"
        var selectedSpecieId: Int = -1
        var selectedSexId: Int = -1
        var selectedAgeId: Int = -1
        var selectedLocation: String? = null
    }
}