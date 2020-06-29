package Fragments

import Entities.Sport
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

import com.utn.tp3.R

class NewItem : Fragment() {

    // Access a Cloud Firestore instance from your Fragment/Activity
    var  db = FirebaseFirestore.getInstance()

    private lateinit var view_new_item: View

    lateinit var new_sport_name: EditText
    lateinit var new_sport_description: EditText
    lateinit var new_sport_frecuencia: EditText
    lateinit var new_sport_urlimg: EditText
    lateinit var btn_a: Button
    lateinit var btn_m: Button
    lateinit var btn_f: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view_new_item = inflater.inflate(R.layout.new_item_fragment, container, false)

        new_sport_name = view_new_item.findViewById(R.id.txt_name)
        new_sport_description = view_new_item.findViewById(R.id.txt_description)
        new_sport_frecuencia = view_new_item.findViewById(R.id.txt_frecuencia)
        new_sport_urlimg = view_new_item.findViewById(R.id.txt_urlImage)

        btn_a = view_new_item.findViewById(R.id.btn_aerobico)
        btn_m = view_new_item.findViewById(R.id.btn_musculacion)
        btn_f = view_new_item.findViewById(R.id.btn_flexibilidad)

        return view_new_item
    }

    public fun LoadNewItem (type: String?){

        var typAct = 0

        if(new_sport_name.text.toString()!="" && new_sport_description.text.toString()!="" && new_sport_urlimg.text.toString()!=""){

            var sport: Sport = Sport(
                new_sport_name.text.toString(),
                new_sport_description.text.toString(),
                new_sport_frecuencia.text.toString(),
                new_sport_urlimg.text.toString(),
                type)
            db.collection("sports").document(sport.nombre).set(sport)

            when (type){
                "AEROBICO" -> typAct = 1
                "MUSCULACION" -> typAct = 2
                "FLEXIBILIDAD" -> typAct = 3
            }
            val actionnew = NewItemDirections.actionNewItemToListFragment(typAct)
            view_new_item.findNavController().navigate(actionnew)
        }

        else Snackbar.make(view_new_item, "Datos incompletos", Snackbar.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()

        btn_a.setOnClickListener {
            LoadNewItem("AEROBICO")
        }

        btn_m.setOnClickListener {
            LoadNewItem("MUSCULACION")
        }

        btn_f.setOnClickListener {
            LoadNewItem("FLEXIBILIDAD")
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}
