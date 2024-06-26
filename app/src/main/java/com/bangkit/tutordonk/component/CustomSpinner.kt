package com.bangkit.tutordonk.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.bangkit.tutordonk.R

class CustomSpinner @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatSpinner(context, attrs, defStyleAttr) {

    private var onItemSelected: ((String) -> Unit)? = null

    var value: String = ""

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomSpinner,
            0, 0
        ).apply {
            try {
                val entriesResId = getResourceId(R.styleable.CustomSpinner_android_entries, 0)
                if (entriesResId != 0) setEntries(entriesResId)
            } finally {
                recycle()
            }
        }

        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val getItem = parent.getItemAtPosition(position) as String
                value = getItem
                onItemSelected?.invoke(getItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                /* no-op */
            }
        }
    }

    fun setEntries(arrayResId: Int) {
        val items = context.resources.getStringArray(arrayResId)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = adapter
    }

    fun setDefaultEntries(data: List<String>) {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = adapter
    }

    fun setOnItemSelectedListener(listener: (String) -> Unit) {
        onItemSelected = listener
    }

}
