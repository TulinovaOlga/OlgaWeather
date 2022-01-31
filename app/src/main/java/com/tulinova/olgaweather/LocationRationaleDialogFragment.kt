package com.tulinova.olgaweather

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class LocationRationaleDialogFragment : DialogFragment() {
    private lateinit var listener: LocationRationaleDialogListener

    interface LocationRationaleDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.location_permission_rationale))
            .setPositiveButton(getString(android.R.string.ok)) { _,_ ->
                listener.onDialogPositiveClick(this)
            }
            .create()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as LocationRationaleDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }
}
