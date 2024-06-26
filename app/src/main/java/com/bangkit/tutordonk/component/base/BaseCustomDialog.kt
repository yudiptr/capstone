package com.bangkit.tutordonk.component.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDialog
import androidx.viewbinding.ViewBinding
import com.bangkit.tutordonk.utils.dpToPx

class BaseCustomDialog<VB : ViewBinding>(
    context: Context,
    private val bindingInflater: (LayoutInflater) -> VB,
    private val bind: (VB) -> Unit,
    val onAddButtonClick: ((String) -> Unit)? = null,
    val onDismiss: ((binding: VB) -> Unit)? = null
) : AppCompatDialog(context) {

    private lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        binding = bindingInflater.invoke(LayoutInflater.from(context))
        bind(binding)
        setContentView(binding.root)

        window?.setLayout(context.dpToPx(350), ViewGroup.LayoutParams.WRAP_CONTENT)

        setOnDismissListener {
            onDismiss?.invoke(binding)
        }
    }
}