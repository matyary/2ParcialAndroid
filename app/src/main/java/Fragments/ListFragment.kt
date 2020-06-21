package Fragments

import Entities.Sport
import Holders.SportHolder
import android.graphics.Color
import android.media.MediaPlayer

import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import androidx.lifecycle.ViewModelProvider

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

import com.utn.tp3.R

class ListFragment : Fragment() {

    lateinit var view_sport: View
    lateinit var recSport: RecyclerView

    // Access a Cloud Firestore instance from your Fragment/Activity
    var db = FirebaseFirestore.getInstance()
    private lateinit var adapter: FirestoreRecyclerAdapter<Sport, SportHolder>

    private var listSport : MutableList<Sport>? = null
    private var selectedSport : Sport? = null

    private lateinit var viewModelTab1: FragmentTab1ViewModel

    lateinit var mp: MediaPlayer

    val args: ListFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        view_sport = inflater.inflate(R.layout.list_fragment, container, false)

        recSport = view_sport.findViewById(R.id.rec_sport)

        recSport.setHasFixedSize(true)
        recSport.layoutManager = LinearLayoutManager(context)

        mp = MediaPlayer.create(requireActivity(), R.raw.pumpit)

        setHasOptionsMenu(true)

        return view_sport
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModelTab1 = ViewModelProvider(requireActivity()).get(FragmentTab1ViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()

        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        when (pref.getString("music", "default")){
            "pumpit" -> {mp = MediaPlayer.create(requireActivity(), R.raw.pumpit)}
            "misionimposible" -> {mp = MediaPlayer.create(requireActivity(), R.raw.misionimposible)}
            "rocky" -> {mp = MediaPlayer.create(requireActivity(), R.raw.rocky)}
        }

        if(pref.getBoolean("sonido", false)) {
            mp.start()
        }

        val actividad = args.actividad
        val flagList = args.flaglist

        when (actividad){

            1 ->{
                db.collection("sports")
                    .whereEqualTo("tipo", "AEROBICO")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            listSport?.add(document.toObject(Sport::class.java))
                        }
                    }
            }
            2 ->{
                db.collection("sports")
                    .whereEqualTo("tipo", "MUSCULACION")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            listSport?.add(document.toObject(Sport::class.java))
                        }
                    }
            }
            3 ->{
                db.collection("sports")
                    .whereEqualTo("tipo", "FLEXIBILIDAD")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            listSport?.add(document.toObject(Sport::class.java))
                        }
                    }
            }
        }

        if(listSport == null){
            listSport = ArrayList()
        }

        fillRecycler()

        if (flagList == 1)
            recSport.adapter?.notifyDataSetChanged()
    }

    fun fillRecycler(){
        val query = db.collection("sports")

        val options = FirestoreRecyclerOptions.Builder<Sport>()
            .setQuery(query, Sport::class.java)
            .build()

        adapter = object :
            FirestoreRecyclerAdapter<Sport, SportHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sport, parent, false)
                return SportHolder(view)
            }

            override fun onBindViewHolder(holder: SportHolder, position: Int, model: Sport) {
                holder.setName(model.nombre)
                holder.setFrecuency(model.frecuencia)
                holder.getCardLayout().setOnClickListener {
                    viewModelTab1.ItemClicked.value = listSport!![position]
                    val actiontab = ListFragmentDirections.actionListFragmentToContainerFragment()
                    view_sport.findNavController().navigate(actiontab)
                }
                holder.getCardLayout().setOnLongClickListener {
                    holder.getCardLayout().setBackgroundColor(Color.MAGENTA)
                    selectedSport = listSport!![position]
                    //Log.d("posicion", selectedSport!!.nombre)
                    Snackbar.make(view_sport, "Presione icono de borrado para eliminar ítem seleccionado", Snackbar.LENGTH_LONG).show()
                    return@setOnLongClickListener true
                }
            }

            override fun onDataChanged() {
                super.onDataChanged()
            }
        }
        adapter.startListening()
        recSport.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {

            R.id.action_add -> {
                val action_toolbar_add = ListFragmentDirections.actionListFragmentToNewItem()
                view_sport.findNavController().navigate(action_toolbar_add)
            }

            R.id.action_erase -> {
                val action_toolbar_erase = ListFragmentDirections.actionListFragmentToDialogFragment(selectedSport)
                view_sport.findNavController().navigate(action_toolbar_erase)
            }

            R.id.action_settings -> {
                val action_toolbar_settings = ListFragmentDirections.actionListFragmentToSettingsActivity()
                view_sport.findNavController().navigate(action_toolbar_settings)
            }

            else -> ""
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        mp.pause()
    }
}


