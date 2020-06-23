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
    lateinit var newUser: User

    //Acceso al depósito de almacenamiento
    var storage = Firebase.storage("gs://sportapp-6e7c6.appspot.com")
    private var photoUrl: String = ""

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

        //Imagen de perfil por default, alertando que el usuario no tiene foto.
        Picasso.get()
            .load("https://www.adl-logistica.org/wp-content/uploads/2019/07/imagen-perfil-sin-foto.png")
            .into(userPIC)

        //Tareas...
        val parentJob = Job()
        val scope = CoroutineScope(Dispatchers.IO + parentJob )
        //Tarea en segundo plano
        scope.launch {
            toStorage()
            fromStorage()
            createUser()
        }

        //Datos de usuario a crear provenientes de la autenticación de Google.
        newUser = args.newregisterUser!!
        Log.d ("NOMBRE USER", newUser.nombre)
        Log.d ("EMAIL USER", newUser.correo)
        Log.d ("UID USER", newUser.uid)

        //Se autocompletan dos de los campos de creación de usuario.
        user_register.setText(newUser.nombre)
        email_register.setText(newUser.correo)

        //OnClick de botón de creación de usuario.
        btn_new_user.setOnClickListener {
            //Si todos los campos fueron completados e incluso la foto fue cargada...
            if ( user_register.text.toString() != "" && pass_register.text.toString() != "" &&
                email_register.text.toString() != "" && peso_register.text.toString() != "" &&
                altura_register.text.toString() != "" && photoUrl != "") {

                val IMC = (peso_register.text.toString().toInt()) / (altura_register.text.toString().toFloat()*altura_register.text.toString().toFloat())

                newUser.imc = IMC
                newUser.nombre = user_register.text.toString()
                newUser.clave = pass_register.text.toString()
                newUser.correo = email_register.text.toString()
                newUser.peso = peso_register.text.toString().toInt()
                newUser.altura = altura_register.text.toString().toFloat()

                db.collection("users").document(newUser.uid).set(newUser)

                val action = FragmentRegisterDirections.actionFragmentRegisterToFragmentLogin()
                view_register.findNavController().navigate(action)
            }
            else{
                Snackbar.make(view_register, "Datos incompletos", Snackbar.LENGTH_LONG).show()
            }
        }

        //Si el permiso de uso de galería no fue aprobado...
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
        //Si por el contrario el permiso fue aprobado...
        else {
            //OnClick de imagen de perfil de usuario.
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
        if (selectedUri != null) {
            // Points to the root reference
            val storageRef = storage.reference

            // Points to "profileSmartphone"
            val photosRef = storageRef.child("profileSmartphone")

            // Points to "profileSmartphone/XXX.jpg"
            // Note that you can use variables to create child values
            val fileName = "foto_" + newUser.uid + "_.jpg"
            val fileRef = photosRef.child(fileName)

            fileRef.putFile(selectedUri!!)
        }
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
