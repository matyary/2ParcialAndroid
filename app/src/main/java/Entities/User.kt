package Entities

import android.os.Parcel
import android.os.Parcelable

class User (uid : String?,
            nombre : String?,
            clave : String?,
            correo: String?,
            peso: Int?,
            altura: Float?,
            imc: Float?,
            foto: String?):
    Parcelable{

    var uid : String
    var nombre : String
    var clave : String
    var correo : String
    var peso : Int
    var altura : Float
    var imc : Float
    var foto : String

    init {
        this.uid = uid!!
        this.nombre = nombre!!
        this.clave = clave!!
        this.correo = correo!!
        this.peso = peso!!
        this.altura = altura!!
        this.imc = imc!!
        this.foto = foto!!
    }

    constructor() : this("","","","",0,0.0f,0.0f,"")

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readFloat(),
        source.readFloat(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(uid)
        writeString(nombre)
        writeString(clave)
        writeString(correo)
        writeInt(peso)
        writeFloat(altura)
        writeFloat(imc)
        writeString(foto)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }

    fun getName() : String{
        return (this.nombre)
    }

    fun getPass() : String{
        return (this.clave)
    }

    fun setName(nombre: String) {
        this.nombre = nombre
    }

    fun setPass(clave: String) {
        this.clave = clave
    }
}