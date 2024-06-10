package com.example.petfriendsapp.entities

import android.os.Parcel
import android.os.Parcelable





data class Review (
    val valoracion : Int = 0,
    val comunicacionRating : Int = 0,
    val condicionRating : Int = 0,
    val opinion : String = "",




    ) : Parcelable {
    constructor(parcel: Parcel): this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(valoracion)
        parcel.writeInt(comunicacionRating)
        parcel.writeInt(condicionRating)
        parcel.writeString(opinion)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }

}