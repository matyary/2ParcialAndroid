package Dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.github.nikartm.button.FitButton
import com.google.firebase.firestore.FirebaseFirestore
import com.utn.tp3.R

class DialogFragmentLoginFailed :  DialogFragment() {

    lateinit var v: View

    lateinit var btnAceptar: FitButton
    lateinit var txtDialog: TextView

    val args: DialogFragmentLoginFailedArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_dialog_login_failed, container, false)

        btnAceptar = v.findViewById(R.id.btn_aceptar_dialog)
        txtDialog = v.findViewById(R.id.dialog_text)
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

        if (args.errorType==1) {
            txtDialog.setText("El usuario o contraseña ingresados \n" +
                    "no son correctos. Por favor asegúrese \n" +
                    "de ingresarlos correctamente.").toString()
        }

        if (args.errorType==2) {
            txtDialog.setText("Hay uno o más campos de texto \n" +
                    "incompletos. Por favor asegúrese \n" +
                    "de ingresar todos los datos.").toString()
        }

        btnAceptar.setOnClickListener() {
            dismiss()
        }
    }
}



