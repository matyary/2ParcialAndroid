package Fragments

import Entities.Sport
import Entities.User
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
import com.google.firebase.firestore.Query

import com.utn.tp3.R

class ListFragment : Fragment() {

    lateinit var view_sport: View
    lateinit var recSport: RecyclerView

    // Access a Cloud Firestore instance from your Fragment/Activity
    var db = FirebaseFirestore.getInstance()
    private lateinit var adapter: FirestoreRecyclerAdapter<Sport, SportHolder>
    lateinit var query: Query

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

        when (actividad){

            1 ->{
                query = db.collection("sports")
                    .whereEqualTo("tipo", "AEROBICO")
            }
            2 ->{
                query = db.collection("sports")
                    .whereEqualTo("tipo", "MUSCULACION")
            }
            3 ->{
                query = db.collection("sports")
                    .whereEqualTo("tipo", "FLEXIBILIDAD")
            }
        }

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
                    viewModelTab1.ItemClicked.value = model
                    val actiontab = ListFragmentDirections.actionListFragmentToContainerFragment()
                    view_sport.findNavController().navigate(actiontab)
                }
                holder.getCardLayout().setOnLongClickListener {
                    holder.getCardLayout().setBackgroundColor(Color.MAGENTA)
                    selectedSport = model
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

        adapter.notifyDataSetChanged()
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
                if (selectedSport != null) {
                    val action_toolbar_erase = ListFragmentDirections.actionListFragmentToDialogFragmentErase(selectedSport)
                    view_sport.findNavController().navigate(action_toolbar_erase)
                }
                else {
                    val action_toolbar_erase_not = ListFragmentDirections.actionListFragmentToDialogFragmentEraseNot()
                    view_sport.findNavController().navigate(action_toolbar_erase_not)
                }
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