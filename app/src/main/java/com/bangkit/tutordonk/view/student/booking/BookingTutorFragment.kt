package com.bangkit.tutordonk.view.student.booking

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bangkit.tutordonk.R
import com.bangkit.tutordonk.databinding.FragmentStudentBookingTutorBinding
import com.bangkit.tutordonk.databinding.PopupRatingBinding
import com.bangkit.tutordonk.databinding.PopupReportBinding
import com.bangkit.tutordonk.view.base.BaseCustomDialog
import com.bangkit.tutordonk.view.component.historyrecyclerview.model.HistoryItem
import com.bangkit.tutordonk.view.model.ListTutor
import com.bangkit.tutordonk.view.model.Tutor
import com.bangkit.tutordonk.view.navigateWithAnimation
import com.bangkit.tutordonk.view.setReadOnly
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingTutorFragment : Fragment() {
    private var _binding: FragmentStudentBookingTutorBinding? = null
    private val binding get() = _binding!!
    private var selectedStudy = ""

    private lateinit var navController: NavController
    private lateinit var dialogRating: BaseCustomDialog<PopupRatingBinding>
    private lateinit var dialogReport: BaseCustomDialog<PopupReportBinding>
    private lateinit var historyItem: HistoryItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentBookingTutorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setLayoutListener()
        if (arguments?.getBoolean(ARG_FROM_HISTORY, false) == true) {
            setDialogListener()
            checkArgumentData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLayoutListener() = with(binding) {
        spinnerStudy.setOnItemSelectedListener { study ->
            selectedStudy = study
            changeItemSubStudy()
        }
        mcbSelectSubStudy.setOnCheckedChangeListener { _, isChecked ->
            changeItemSubStudy()
            spinnerSubStudy.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        tilDate.setEndIconOnClickListener { showDatePickerDialog() }
        tietDate.addTextChangedListener(formatDateWatcher())

        btnRating.setOnClickListener { dialogRating.show() }
        btnReport.setOnClickListener { dialogReport.show() }
        btnBooking.setOnClickListener { goToTutorHome() }
    }

    private fun checkArgumentData() {
        historyItem = Gson().fromJson(arguments?.getString(ARG_HISTORY_ITEM), HistoryItem::class.java)
        with(binding) {
            groupRateTutor.visibility = View.VISIBLE
            btnBooking.visibility = View.GONE
            mcbSelectSubStudy.isChecked = true

            spinnerStudy.setDefaultEntries(listOf(historyItem.major))
            spinnerStudy.setReadOnly()
            spinnerSubStudy.setDefaultEntries(listOf(historyItem.subMajor))
            spinnerSubStudy.setReadOnly()
            spinnerSelectTutor.setDefaultEntries(listOf(historyItem.name))
            spinnerSelectTutor.setReadOnly()
        }
    }

    private fun setDialogListener() {
        dialogRating = BaseCustomDialog(
            context = requireContext(),
            bindingInflater = PopupRatingBinding::inflate,
            bind = { binding ->
                with(binding) {
                    tvName.text = historyItem.name
                    ivStars1.setOnClickListener { setRating(binding, 1) }
                    ivStars2.setOnClickListener { setRating(binding, 2) }
                    ivStars3.setOnClickListener { setRating(binding, 3) }
                    ivStars4.setOnClickListener { setRating(binding, 4) }
                    ivStars5.setOnClickListener { setRating(binding, 5) }
                    ivClose.setOnClickListener { dialogRating.cancel() }
                    btnSend.setOnClickListener { dialogRating.cancel() }
                }


            })

        dialogReport = BaseCustomDialog(
            context = requireContext(),
            bindingInflater = PopupReportBinding::inflate,
            bind = { binding ->
                with(binding) {
                    tvName.text = historyItem.name
                    ivClose.setOnClickListener { dialogReport.cancel() }
                    btnSend.setOnClickListener { dialogReport.cancel() }
                }
            })
    }

    private fun setRating(binding: PopupRatingBinding, rating: Int) {
        val filledStar = R.drawable.ic_stars_filled
        val emptyStar = R.drawable.ic_stars

        with(binding) {
            ivStars1.setImageResource(if (rating >= 1) filledStar else emptyStar)
            ivStars2.setImageResource(if (rating >= 2) filledStar else emptyStar)
            ivStars3.setImageResource(if (rating >= 3) filledStar else emptyStar)
            ivStars4.setImageResource(if (rating >= 4) filledStar else emptyStar)
            ivStars5.setImageResource(if (rating >= 5) filledStar else emptyStar)
        }
    }

    private fun changeItemSubStudy() = with(binding) {
        spinnerSubStudy.setEntries(
            when (selectedStudy) {
                B_ENG -> R.array.list_sub_study_b_english_array
                ALGORITHM -> R.array.list_sub_study_algorithm_array
                else -> R.array.list_sub_study_kalkulus_array
            }
        )
    }

    private fun goToTutorHome() {
        val data = ListTutor(
            List(4) {
                Tutor(
                    "Ini Nama $it",
                    "0821-2183-123$it",
                    "Jln Raya Cibadak No $it",
                    "${300000 * it}",
                    "1. https://drive.google.com/file/d/1x_8T4AQmAP6_j5lf2vJ8B9l76DLr_dSR/view\n2. https://drive.google.com/file/d/1x_8T4AQmAP6_j5lf2vJ8B9l76DLr_dSR/view\n3. https://drive.google.com/file/d/1x_8T4AQmAP6_j5lf2vJ8B9l76DLr_dSR/view"
                )
            }
        )

        val bundle = bundleOf(
            BrowseTutorFragment.ARG_TUTOR_DATA to Gson().toJson(data)
        )

        navController.navigateWithAnimation(R.id.tutorHomeFragment, args = bundle)
    }

    private fun formatDateWatcher(): TextWatcher {
        return object : TextWatcher {
            private var isUpdating = false
            private var oldText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                oldText = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /* no-op */
            }

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return

                isUpdating = true

                val input = s.toString().replace("/", "")
                val length = input.length
                val formatted = StringBuilder()

                for (i in input.indices) {
                    formatted.append(input[i])
                    if ((i == 1 || i == 3) && i < length - 1) {
                        formatted.append("/")
                    }
                }

                var cursorPosition = binding.tietDate.selectionStart
                val formattedLength = formatted.length

                if (oldText.length > input.length) {
                    if (cursorPosition > 0 && oldText.getOrNull(cursorPosition - 1) == '/') {
                        cursorPosition--
                    }
                } else {
                    if (cursorPosition in 2..formattedLength && formatted.getOrNull(cursorPosition - 1) == '/') {
                        cursorPosition++
                    }
                }

                binding.tietDate.setText(formatted.toString())
                binding.tietDate.setSelection(cursorPosition.coerceAtMost(formattedLength))

                isUpdating = false
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, selectedYear)
                set(Calendar.MONTH, selectedMonth)
                set(Calendar.DAY_OF_MONTH, selectedDay)
            }
            val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.US)
            binding.tietDate.setText(dateFormat.format(selectedDate.time))
            showTimePicker()
        }, year, month, day)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Time")
            .build()

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val selectedTime = String.format("%02d:%02d", hour, minute)
            print(selectedTime)
        }

        picker.show(parentFragmentManager, "TAG_TIME_PICKER")
    }

    companion object {
        const val B_ENG = "B Inggris"
        const val ALGORITHM = "Algoritma Dasar"
        const val ARG_HISTORY_ITEM = "HISTORY_ITEM"
        const val ARG_FROM_HISTORY = "FROM_HISTORY"
    }
}