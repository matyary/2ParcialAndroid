package Entities

import android.net.Uri
import android.widget.ImageView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URI

@Entity(tableName = "users")
class User (id : Int, name : String, pass : String, weight: Int, height: Float, imc: Float) {

    @PrimaryKey
    @ColumnInfo(name = "id")
    var id : Int

    @ColumnInfo(name = "name")
    var name : String

    @ColumnInfo(name = "pass")
    var pass : String

    @ColumnInfo(name = "weight")
    var weight : Int

    @ColumnInfo(name = "height")
    var height : Float

    @ColumnInfo(name = "imc")
    var imc : Float

    init {
        this.id = id
        this.name = name
        this.pass = pass
        this.weight = weight
        this.height = height
        this.imc = imc
    }

    fun getNombre() : String{
        return (this.name)
    }

    fun getClave() : String{
        return (this.pass)
    }
}