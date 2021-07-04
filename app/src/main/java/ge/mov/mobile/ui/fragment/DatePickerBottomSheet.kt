package ge.mov.mobile.ui.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ge.mov.mobile.R
import ge.mov.mobile.databinding.BottomDatePickerBinding


class DatePickerBottomSheet() : BottomSheetDialogFragment() {

    private var day: Int = 0
    private var month: Int = 0
    private var year: Int = 0

    constructor(day: Int, month: Int, year: Int) : this() {
        this.day = day
        this.month = month - 1
        this.year = year
    }

    var listener: OnDateSelectedListener? = null

    private var _binding: BottomDatePickerBinding? = null
    private val binding: BottomDatePickerBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = BottomDatePickerBinding.bind(view)
        binding.btnOk.setOnClickListener { dismiss() }

        if (day > 0 && year > 0) {
            binding.datePicker.updateDate(year, month, day)
        }
    }

    private fun onClose() {
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        listener?.onDateSelected(day, month + 1, year)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onClose()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { parent ->
                val behaviour = BottomSheetBehavior.from(parent)
                setupFullHeight(parent)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    interface OnDateSelectedListener {
        fun onDateSelected(day: Int, month: Int, year: Int)
    }
}