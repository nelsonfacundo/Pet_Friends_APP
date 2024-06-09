package com.example.petfriendsapp.entities

import android.os.Parcel
import android.os.Parcelable

data class Resenia (
    val valoracion: Int = 0,
    val resenia: String = "",

) : Parcelable {
    constructor(parcel: Parcel): this(
        parcel.readInt(),
        parcel.readString().toString(),

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(valoracion)
        parcel.writeString(resenia)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Resenia> {
        override fun createFromParcel(parcel: Parcel): Resenia {
            return Resenia(parcel)
        }

        override fun newArray(size: Int): Array<Resenia?> {
            return arrayOfNulls(size)
        }
    }


}