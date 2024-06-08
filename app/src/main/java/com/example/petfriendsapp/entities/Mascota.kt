package com.example.petfriendsapp.entities

import android.os.Parcel
import android.os.Parcelable

data class Mascota(
    val nombre: String = "",
    val especie: String = "",
    val edad: Int = 0,
    val ubicacion: String = "",
    val descripcion: String = "",
    val imageUrl: String = "",
    val userId: String = "",
    val sexo: String=""

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()

    )
    override fun toString(): String {
        return "Mascota(especie='$especie', nombre='$nombre', edad=$edad, ubicacion='$ubicacion', descripcion='$descripcion',imageUrl ='$imageUrl', userId='$userId',sexo='$sexo')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(especie)
        parcel.writeString(nombre)
        parcel.writeInt(edad)
        parcel.writeString(ubicacion)
        parcel.writeString(descripcion)
        parcel.writeString(imageUrl)
        parcel.writeString(userId)
        parcel.writeString(sexo)


    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mascota> {
        override fun createFromParcel(parcel: Parcel): Mascota {
            return Mascota(parcel)
        }

        override fun newArray(size: Int): Array<Mascota?> {
            return arrayOfNulls(size)
        }
    }

}
