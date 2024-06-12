package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.petfriendsapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FiltrosFragment : BottomSheetDialogFragment() {

    private lateinit var ageRange1to3: Chip
    private lateinit var ageRange3to6: Chip
    private lateinit var ageRange6to9: Chip
    private lateinit var ageRangeOver9: Chip
    private var minAge: Int = 0
    private var maxAge: Int = Int.MAX_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filtros, container, false)

        val applyFilters: Button = view.findViewById(R.id.btn_aplicar_filtros)
        val clearFilters: Button = view.findViewById(R.id.btn_limpiar_filtros)
        ageRange1to3  = view.findViewById(R.id.chip_edad_1_3)
        ageRange3to6 = view.findViewById(R.id.chip_edad_3_6)
        ageRange6to9 = view.findViewById(R.id.chip_edad_6_9)
        ageRangeOver9 = view.findViewById(R.id.chip_edad_mas_9)

        applyFilters.setOnClickListener {
            //Capturo filtros seleccionados
            val selectedSpecies = view.findViewById<ChipGroup>(R.id.chips_especie)
                .checkedChipId.let {id ->view.findViewById<Chip>(id)?.text?.toString()}

            val selectedSex = view.findViewById<ChipGroup>(R.id.chips_sexo)
                .checkedChipId.let {id ->view.findViewById<Chip>(id)?.text?.toString()}

            //Capturo rango de edad seleccionado
            setAgeRange()

            //Guardo los filtros
            val result = Bundle().apply {
                putString("selectedSpecies", selectedSpecies)
                putString("selectedSex", selectedSex)
                putInt("minAge", minAge)
                putInt("maxAge", maxAge)
            }

            //Paso de los filtros al inicio
            parentFragmentManager.setFragmentResult("requestKey", result)

            //Cierro bottomSheet
            dismiss()
        }

        clearFilters.setOnClickListener{
            val result = Bundle().apply {
                putString("selectedSpecies", null)
                putString("selectedSex", null)
                putInt("minAge", 0)
                putInt("maxAge", Int.MAX_VALUE)
            }

            parentFragmentManager.setFragmentResult("requestKey", result)
            dismiss()
        }

        return view
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

    companion object {
        const val TAG = "FiltrosFragment"
    }

}