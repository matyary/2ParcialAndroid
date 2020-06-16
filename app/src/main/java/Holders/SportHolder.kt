package Holders

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.utn.tp3.R


class SportHolder (v: View) : RecyclerView.ViewHolder(v) {

    private var view: View

    init {
        this.view = v
    }

    fun setName(name : String) {
        val txt : TextView = view.findViewById(R.id.txt_name_item)
        txt.text = name
    }

    fun setFrecuency(frecuency: String) {
        val fcy: TextView = view.findViewById(R.id.txt_fcy_item)
        fcy.text = frecuency
    }

    fun getCardLayout (): CardView {

        return view.findViewById(R.id.card_package_item)
    }
}