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
import com.example.petfriendsapp.components.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class DarAdopcionMascotaFragment : Fragment() {

    private lateinit var buttonBack: ImageView
    private lateinit var buttonDarAdopcionMascota: Button
    private lateinit var viewDarAdopcionMascota: View
    private lateinit var buttonSeleccionarFoto: Button
    private lateinit var editTextEspecie: Spinner
    private lateinit var editTextNombre: EditText
    private lateinit var editTextEdad: EditText
    private lateinit var editTextUbicacion: Spinner
    private lateinit var editTextSexo: Spinner
    private lateinit var editTextDescripcion: EditText
    private lateinit var imageViewFotoMascota: ImageView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var imageUri: Uri? = null
    private lateinit var loadingDialog: LoadingDialog

    private val PICK_IMAGE_REQUEST = 1

    companion object {

        val BUTTON_DAR_ADOPCION_MASCOTA = R.id.button_dar_adopcion_mascota
        val EDIT_TEXT_ESPECIE = R.id.especieMascota
        val EDIT_TEXT_NOMBRE = R.id.nombreMascota
        val EDIT_TEXT_EDAD = R.id.edadMascota
        val EDIT_TEXT_UBICACION = R.id.direccionMascota
        val EDIT_TEXT_SEXO = R.id.sexoMascota
        val EDIT_TEXT_DESCRIPCION = R.id.descripcionMascota
        val BUTTON_SELECCIONAR_FOTO = R.id.button_seleccionar_foto_mascota
        val IMAGE_VIEW_FOTO_MASCOTA =R.id.imageView_foto_mascota
        val BUTTON_BACK = R.id.id_back_fragment_veterinaria
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDarAdopcionMascota = inflater.inflate(R.layout.fragment_dar_adopcion_mascota, container, false)
        initViews()
        initListeners()
        initSpinners()
        loadingDialog = LoadingDialog(requireContext())
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
    editTextNombre= viewDarAdopcionMascota.findViewById(EDIT_TEXT_NOMBRE)
    editTextEdad= viewDarAdopcionMascota.findViewById(EDIT_TEXT_EDAD)
    editTextUbicacion= viewDarAdopcionMascota.findViewById(EDIT_TEXT_UBICACION)
    editTextSexo= viewDarAdopcionMascota.findViewById(EDIT_TEXT_SEXO)
    editTextDescripcion= viewDarAdopcionMascota.findViewById(EDIT_TEXT_DESCRIPCION)
    imageViewFotoMascota= viewDarAdopcionMascota.findViewById(IMAGE_VIEW_FOTO_MASCOTA)
    buttonBack = viewDarAdopcionMascota.findViewById(BUTTON_BACK)
}
    private fun initListeners(){
        buttonBack.setOnClickListener { navigateToHome() }
        buttonSeleccionarFoto.setOnClickListener{abrirGaleria()}
        buttonDarAdopcionMascota.setOnClickListener { enviarForm() }
    }

    private fun enviarForm() {
        val validationResult = validarDatos()
        if (!validationResult.first) {
            val errorMessage = validationResult.second
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        loadingDialog.show() // Mostrar el diálogo de carga mientras se envía el formulario

        val nombre = editTextNombre.text.toString().trim()
        val ubicacion = editTextUbicacion.selectedItem.toString()
        val descripcion = editTextDescripcion.text.toString().trim()
        val edadText = editTextEdad.text.toString().trim()
        val edad = edadText.toInt()
        val especie = editTextEspecie.selectedItem.toString()
        val sexo = editTextSexo.selectedItem.toString()
        val userId = auth.currentUser?.uid

        if (userId != null && imageUri != null) {
            // Subir imagen a Firebase Storage
            val storageRef = FirebaseStorage.getInstance().reference.child("mascotas/${System.currentTimeMillis()}_${userId}.jpg")
            storageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // Obtener URL de descarga
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                            // Crear un mapa de datos
                            val mascotaData = hashMapOf(
                                "nombre" to nombre,
                                "ubicacion" to ubicacion,
                                "descripcion" to descripcion,
                                "edad" to edad,
                                "especie" to especie,
                                "sexo" to sexo,
                                "userId" to userId,
                                "imageUrl" to imageUrl,
                                "estado" to "pendiente" // Agregar el estado inicial
                            )

                        // Guardar datos en Firestore
                        db.collection("mascotas").add(mascotaData)
                            .addOnSuccessListener { documentReference ->
                                loadingDialog.dismiss() // Ocultar el diálogo de carga después de completar el envío
                                Toast.makeText(requireContext(), "Mascota registrada con éxito!", Toast.LENGTH_SHORT).show()
                                navigateToInicio()
                            }
                            .addOnFailureListener { e ->
                                loadingDialog.dismiss() // Ocultar el diálogo de carga en caso de error
                                Toast.makeText(requireContext(), "Error al registrar la mascota: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    loadingDialog.dismiss() // Ocultar el diálogo de carga en caso de error
                    Toast.makeText(requireContext(), "Error al subir la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            loadingDialog.dismiss() // Ocultar el diálogo de carga si hay un problema con el usuario o la imagen
            Toast.makeText(requireContext(), "Usuario no autenticado o imagen no seleccionada", Toast.LENGTH_SHORT).show()
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

        val optionsUbicacion = resources.getStringArray(R.array.spinner_provincias)
        val adapterUbicacion = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, optionsUbicacion)
        adapterUbicacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        editTextUbicacion.adapter = adapterUbicacion
    }

    private fun validarDatos():Pair<Boolean,String>{
        val nombre = editTextNombre.text.toString().trim()
        val descripcion = editTextDescripcion.text.toString().trim()
        val edad = editTextEdad.text.toString().trim()


        if (nombre.isEmpty()) return Pair(false, getString(R.string.txt_empty_nombre))
        if (nombre.length < 3 || nombre.length > 25) return Pair(false, getString(R.string.txt_cantC_nombre_mascota))
        if (descripcion.isEmpty()) return Pair(false, getString(R.string.txt_empty_descripcion))
        if(edad.isEmpty()) return Pair(false, getString(R.string.txt_empty_edad))
        val number = edad.toInt()
        if(number < 0 || number > 100) return Pair(false, getString(R.string.txt_empty_edad_incorrecta))
        if (imageUri == null) return Pair(false, getString(R.string.txt_empty_imagen))
        if (editTextEspecie.selectedItemPosition == 0) return Pair(false, getString(R.string.txt_opcion_valida_especie))
        if (editTextSexo.selectedItemPosition == 0) return Pair(false, getString(R.string.txt_opcion_valida_sexo))
        if (editTextUbicacion.selectedItemPosition == 0) return Pair(false, getString(R.string.txt_opcion_valida_provincia))
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

    private fun navigateToHome() {
        val action1 = DarAdopcionMascotaFragmentDirections.actionDarAdopcionMascotaFragmentToInicio()
        viewDarAdopcionMascota.findNavController().navigate(action1)
    }
}