package com.example.petfriendsapp.entities

import android.os.Parcel
import android.os.Parcelable

data class Solicitud(
    val estado: String = "",
    val idMascota: String = "",
    val idUsuarioAdopta: String = "",
    val idUsuarioDueño: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(estado)
        parcel.writeString(idMascota)
        parcel.writeString(idUsuarioAdopta)
        parcel.writeString(idUsuarioDueño)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Solicitud> {
        override fun createFromParcel(parcel: Parcel): Solicitud {
            return Solicitud(parcel)
        }

        override fun newArray(size: Int): Array<Solicitud?> {
            return arrayOfNulls(size)
        }
    }
}
