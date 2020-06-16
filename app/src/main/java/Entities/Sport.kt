package Entities

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

class Sport (nombre: String?, descripcion: String?, frecuencia: String?, urlImage: String?, tipo: String?) :
    Parcelable{

    var nombre: String
    var descripcion: String
    var frecuencia: String
    var urlImage: String
    var tipo: String

    init {
        this.nombre = nombre!!
        this.descripcion = descripcion!!
        this.frecuencia = frecuencia!!
        this.urlImage = urlImage!!
        this.tipo = tipo!!
    }
    constructor() : this("","","","","")

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(nombre)
        writeString(descripcion)
        writeString(frecuencia)
        writeString(urlImage)
        writeString(tipo)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Sport> = object : Parcelable.Creator<Sport> {
            override fun createFromParcel(source: Parcel): Sport = Sport(source)
            override fun newArray(size: Int): Array<Sport?> = arrayOfNulls(size)
        }
    }
}




