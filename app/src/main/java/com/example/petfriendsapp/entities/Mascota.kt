package com.example.petfriendsapp.entities
import android.os.Parcel
import android.os.Parcelable
class Mascota(raza: String, edad: Int, nombre: String, ubicacion: String, descripcion:String,sexo:String) :
    Parcelable {
    var raza: String
    var edad: Int
    var nombre: String
    var ubicacion: String
    var descripcion:String
    var sexo:String;

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),

        )

    init {
        this.raza=raza
        this.edad=edad
        this.nombre=nombre
        this.ubicacion = ubicacion
        this.descripcion = descripcion
        this.sexo = sexo
    }


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