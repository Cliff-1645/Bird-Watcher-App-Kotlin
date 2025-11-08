package com.birdwatching.app.util

import android.app.DatePickerDialog
import android.content.Context
import java.util.Calendar
import java.util.Date

class DatePickerHelper(private val context: Context) {
    fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                onDateSelected(selectedDate.time)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}

