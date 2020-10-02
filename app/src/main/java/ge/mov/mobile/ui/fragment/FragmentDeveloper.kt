package ge.mov.mobile.ui.fragment

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import ge.mov.mobile.R
import ge.mov.mobile.databinding.FragmentDeveloperBinding
import ge.mov.mobile.ui.viewmodel.DeveloperViewModel

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

        Glide.with(activity!!.applicationContext)
            .asDrawable()
            .error(R.color.colorAccent)
            .placeholder(R.color.colorPrimary)
            .load(vm.loadImage())
            .into(binding.developerImage)

        binding.contactFacebook.setOnClickListener {
            openUrl(vm.getFacebook())
        }

        binding.contactInstagram.setOnClickListener {
            openUrl(vm.getInstagram())
        }

        binding.contactLinkedin.setOnClickListener {
            openUrl(vm.getLinkedIn())
        }

        binding.goBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            activity?.supportFragmentManager?.beginTransaction()?.remove(FragmentDeveloper())?.commitAllowingStateLoss()
        }

        return view
    }

    private fun openUrl(url: String) {
        val intent = Intent(ACTION_VIEW)
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        activity?.supportFragmentManager?.beginTransaction()?.remove(FragmentDeveloper())?.commitAllowingStateLoss()
    }
}