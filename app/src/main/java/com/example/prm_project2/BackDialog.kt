package com.example.prm_project2

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment

class BackDialog : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setTitle("Alert")
            setMessage("Do you want to leave without saving changes?")
            setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                requireActivity().finish()
            })
            setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->  })
        }.create()

    }
}