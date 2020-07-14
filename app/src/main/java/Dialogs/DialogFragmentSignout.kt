package Dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.github.nikartm.button.FitButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.utn.tp3.R
import kotlin.system.exitProcess

class DialogFragmentSignout :  DialogFragment() {

    lateinit var v: View

    lateinit var btnAccept: FitButton
    lateinit var btnCancel: FitButton

    // Initialize Firebase Auth
    private var auth: FirebaseAuth = Firebase.auth

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_dialog_signout, container, false)

        btnAccept = v.findViewById(R.id.btn_aceptar_dialog)
        btnCancel = v.findViewById(R.id.btn_cancel_dialog)
        return v
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onStart() {
        super.onStart()

        btnCancel.setOnClickListener() {
            dismiss()
        }

        btnAccept.setOnClickListener {
            Firebase.auth.signOut()
            Handler().postDelayed({
                //do something
                exitProcess(0)
            }, 1000)
        }
    }
}



