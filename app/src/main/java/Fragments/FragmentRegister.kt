package Fragments

import Entities.User
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import com.utn.tp3.R
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 */
class FragmentRegister : Fragment() {

    lateinit var view_register: View
    lateinit var user_register: EditText
    lateinit var pass_register: EditText
    lateinit var email_register: EditText
    lateinit var peso_register: EditText
    lateinit var altura_register: EditText
    lateinit var btn_new_user: Button

    lateinit var userPIC: ImageView
    private var selectedUri: Uri? = null
    private var requestManager: RequestManager? = null

    // Access a Cloud Firestore instance from your Fragment/Activity
    var db = FirebaseFirestore.getInstance()

    //Acceso al depósito de almacenamiento
    var storage = Firebase.storage("gs://sportapp-6e7c6.appspot.com")

    val args: FragmentRegisterArgs by navArgs()

    companion object {
        const val IMAGE_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view_register = inflater.inflate(R.layout.fragment_register, container, false)

        user_register = view_register.findViewById(R.id.editText_user_nuser)
        pass_register = view_register.findViewById(R.id.editText_pass_nuser)
        email_register = view_register.findViewById(R.id.editText_email_nuser)
        peso_register = view_register.findViewById(R.id.editText_peso_nuser)
        altura_register = view_register.findViewById(R.id.editText_altura_nuser)
        btn_new_user = view_register.findViewById(R.id.button_newuser)

        userPIC = view_register.findViewById(R.id.imageUser)
        requestManager = Glide.with(this)

        return view_register
    }

    override fun onStart() {
        super.onStart()

        Picasso.get()
            .load("https://www.adl-logistica.org/wp-content/uploads/2019/07/imagen-perfil-sin-foto.png")
            .into(userPIC)

        val parentJob = Job()
        val handler = CoroutineExceptionHandler { _, throwable ->
            Snackbar.make(view_register, "Error al cargar foto", Snackbar.LENGTH_LONG).show()
        }
        val scope = CoroutineScope(Dispatchers.Default + parentJob + handler )
        //Tarea en segundo plano
        scope.launch {
            toStorage()
            fromStorage()
            createUser()
        }

        val newUser = args.newregisterUser!!
        Log.d ("NOMBRE USER", newUser.name)
        Log.d ("EMAIL USER", newUser.email)
        Log.d ("UID USER", newUser.uid)

        user_register.setText(newUser.name)
        email_register.setText(newUser.email)

        btn_new_user.setOnClickListener {
            if ( user_register.text.toString() != "" && pass_register.text.toString() != "" &&
                email_register.text.toString() != "" && peso_register.text.toString() != "" &&
                altura_register.text.toString() != "") {

                val IMC = (peso_register.text.toString().toInt()) / (altura_register.text.toString().toFloat()*altura_register.text.toString().toFloat())

                newUser.imc = IMC
                newUser.name = user_register.text.toString()
                newUser.pass = pass_register.text.toString()
                newUser.email = email_register.text.toString()
                newUser.weight = peso_register.text.toString().toInt()
                newUser.height = altura_register.text.toString().toFloat()
                db.collection("users").document(newUser.uid).set(newUser)
                val action = FragmentRegisterDirections.actionFragmentRegisterToFragmentLogin()
                view_register.findNavController().navigate(action)
            }
            else{
                Snackbar.make(view_register, "Datos incompletos", Snackbar.LENGTH_LONG).show()
            }
        }

        if(ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            }
            else {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    IMAGE_PERMISSION_REQUEST_CODE)
            }
        }
        else {
            userPIC.setOnClickListener {
                TedBottomPicker.with(requireActivity())
                    .setSelectedUri(selectedUri)
                    .setPeekHeight(10).show { uri ->

                        selectedUri = uri

                        requestManager
                            ?.load(uri)
                            ?.into(userPIC)
                    }
            }
        }
    }

    //Tarea en segundo plano para subir a Firebase Storage la foto de perfil de usuario.
    suspend fun toStorage(){
        Log.d("TEST:", "toStorage")
    }

    //Tarea de segundo plano para descargar foto de perfil de usuario en formato URL.
    suspend fun fromStorage(){
        Log.d("TEST:", "fromStorage")
    }

    //Tarea de segundo plano para cargar objeto usuario a la base de datos de Firebase.
    suspend fun createUser(){
        Log.d("TEST:", "createUser")
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}
