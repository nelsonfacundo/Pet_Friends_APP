package com.example.petfriendsapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Mascota
import com.example.petfriendsapp.holders.MascotaUserHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class MascotaUserFirestoreRecycletAdapter(
    private val context: Context,
    options: FirestoreRecyclerOptions<Mascota>,
    private val clickListener: (Mascota, String) -> Unit
) : FirestoreRecyclerAdapter<Mascota, MascotaUserHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaUserHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mascota_delete, parent, false)
        return MascotaUserHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaUserHolder, position: Int, model: Mascota) {
        holder.setName(model.nombre)
        holder.setEdad(model.edad)
        holder.setRaza(model.especie)
        holder.setSexo(model.sexo)
        holder.setImageUrl(model.imageUrl)

        val mascotaId = snapshots.getSnapshot(position).id

        holder.getCardLayout().setOnClickListener {
            clickListener(model, mascotaId)
        }

        holder.setOnDeleteClickListener {
            showConfirmationDialog(mascotaId)
        }
    }

    private fun deleteMascota(mascotaId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("mascotas").document(mascotaId).delete()
            .addOnSuccessListener {
                Toast.makeText(context, R.string.txt_delete_pet_success, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Ocurrió un error al intentar eliminar la mascota
                Toast.makeText(context, R.string.txt_delete_pet_error, Toast.LENGTH_SHORT).show()
            }
    }

    private fun showConfirmationDialog(mascotaId: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.txt_dialog_delete_title)
            .setMessage(R.string.txt_dialog_detele_message)
            .setPositiveButton(R.string.txt_dialog_delete_confirm) { _, _ ->
                deleteMascota(mascotaId)
            }
            .setNegativeButton(R.string.txt_dialog_delete_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
