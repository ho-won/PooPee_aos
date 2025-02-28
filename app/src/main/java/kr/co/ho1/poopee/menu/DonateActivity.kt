package kr.co.ho1.poopee.menu

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import kr.co.ho1.poopee.R
import kr.co.ho1.poopee.common.base.BaseActivity
import kr.co.ho1.poopee.common.dialog.BasicDialog
import kr.co.ho1.poopee.common.util.CustomBackgroundSpan
import kr.co.ho1.poopee.common.util.LogManager
import kr.co.ho1.poopee.common.util.MyUtil
import kr.co.ho1.poopee.databinding.ActivityDonateBinding

class DonateActivity : BaseActivity(), PurchasesUpdatedListener {
    private lateinit var binding: ActivityDonateBinding

    private lateinit var billingClient: BillingClient
    private var productList: List<ProductDetails> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        init()
        setListener()
    }

    private fun init() {
        setTitle()
        setupBillingClient()
    }

    private fun setListener() {
        binding.btnPrice01.setOnClickListener {
            if (productList.size > 0) {
                makePurchase(productList[0])
            }
        }
        binding.btnPrice02.setOnClickListener {
            if (productList.size > 1) {
                makePurchase(productList[1])
            }
        }
        binding.btnPrice03.setOnClickListener {
            if (productList.size > 2) {
                makePurchase(productList[2])
            }
        }
    }

    private fun setTitle() {
        val text = MyUtil.getString(R.string.menu_donate_01)
        val spannableString = SpannableString(text)
        val start = text.indexOf("PooPee")
        val end = start + "PooPee".length

        spannableString.setSpan(
            CustomBackgroundSpan(Color.parseColor("#a7efdd")),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvTitle.text = spannableString
    }


    private fun setupBillingClient() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // 결제 클라이언트가 준비되었습니다.
                    queryAvailableProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // 여기서 재연결 로직을 구현합니다.
            }
        })
    }

    private fun queryAvailableProducts() {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("poopee_donate_01")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("poopee_donate_02")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("poopee_donate_03")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                this.productList = productDetailsList
                LogManager.e("this.productDetailsList : ${this.productList.size}")
                Handler(Looper.getMainLooper()).post {
                    if (this.productList.size > 0) {
                        binding.btnPrice01.text = getProductPrice(this.productList[0])
                        binding.btnPrice01.visibility = View.VISIBLE
                    }
                    if (this.productList.size > 1) {
                        binding.btnPrice02.text = getProductPrice(this.productList[1])
                        binding.btnPrice02.visibility = View.VISIBLE
                    }
                    if (this.productList.size > 2) {
                        binding.btnPrice03.text = getProductPrice(this.productList[2])
                        binding.btnPrice03.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun getProductPrice(productDetails: ProductDetails): String {
        return when (productDetails.productType) {
            BillingClient.ProductType.INAPP -> {
                val offer = productDetails.oneTimePurchaseOfferDetails
                offer?.formattedPrice ?: "가격 정보 없음"
            }

            BillingClient.ProductType.SUBS -> {
                val offers = productDetails.subscriptionOfferDetails
                offers?.firstOrNull()?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice
                    ?: "가격 정보 없음"
            }

            else -> "알 수 없는 상품 유형"
        }
    }

    private fun makePurchase(product: ProductDetails) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(product)
                        .build()
                )
            )
            .build()

        billingClient.launchBillingFlow(this, billingFlowParams)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // 사용자가 구매를 취소했습니다.
        } else {
            // 구매 처리 중 오류가 발생했습니다.
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        LogManager.e("Purchase acknowledged")

                        // 상품 소비 처리
                        val consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()

                        billingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                LogManager.e("Purchase consumed successfully")
                                // 여기에서 사용자에게 아이템을 제공하는 로직을 구현하세요
                            } else {
                                LogManager.e("Error consuming purchase: ${billingResult.debugMessage}")
                            }
                            LogManager.e("purchaseToken: ${purchase.purchaseToken}")
                        }
                    }
                }
            }

            val dialog = BasicDialog(
                onLeftButton = {

                },
                onCenterButton = {

                },
                onRightButton = {
                    finish()
                }
            )
            dialog.setTextContent(MyUtil.getString(R.string.menu_donate_03))
            dialog.setBtnRight(MyUtil.getString(R.string.confirm))
            dialog.show(supportFragmentManager, "BasicDialog")
        }
    }

    override fun setToolbar() {
        binding.toolbar.setTitle(MyUtil.getString(R.string.nav_text_09))
        binding.toolbar.setImageLeftOne(MyUtil.getDrawable(R.drawable.ic_navigationbar_back))
        binding.toolbar.setSelectedListener(
            onBtnLeftOne = {
                finish()
            },
            onBtnLeftTwo = {

            },
            onBtnRightOne = {

            },
            onBtnRightTwo = {

            }
        )
    }

}