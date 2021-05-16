package ge.mov.mobile.ui.activity.offline.bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ge.mov.mobile.R
import ge.mov.mobile.databinding.BottomFragmentInfoBinding

class BottomInfo : BottomSheetDialogFragment() {

    private var _binding: BottomFragmentInfoBinding? = null
    private val binding: BottomFragmentInfoBinding
        get() = _binding!!

    companion object {
        fun getInstance(title: String, body: String): BottomInfo {
            val bundle = Bundle()
            bundle.putString("header", title)
            bundle.putString("body", body)

            val fragment = BottomInfo()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_fragment_info, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = BottomFragmentInfoBinding.bind(view)

        binding.header.text = requireArguments().getString("header") ?: ""
        binding.body.text = requireArguments().getString("body") ?: ""
    }

    override fun getTheme() = R.style.BottomSheetDialogTheme

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}