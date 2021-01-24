package ge.mov.mobile.ui.fragment.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import coil.load
import coil.request.CachePolicy
import ge.mov.mobile.R
import ge.mov.mobile.ui.adapter.user.DeveloperSocialAdapter
import ge.mov.mobile.databinding.FragmentDeveloperBinding

class FragmentDeveloper : Fragment() {
    private lateinit var binding: FragmentDeveloperBinding
    private lateinit var vm: DeveloperViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_developer, container, false)
        vm = ViewModelProviders.of(this)[DeveloperViewModel::class.java]
        binding.lifecycleOwner = this
        binding.developer = vm
        val view = binding.root

        setDeveloperInfo()

        binding.goBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            activity?.supportFragmentManager?.beginTransaction()?.remove(FragmentDeveloper())?.commitAllowingStateLoss()
        }

        return view
    }

    private fun setDeveloperInfo() {
        binding.developerName.text = vm.getDeveloperInfo()?.name
        binding.developerEmail.text = vm.getDeveloperInfo()?.email
        binding.developerImage.load(vm.loadImage())
        {
            memoryCachePolicy(CachePolicy.DISABLED)
            diskCachePolicy(CachePolicy.DISABLED)
        }

        val social = vm.getSocial()
        if (!social.isNullOrEmpty())
            binding.developerSocialList.adapter = DeveloperSocialAdapter(requireContext(), social)
    }

    override fun onDestroy() {
        super.onDestroy()

        activity?.supportFragmentManager?.beginTransaction()?.remove(FragmentDeveloper())?.commitAllowingStateLoss()
    }
}