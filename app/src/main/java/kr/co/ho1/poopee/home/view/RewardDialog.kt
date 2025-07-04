package kr.co.ho1.poopee.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.ho1.poopee.common.base.BaseDialog
import kr.co.ho1.poopee.databinding.DialogRewardBinding

@SuppressLint("ValidFragment")
class RewardDialog(private var onRewardedAd: (() -> Unit), private var onRewardedInterstitialAd: (() -> Unit)) : BaseDialog() {
    private var _binding: DialogRewardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DialogRewardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListener()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {

    }

    private fun setListener() {
        binding.btnRewardedAd.setOnClickListener {
            onRewardedAd()
            dismiss()
        }
        binding.btnRewardedInterstitialAd.setOnClickListener {
            onRewardedInterstitialAd()
            dismiss()
        }
    }

}