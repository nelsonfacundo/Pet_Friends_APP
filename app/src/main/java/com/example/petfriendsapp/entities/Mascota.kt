package com.example.petfriendsapp.entities

import android.os.Parcel
import android.os.Parcelable

data class Mascota(
    var raza: String,
    var edad: Int,
    var nombre: String,
    var ubicacion: String,
    var descripcion: String,
    var sexo: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
    )
    override fun toString(): String {
        return "Mascota(raza='$raza', edad=$edad, nombre='$nombre', ubicacion='$ubicacion', descripcion='$descripcion', sexo = '$sexo')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(raza)
        parcel.writeInt(edad)
        parcel.writeString(nombre)
        parcel.writeString(ubicacion)
        parcel.writeString(descripcion)
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
