package com.example.petfriendsapp.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.petfriendsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class DarAdopcionMascotaFragment : Fragment() {


    private lateinit var buttonDarAdopcionMascota: Button
    private lateinit var viewDarAdopcionMascota: View
    private lateinit var buttonSeleccionarFoto: Button
    private lateinit var editTextEspecie: Spinner
    private lateinit var editTextRaza: EditText
    private lateinit var editTextNombre: EditText
    private lateinit var editTextEdad: EditText
    private lateinit var editTextUbicacion: EditText
    private lateinit var editTextSexo: Spinner
    private lateinit var editTextDescripcion: EditText
    private lateinit var imageViewFotoMascota: ImageView
    private var imageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    companion object {

        val BUTTON_DAR_ADOPCION_MASCOTA = R.id.button_dar_adopcion_mascota
        val EDIT_TEXT_ESPECIE = R.id.especieMascota
        val EDIT_TEXT_RAZA = R.id.razaMascota
        val EDIT_TEXT_NOMBRE = R.id.nombreMascota
        val EDIT_TEXT_EDAD = R.id.edadMascota
        val EDIT_TEXT_UBICACION = R.id.direccionMascota
        val EDIT_TEXT_SEXO = R.id.sexoMascota
        val EDIT_TEXT_DESCRIPCION = R.id.descripcionMascota
        val BUTTON_SELECCIONAR_FOTO = R.id.button_seleccionar_foto_mascota
        val IMAGE_VIEW_FOTO_MASCOTA =R.id.imageView_foto_mascota
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewDarAdopcionMascota = inflater.inflate(R.layout.fragment_dar_adopcion_mascota, container, false)
        initViews()
        initListeners()
        initSpinners()
        return viewDarAdopcionMascota
    }
    override fun onStart() {
        super.onStart()
        initListeners()
    }
private fun initViews(){
    buttonDarAdopcionMascota= viewDarAdopcionMascota.findViewById(BUTTON_DAR_ADOPCION_MASCOTA)
    buttonSeleccionarFoto= viewDarAdopcionMascota.findViewById(BUTTON_SELECCIONAR_FOTO)
    editTextEspecie= viewDarAdopcionMascota.findViewById(EDIT_TEXT_ESPECIE)
    editTextRaza= viewDarAdopcionMascota.findViewById(EDIT_TEXT_RAZA)
    editTextNombre= viewDarAdopcionMascota.findViewById(EDIT_TEXT_NOMBRE)
    editTextEdad= viewDarAdopcionMascota.findViewById(EDIT_TEXT_EDAD)
    editTextUbicacion= viewDarAdopcionMascota.findViewById(EDIT_TEXT_UBICACION)
    editTextSexo= viewDarAdopcionMascota.findViewById(EDIT_TEXT_SEXO)
    editTextDescripcion= viewDarAdopcionMascota.findViewById(EDIT_TEXT_DESCRIPCION)
    imageViewFotoMascota= viewDarAdopcionMascota.findViewById(IMAGE_VIEW_FOTO_MASCOTA)
}
    private fun initListeners(){
buttonSeleccionarFoto.setOnClickListener{abrirGaleria()}
        buttonDarAdopcionMascota.setOnClickListener { enviarForm() }

    }
private fun enviarForm(){
    val validationResult = validarDatos()
    if(validationResult.first){
        val nombre = editTextNombre.text.toString().trim()
        val ubicacion = editTextUbicacion.text.toString().trim()
        val descripcion = editTextDescripcion.text.toString().trim()
        val edadText = editTextEdad.text.toString().trim()
        val edad = edadText.toInt()
        val raza = editTextRaza.text.toString().trim()
        val especie = editTextEspecie.toString()
        val sexo = editTextSexo.toString()
        val userId = auth.currentUser


    }
    else{
        Toast.makeText(requireContext(), validationResult.second, Toast.LENGTH_SHORT).show()
    }
}
    private fun initSpinners(){
        val optionsSexo = resources.getStringArray(R.array.spinner_sexo)
        val adapterSexo = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, optionsSexo)
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        editTextSexo.adapter = adapterSexo

        val optionsEspecie = resources.getStringArray(R.array.spinner_especie)
        val adapterEspecie = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, optionsEspecie)
        adapterEspecie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        editTextEspecie.adapter = adapterEspecie
    }

    private fun validarDatos():Pair<Boolean,String>{
        val nombre = editTextNombre.text.toString().trim()
        val ubicacion = editTextUbicacion.text.toString().trim()
        val descripcion = editTextDescripcion.text.toString().trim()
        val edad = editTextEdad.text.toString().trim()
        val raza = editTextRaza.text.toString().trim()
        val especie = editTextEspecie.toString()
        val sexo = editTextSexo.toString()


        if (nombre.isEmpty()) return Pair(false, getString(R.string.txt_empty_nombre))
        if (nombre.length < 3 || nombre.length > 25) return Pair(false, getString(R.string.txt_cantC_nombre_mascota))
        if (ubicacion.isEmpty()) return Pair(false, getString(R.string.txt_empty_ubicacion))
        if (descripcion.isEmpty()) return Pair(false, getString(R.string.txt_empty_descripcion))
        if(edad.isEmpty()) return Pair(false, getString(R.string.txt_empty_edad))
        val number = edad.toInt()
        if(number < 0 || number > 100) return Pair(false, getString(R.string.txt_empty_edad_incorrecta))
        if (imageUri == null) return Pair(false, getString(R.string.txt_empty_imagen))
        if(raza.isEmpty()) return Pair(false, getString(R.string.txt_empty_raza))
        if (raza.length < 3 || raza.length > 25) return Pair(false, getString(R.string.txt_cantC_raza))
        if(especie == "Seleccionar especie") return Pair(false, getString(R.string.txt_opcion_valida_especie))
        if(sexo == "Seleccionar sexo") return Pair(false, getString(R.string.txt_opcion_valida_sexo))

        return Pair(true,"")
    }

    private fun abrirGaleria() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, ""),
            PICK_IMAGE_REQUEST
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            if (imageUri != null) {
                Glide.with(this)
                    .load(imageUri)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(250)))
                    .into(imageViewFotoMascota)
            } else {
                Log.e("DataFormActivity", "Image URI is null")
                Toast.makeText(requireContext(), "Error al seleccionar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }
private fun navigateToInicio(){
    val action = DarAdopcionMascotaFragmentDirections.actionDarAdopcionMascotaFragmentToInicio()
    viewDarAdopcionMascota.findNavController().navigate(action)
}
}