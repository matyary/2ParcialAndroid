package Fragments

import Entities.User
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar

import com.utn.tp3.R
import database.appDatabase
import database.userDao

/**
 * A simple [Fragment] subclass.
 */
class FragmentLogin : Fragment() {

    lateinit var view_flogin: View
    lateinit var user_flogin: EditText
    lateinit var pass_flogin: EditText
    lateinit var btn_new_user: Button
    lateinit var btn_flogin_to_fselect: Button
    lateinit var checkbox: CheckBox

    private var db: appDatabase? = null
    private var userDao: userDao? = null
    lateinit var  userList :MutableList<User>
    lateinit var userEnter: User

    var flag_log_ok : Int =0

    lateinit var mp: MediaPlayer
    lateinit var sound: ToggleButton
    lateinit var txtMusic: TextView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view_flogin = inflater.inflate(R.layout.fragment_login, container, false)

        user_flogin = view_flogin.findViewById(R.id.editText_user_flogin)
        pass_flogin = view_flogin.findViewById(R.id.editText_pass_flogin)
        btn_new_user = view_flogin.findViewById(R.id.button_flogin)
        btn_flogin_to_fselect = view_flogin.findViewById(R.id.button_flogin_to_fselect)
        checkbox = view_flogin.findViewById(R.id.checkBox)

        mp = MediaPlayer.create(requireActivity(), R.raw.rocky)
        sound = view_flogin.findViewById(R.id.music)
        txtMusic = view_flogin.findViewById(R.id.music_state)

        return view_flogin
    }

    override fun onStart() {
        super.onStart()

        checkbox.isChecked = true

        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = pref.edit()

        if (pref.getString("Usuario", "default") != "default" && pref.getString("Contraseña", "default")!= "default"){
            user_flogin.setText(pref.getString("Usuario", "default"))
            pass_flogin.setText(pref.getString("Contraseña", "default"))
        }

        sound.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The toggle is enabled
                mp.pause()
                txtMusic.text = "OFF"
            } else {
                // The toggle is disabled
                mp.start()
                txtMusic.text = "ON"
            }
        }

        db = appDatabase.getAppDataBase(view_flogin.context)
        userDao = db?.userDao()

        btn_new_user.setOnClickListener {

            val action = FragmentLoginDirections.actionFragmentLoginToFragmentRegister()
            view_flogin.findNavController().navigate(action)

        }

        btn_flogin_to_fselect.setOnClickListener {

            if ( user_flogin.text.toString() != "" && pass_flogin.text.toString() != "") {

                userList = userDao?.loadAllPersons() as MutableList<User>

                for ( actualUser in userList){
                    if( actualUser.getNombre() == user_flogin.text.toString() && actualUser.getClave() == pass_flogin.text.toString() ){
                        if(checkbox.isChecked){
                            userEnter = userDao?.loadPersonByName(user_flogin.text.toString())!!
                            Log.d("peso", userEnter.weight.toString())
                            Log.d("altura", userEnter.height.toString())
                            Log.d("imc", userEnter.imc.toString())
                            editor.putString("Usuario",user_flogin.text.toString())
                            editor.putString("Contraseña",pass_flogin.text.toString())
                            editor.putString("Peso", userEnter.weight.toString())
                            editor.putString("Altura", userEnter.height.toString())
                            editor.putString("IMC", userEnter.imc.toString())
                            editor.apply()
                        }
                        val action2 = FragmentLoginDirections.actionFragmentLoginToFragmentSelect()
                        view_flogin.findNavController().navigate(action2)
                        flag_log_ok = 1
                    }
                }
                if (flag_log_ok != 1) {
                    Snackbar.make(view_flogin, "Error de Inicio de Sesión", Snackbar.LENGTH_LONG).show()
                }
                flag_log_ok = 0
            }

            else {
                Snackbar.make(view_flogin, "Datos incompletos", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mp.start()
        txtMusic.text = "ON"
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onStop() {
        super.onStop()
        mp.pause()
        txtMusic.text = "OFF"
    }
}
