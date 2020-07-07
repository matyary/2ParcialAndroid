package Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.utn.tp3.R

/**
 * A simple [Fragment] subclass.
 */
class FragmentSelect : Fragment() {

    lateinit var chk_aerobico: CheckBox
    lateinit var chk_musculacion: CheckBox
    lateinit var chk_flexibilidad: CheckBox
    lateinit var welcomeText: TextView
    lateinit var boton: Button
    lateinit var botonR: Button
    lateinit var view_fselect: View

    private lateinit var viewModel: FragmentSelectViewModel

    // Access a Cloud Firestore instance from your Fragment/Activity
    var db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view_fselect = inflater.inflate(R.layout.fragment_select, container, false)

        chk_aerobico = view_fselect.findViewById(R.id.checkBox_a)
        chk_musculacion = view_fselect.findViewById(R.id.checkBox_m)
        chk_flexibilidad = view_fselect.findViewById(R.id.checkBox_f)
        welcomeText = view_fselect.findViewById(R.id.text_bienvenido)
        boton = view_fselect.findViewById(R.id.button_fselect)
        botonR = view_fselect.findViewById(R.id.run_btn)

        return view_fselect
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentSelectViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()

        viewModel.sportList()

        for (sport in viewModel.sports) {
            db.collection("sports").document(sport.nombre).set(sport)
        }

        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        welcomeText.setText("Bienvenid@\t" + pref.getString("Usuario", "default")).toString()

        boton.setOnClickListener {
            if( (chk_aerobico.isChecked && chk_musculacion.isChecked && chk_flexibilidad.isChecked) ||
                (chk_aerobico.isChecked && chk_musculacion.isChecked) ||
                (chk_musculacion.isChecked && chk_flexibilidad.isChecked) ||
                (chk_aerobico.isChecked && chk_flexibilidad.isChecked) ){

                chk_aerobico.isChecked = false
                chk_musculacion.isChecked = false
                chk_flexibilidad.isChecked = false

                val action2 = FragmentSelectDirections.actionFragmentSelectToDialogFragmentSelectAdv(2)
                view_fselect.findNavController().navigate(action2)
                //Snackbar.make(view_fselect, "Seleccionar solo una actividad", Snackbar.LENGTH_LONG).show()
            }

            else if (chk_aerobico.isChecked){
                val action = FragmentSelectDirections.actionFragmentSelectToListFragment(1)
                view_fselect.findNavController().navigate(action)
            }

            else if (chk_musculacion.isChecked){
                val action = FragmentSelectDirections.actionFragmentSelectToListFragment(2)
                view_fselect.findNavController().navigate(action)
            }

            else if(chk_flexibilidad.isChecked){
                val action = FragmentSelectDirections.actionFragmentSelectToListFragment(3)
                view_fselect.findNavController().navigate(action)
            }

            else {
                val action1 = FragmentSelectDirections.actionFragmentSelectToDialogFragmentSelectAdv(1)
                view_fselect.findNavController().navigate(action1)
                //Snackbar.make(view_fselect, "Seleccion una actividad para continuar", Snackbar.LENGTH_LONG).show()
            }
        }

        botonR.setOnClickListener {
            val actionrun = FragmentSelectDirections.actionFragmentSelectToMapsRunActivity()
            view_fselect.findNavController().navigate(actionrun)
        }
    }
}
