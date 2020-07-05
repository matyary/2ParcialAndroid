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
import android.widget.ProgressBar
import androidx.core.os.HandlerCompat.postDelayed
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.github.nikartm.button.FitButton
import com.google.firebase.firestore.FirebaseFirestore
import com.utn.tp3.R
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay

class DialogFragmentUploadPic :  DialogFragment() {

    lateinit var v: View

    lateinit var loading: ProgressBar

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_dialog_upload_pic, container, false)

        loading = v.findViewById(R.id.progressBar)
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

        Handler().postDelayed({
            //do something
            dismiss()
        }, 10000)

    }
}



