package Fragments

import Entities.User
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.utn.tp3.R
import database.appDatabase
import database.userDao
import gun0912.tedbottompicker.TedBottomPicker
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 */
class FragmentRegister : Fragment() {

    lateinit var view_register: View
    lateinit var user_register: EditText
    lateinit var pass_register: EditText
    lateinit var peso_register: EditText
    lateinit var altura_register: EditText
    lateinit var btn_new_user: Button

    lateinit var userPIC: ImageView
    private var selectedUri: Uri? = null
    private var requestManager: RequestManager? = null

    private var db: appDatabase? = null
    private var userDao: userDao? = null

    var i : Int =0

    companion object {
        const val IMAGE_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        view_register = inflater.inflate(R.layout.fragment_register, container, false)

        user_register = view_register.findViewById(R.id.editText_user_nuser)
        pass_register = view_register.findViewById(R.id.editText_pass_nuser)
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
            .load("https://lh3.googleusercontent.com/proxy/4rLomsCqccI3IyfopQBLDEvVNj4ZujjoBOghVUoCc1OJF27ejwwHj2MyPSRP4c1i1tLWm9dNbCyJRdZAzXIgSJ9Lag4Xd0IWVhu9sSGWt8AAUKXoPxUpeIiNXlP0zqMkEs9HF8ANdbsjtuv8")
            .into(userPIC)

        val parentJob = Job()
        val handler = CoroutineExceptionHandler { _, throwable ->
            Snackbar.make(view_register, "Error al cargar foto", Snackbar.LENGTH_LONG).show()
        }
        val scope = CoroutineScope(Dispatchers.Default + parentJob + handler )
        //Tarea en segundo plano
        scope.launch {
            cargarPIC()
        }

        db = appDatabase.getAppDataBase(view_register.context)
        userDao = db?.userDao()

        btn_new_user.setOnClickListener {
            if ( user_register.text.toString() != "" && pass_register.text.toString() != "" && peso_register.text.toString() != "" && altura_register.text.toString() != "") {
                val IMC = (peso_register.text.toString().toInt()) / (altura_register.text.toString().toFloat()*altura_register.text.toString().toFloat())
                userDao?.insertPerson(User(i,user_register.text.toString(),pass_register.text.toString(),
                    peso_register.text.toString().toInt(), altura_register.text.toString().toFloat(), IMC))
                i += 1
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

    suspend fun cargarPIC(){
        // cargar foto de usuario
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}
