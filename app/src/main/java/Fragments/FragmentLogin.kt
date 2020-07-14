package Fragments

import Entities.User
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.utn.tp3.MainActivity
import com.utn.tp3.R
import kotlin.system.exitProcess


/**
 * A simple [Fragment] subclass.
 */
class FragmentLogin : Fragment() {

    companion object {
        private const val SIGN_IN_REQUEST_CODE = 91
    }

    class Navigate {
        companion object {
            const val toWelcomeScreen = 1
            const val toDialogError = 2
            const val toDialogIncomplete = 3
            const val toRegisterScreen = 4
        }
    }

    class Dialog {
        companion object {
            const val DATOS_ERRONEOS = 1
            const val DATOS_INCOMPLETOS = 2
        }
    }

    // Access a Cloud Firestore instance from your Fragment/Activity
    var db = FirebaseFirestore.getInstance()

    lateinit var view_flogin: View
    lateinit var user_flogin: EditText
    lateinit var pass_flogin: EditText
    lateinit var checkbox: CheckBox

    lateinit var signInButton: SignInButton
    private lateinit var auth: FirebaseAuth

    private var userEnter: User? = null

    //Objeto usuario y sus campos a registrar...
    lateinit var registerUser: User
    lateinit var name: String
    lateinit var email: String
    lateinit var uid: String

    private var flagUserAuthOK: Int = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view_flogin = inflater.inflate(R.layout.fragment_login, container, false)

        // Set the dimensions of the sign-in button.
        signInButton = view_flogin.findViewById(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)

        // Initialize Firebase Auth
        auth = Firebase.auth

        user_flogin = view_flogin.findViewById(R.id.editText_user_flogin)
        pass_flogin = view_flogin.findViewById(R.id.editText_pass_flogin)
        checkbox = view_flogin.findViewById(R.id.checkBox)
        //Usuario y contraseña preguardados por default.
        checkbox.isChecked = true

        return view_flogin
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        checkAuth(auth.currentUser)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestId()
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        //OnClick de botón de inicio de sesión por Google.
        signInButton.setOnClickListener {
            if (auth.currentUser == null) {
                mGoogleSignInClient.signOut()
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE)
            }
            else {
                navigateWhere()
            }
        }
    }

    //Método para conocer destino de navegación.
    private fun navigateWhere (){
        if (flagUserAuthOK == 1) {
            //Si los campos de nombre de usuario y contraseña están completos...
            if (user_flogin.text.toString() != "" && pass_flogin.text.toString() != "") {
                //Si alguno de los usuarios ya instanciados en base de datos se corresponde con el
                //ingresado en los EditText...
                if (userEnter?.getName() == user_flogin.text.toString() &&
                    userEnter?.getPass() == pass_flogin.text.toString()) {
                    //Si está tildado el checkbox de recordar usuario y contraseña,
                    //se guarda info de usuario en Settings.
                    if (checkbox.isChecked) {
                        Log.d("peso", userEnter?.peso.toString())
                        Log.d("altura", userEnter?.altura.toString())
                        Log.d("imc", userEnter?.imc.toString())
                        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
                        val editor = pref.edit()
                        editor.putString("Usuario", user_flogin.text.toString())
                        editor.putString("Contraseña", pass_flogin.text.toString())
                        editor.putString("Peso", userEnter?.peso.toString())
                        editor.putString("Altura", userEnter?.altura.toString())
                        editor.putString("IMC", userEnter?.imc.toString())
                        editor.apply()
                    }
                    navigateTo(Navigate.toWelcomeScreen)
                }
                else {
                    navigateTo(Navigate.toDialogError)
                }
            }
            else {
                navigateTo(Navigate.toDialogIncomplete)
            }
        }
        else {
            navigateTo(Navigate.toRegisterScreen)
        }
    }

    //Método de navegación.
    private fun navigateTo (direccion: Int){
        when (direccion) {
            Navigate.toWelcomeScreen -> {
                if (view_flogin.findNavController().currentDestination?.id == R.id.fragmentLogin) {
                    val action1 = FragmentLoginDirections.actionFragmentLoginToFragmentSelect()
                    view_flogin.findNavController().navigate(action1)
                }
            }
            Navigate.toDialogError -> {
                if (view_flogin.findNavController().currentDestination?.id == R.id.fragmentLogin) {
                    val action2 = FragmentLoginDirections.actionFragmentLoginToDialogFragmentLoginFailed(Dialog.DATOS_ERRONEOS)
                    view_flogin.findNavController().navigate(action2)
                }
            }
            Navigate.toDialogIncomplete -> {
                if (view_flogin.findNavController().currentDestination?.id == R.id.fragmentLogin) {
                    val action3 = FragmentLoginDirections.actionFragmentLoginToDialogFragmentLoginFailed(Dialog.DATOS_INCOMPLETOS)
                    view_flogin.findNavController().navigate(action3)
                }
            }
            Navigate.toRegisterScreen -> {
                if (view_flogin.findNavController().currentDestination?.id == R.id.fragmentLogin) {
                    val action4 = FragmentLoginDirections.actionFragmentLoginToFragmentRegister(registerUser)
                    view_flogin.findNavController().navigate(action4)
                }
            }
            else -> ""
        }
    }

    //Método para checkear autenticación con Google.
    private fun checkAuth (cuenta: FirebaseUser?) {
        if (cuenta == null) {
            flagUserAuthOK = 0
            user_flogin.setText("")
            pass_flogin.setText("")
        }
        else {
            //Checkeo si ya existe objeto usuario para tal autenticación...
            db.collection("users")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        //Si ya existe un usuario con el uid de la autenticación...
                        if (document.toObject(User::class.java).uid == cuenta.uid) {
                            flagUserAuthOK = 1
                            //Configuración Settings...
                            val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
                            //Si hay preferencias de usuario guardadas, autocompletar editText...
                            if (pref.getString("Usuario", "default") != "default" && pref.getString("Contraseña", "default") != "default") {
                                user_flogin.setText(pref.getString("Usuario", "default"))
                                pass_flogin.setText(pref.getString("Contraseña", "default"))
                            }
                            userEnter = document.toObject(User::class.java)
                        }
                    }
                }

            if (userEnter == null) flagUserAuthOK = 0

            //Almaceno datos de interés de la cuenta de Google.
            cuenta.let {
                // Name, email address, and profile photo Url
                name = cuenta.displayName!!
                email = cuenta.email!!
                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getToken() instead.
                uid = cuenta.uid

                registerUser = User(uid, name, "", email, 0, 0.0f, 0.0f, "")
            }
        }
    }

    //Método autenticación Firebase con usuario de Google.
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    checkAuth(user)
                    Handler().postDelayed({
                        //do something
                        navigateWhere()
                    }, 2000)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // ...
                    Snackbar.make(view_flogin, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    checkAuth(null)
                }
            }
    }

    //Al ingresar a esta pantalla, se habilita la actionbar...
    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}
