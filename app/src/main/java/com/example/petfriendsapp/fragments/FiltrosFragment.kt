package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import com.example.petfriendsapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class FiltrosFragment : BottomSheetDialogFragment() {

    private lateinit var rangoEdad1a3: Chip
    private lateinit var rangoEdad3a6: Chip
    private lateinit var rangoEdad6a9: Chip
    private lateinit var rangoEdadMas9: Chip
    private var edadMin: Int = 0
    private var edadMax: Int = Int.MAX_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filtros, container, false)

        val aplicarFiltros: Button = view.findViewById(R.id.btn_aplicar_filtros)
        val limpiarFiltros: Button = view.findViewById(R.id.btn_limpiar_filtros)
        rangoEdad1a3  = view.findViewById(R.id.chip_edad_1_3)
        rangoEdad3a6 = view.findViewById(R.id.chip_edad_3_6)
        rangoEdad6a9 = view.findViewById(R.id.chip_edad_6_9)
        rangoEdadMas9 = view.findViewById(R.id.chip_edad_mas_9)

        aplicarFiltros.setOnClickListener {
            //Capturo filtros seleccionados
            val especieSeleccionada = view.findViewById<ChipGroup>(R.id.chips_especie)
                .checkedChipId.let {id ->view.findViewById<Chip>(id)?.text?.toString()}

            val sexoSeleccionado = view.findViewById<ChipGroup>(R.id.chips_sexo)
                .checkedChipId.let {id ->view.findViewById<Chip>(id)?.text?.toString()}

            //Capturo rango de edad seleccionado
            setRangoEdad()

            //Guardo los filtros
            val result = Bundle().apply {
                putString("especieSeleccionada", especieSeleccionada)
                putString("sexoSeleccionado", sexoSeleccionado)
                putInt("edadMin", edadMin)
                putInt("edadMax", edadMax)
            }

            //Paso de los filtros al inicio
            parentFragmentManager.setFragmentResult("requestKey", result)

            //Cierro bottomSheet
            dismiss()
        }

        limpiarFiltros.setOnClickListener{
            val result = Bundle().apply {
                putString("especieSeleccionada", null)
                putString("sexoSeleccionado", null)
                putInt("edadMin", 0)
                putInt("edadMax", Int.MAX_VALUE)
            }

            parentFragmentManager.setFragmentResult("requestKey", result)
            dismiss()
        }

        return view
    }

    private fun setRangoEdad(){
        if (rangoEdad1a3.isChecked){
            edadMin = 1
            edadMax = 3
        } else if (rangoEdad3a6.isChecked){
            edadMin = 3
            edadMax = 6
        } else if (rangoEdad6a9.isChecked){
            edadMin = 6
            edadMax = 9
        } else if (rangoEdadMas9.isChecked){
            edadMin = 9
            edadMax = Int.MAX_VALUE
        }
    }

    companion object {
        const val TAG = "FiltrosFragment"
    }

}