package ge.mov.mobile.ui.fragment

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import coil.request.CachePolicy
import dagger.hilt.android.AndroidEntryPoint
import ge.mov.mobile.R
import ge.mov.mobile.analytics.FirebaseLogger
import ge.mov.mobile.data.model.Social
import ge.mov.mobile.databinding.FragmentDeveloperBinding
import ge.mov.mobile.ui.adapter.user.DeveloperSocialAdapter
import ge.mov.mobile.viewmodel.DeveloperViewModel

@AndroidEntryPoint
class FragmentDeveloper : Fragment(R.layout.fragment_developer),
    DeveloperSocialAdapter.OnSocialItemClicked {
    private lateinit var binding: FragmentDeveloperBinding
    private val vm: DeveloperViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentDeveloperBinding.bind(view)

        setDeveloperInfo()

        binding.goBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            activity?.supportFragmentManager?.beginTransaction()?.remove(FragmentDeveloper())
                ?.commitAllowingStateLoss()
        }
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
            binding.developerSocialList.adapter = DeveloperSocialAdapter(this, social)
    }

    override fun onDestroy() {
        super.onDestroy()

        activity?.supportFragmentManager?.beginTransaction()?.remove(FragmentDeveloper())
            ?.commitAllowingStateLoss()
    }

    override fun onSocialItemClick(i: Social) {
        try {
            FirebaseLogger(requireActivity()).logDeveloperSocialInfoClicked(i.type)

            val intent = Intent(ACTION_VIEW)
            intent.data = Uri.parse(i.url)
            startActivity(intent)
        } catch (e: Exception) {
        }
    }
}