package com.applock.biometric.helpers

//import android.app.Activity
//import android.content.Context
//import android.content.SharedPreferences
//import android.util.Log
//import com.android.billingclient.api.AcknowledgePurchaseParams
//import com.android.billingclient.api.BillingClient
//import com.android.billingclient.api.BillingClient.ProductType
//import com.android.billingclient.api.BillingClientStateListener
//import com.android.billingclient.api.BillingFlowParams
//import com.android.billingclient.api.BillingResult
//import com.android.billingclient.api.PendingPurchasesParams
//import com.android.billingclient.api.ProductDetails
//import com.android.billingclient.api.Purchase
//import com.android.billingclient.api.PurchasesUpdatedListener
//import com.android.billingclient.api.QueryProductDetailsParams
//import com.android.billingclient.api.QueryPurchasesParams
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import kotlin.coroutines.resume
//import kotlin.coroutines.suspendCoroutine
//import androidx.core.content.edit
//
//interface BillingResponses {
//    fun onBillingReady()
//    fun onPurchaseResponseUpdated(purchases: List<Purchase>)
//    fun onSuccessfullyPurchased(purchasedID: String?)
//}
//
//class BillingManager(private val context: Context, private val billingResponses: BillingResponses) :
//    PurchasesUpdatedListener {
//    private lateinit var billingClient: BillingClient
//
//    init {
//        initializeBillingClient()
//    }
//
//    private fun initializeBillingClient() {
//        billingClient =
//            BillingClient.newBuilder(context).setListener(this)
//                .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
//                .build()
//        connectBillingClient()
//    }
//
//    private fun connectBillingClient() {
//        billingClient.startConnection(object : BillingClientStateListener {
//            override fun onBillingServiceDisconnected() {
//                // Retry connection when disconnected
//                connectBillingClient()
//            }
//
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                    Log.d("Purchase", "Billing Connected")
//                    billingResponses.onBillingReady()
//                    // Billing service is ready
//                }
//            }
//        })
//    }
//
//    private fun onPurchaseUpdate(purchases: List<Purchase>) {
//        if (purchases.isNotEmpty()) {
//            for (purchase in purchases) {
//                handlePurchase(purchase)
//            }
//        } else {
//            // No active purchases found
//            Log.d("Purchase", "on purchase found")
//        }
//    }
//
//    override fun onPurchasesUpdated(
//        billingResult: BillingResult,
//        purchases: List<Purchase>?,
//    ) {
//        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
//            Log.d("Purchase", "on purchase update")
//            onPurchaseUpdate(purchases)
//            billingResponses.onPurchaseResponseUpdated(purchases)
//        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
//            // User canceled the purchase flow
//            checkExistingPurchases {}
//            Log.d("Purchase", "User Cancelled")
//        } else {
//            Log.d("Purchase", "Else Occured ${billingResult.responseCode}:::${purchases?.size}")
//        }
//    }
//
//    private fun acknowledgePurchase(purchaseToken: String) {
//        val acknowledgeParams =
//            AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchaseToken).build()
//
//        billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
//            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                // Acknowledgment successful
//            }
//        }
//    }
//
//    fun queryAvailableProducts(
//        array: Array<String>,
//        productDetails: (List<ProductDetails>) -> Unit = {},
//    ) {
//        val products: MutableList<QueryProductDetailsParams.Product> = arrayListOf()
//        array.forEach {
//            products.add(
//                QueryProductDetailsParams.Product.newBuilder().setProductId(it)
//                    .setProductType(ProductType.INAPP).build()
//            )
//        }
//        val queryProductDetailsParams =
//            QueryProductDetailsParams.newBuilder().setProductList(products).build()
//
//        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
//            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                // Display products to the user
//                displayProducts(productDetailsList.productDetailsList)
//                productDetails.invoke(productDetailsList.productDetailsList)
//            }
//        }
//    }
//
//    fun queryAvailableSubscriptions(
//        array: Array<String>,
//        productDetails: (List<ProductDetails>) -> Unit = {},
//    ) {
//        val products: MutableList<QueryProductDetailsParams.Product> = arrayListOf()
//        array.forEach {
//            products.add(
//                QueryProductDetailsParams.Product.newBuilder().setProductId(it)
//                    .setProductType(ProductType.SUBS).build()
//            )
//        }
//        val queryProductDetailsParams =
//            QueryProductDetailsParams.newBuilder().setProductList(products).build()
//
//        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
//            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                // Display products to the user
//                displayProducts(productDetailsList.productDetailsList)
//                productDetails.invoke(productDetailsList.productDetailsList)
//            }
//        }
//    }
//
//    private fun displayProducts(productDetailsList: List<ProductDetails>) {
//        if (productDetailsList.isEmpty()) {
//            Log.d("Purchase", "No product details found.")
//            return
//        }
//
//        productDetailsList.forEach { productDetails ->
//            Log.d("Purchase", "Product ID: ${productDetails.productId}")
//            Log.d("Purchase", "Product Name: ${productDetails.name}")
//            Log.d("Purchase", "Description: ${productDetails.description}")
//            Log.d(
//                "Purchase", "Price: ${productDetails.oneTimePurchaseOfferDetails?.formattedPrice}"
//            )
//            Log.d(
//                "Purchase",
//                "Currency: ${productDetails.oneTimePurchaseOfferDetails?.priceCurrencyCode}"
//            )
//            Log.d(
//                "Purchase",
//                "Price Amount Micros: ${productDetails.oneTimePurchaseOfferDetails?.priceAmountMicros}"
//            )
//            Log.d("Purchase", "Product Type: ${productDetails.productType}")
//            productDetails.subscriptionOfferDetails?.forEach { offerDetails ->
//                Log.d("Purchase", "Subscription Offer Token: ${offerDetails.offerToken}")
//                offerDetails.pricingPhases.pricingPhaseList.forEach { pricingPhase ->
//                    Log.d("Purchase", "Pricing Phase:")
//                    Log.d("Purchase", "  Price: ${pricingPhase.formattedPrice}")
//                    Log.d("Purchase", "  Currency: ${pricingPhase.priceCurrencyCode}")
//                    Log.d("Purchase", "  Price Amount Micros: ${pricingPhase.priceAmountMicros}")
//                    Log.d("Purchase", "  Billing Period: ${pricingPhase.billingPeriod}")
//                    Log.d("Purchase", "  Recurrence Mode: ${pricingPhase.recurrenceMode}")
//                }
//            }
//        }
//        // Implement your UI to display product details
//    }
//
//    fun launchPurchaseFlow(
//        activity: Activity,
//        productDetails: ProductDetails,
//        billingType: String,
//    ) {
//        val productList: MutableList<BillingFlowParams.ProductDetailsParams> = arrayListOf()
//        val offerToken = productDetails.subscriptionOfferDetails?.get(0)?.offerToken
//
//        try {
//            //
//            if (billingType == ProductType.SUBS) {
//                val queryProductDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
//                    .setProductDetails(productDetails).setOfferToken(offerToken!!).build()
//                productList.add(queryProductDetailsParams)
//            } else {
//                val queryProductDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
//                    .setProductDetails(productDetails).build()
//                productList.add(queryProductDetailsParams)
//            }
//
//        } catch (exce: Exception) {
//            exce.printStackTrace()
//        }
//
//        val billingFlowParams =
//            BillingFlowParams.newBuilder().setProductDetailsParamsList(productList).build()
//        billingClient.launchBillingFlow(activity, billingFlowParams)
//    }
//
//    private fun queryExistingPurchases(onResult: (List<Purchase>) -> Unit) {
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val allPurchases = queryAllPurchases(billingClient)
//                withContext(Dispatchers.Main) {
//                    onResult(allPurchases)
//                }
//            } catch (e: Exception) {
//                Log.e("Purchase", "Error: ${e.message}")
//            }
//        }
//    }
//
//    suspend fun queryAllPurchases(billingClient: BillingClient): List<Purchase> {
//        val allPurchases = mutableListOf<Purchase>()
//
//        // Query INAPP purchases
//        val inAppPurchases = queryPurchasesAsync(billingClient, ProductType.INAPP)
//        allPurchases.addAll(inAppPurchases)
//
//        // Query SUBS purchases
//        val subsPurchases = queryPurchasesAsync(billingClient, ProductType.SUBS)
//        allPurchases.addAll(subsPurchases)
//
//        return allPurchases
//    }
//
//    private suspend fun queryPurchasesAsync(
//        billingClient: BillingClient,
//        productType: String,
//    ): List<Purchase> = suspendCoroutine { continuation ->
//        val queryParams = QueryPurchasesParams.newBuilder().setProductType(productType).build()
//
//        billingClient.queryPurchasesAsync(queryParams) { billingResult, purchases ->
//            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                continuation.resume(purchases)
//            } else {
////                continuation.resume()
////                continuation.resumeWithException(
////                    Exception("Error querying purchases: ${billingResult.responseCode}")
////                )
//            }
//        }
//    }
//
//    fun checkSharedPreferenceOnProductId(productId: String): Boolean {
//        return BillingPreferences(context)
//            .isProductPurchased(productId)
//    }
//
//    fun checkSharedPreferenceOnProductList(listProductIds: List<String>): List<String?> {
//        val listPurchsedIds: MutableList<String?> = arrayListOf()
//        val bb = BillingPreferences(context)
//
//        listProductIds.forEachIndexed { index, productId ->
//            if (bb.isProductPurchased(productId)) {
//                listPurchsedIds.add(productId)
//            }
//        }
//
//        // Return null if no product is purchased
//        return listPurchsedIds.toList()
//    }
////    fun checkSharedPreferenceOnProductList(listProductIds: List<String>):List<Boolean>{
////        val listProductIdsBoolean: MutableList<Boolean> = arrayListOf()
////        val bb=
////            com.body.videodownloaderpro.inapppurchase.BillingManager2.BillingPreferences(context)
////        listProductIds.forEachIndexed{index,str->
////            listProductIdsBoolean.add(bb.isProductPurchased(str))
////        }
////        return listProductIdsBoolean.toList()
////    }
//
//    fun checkExistingPurchases2(onResult: () -> Unit) {
//        val bb = BillingPreferences(context)
//        queryExistingPurchases { purchases ->
//            Log.d("TAG_BILLING", "purchases:$purchases ")
//            val activeProducts = purchases.map { it.products }.flatten()
//            bb.sharedPreferences.all.keys.forEach { productId ->
//                if (productId !in activeProducts) {
//                    bb.savePurchaseState(productId, false)
//                }
//            }
//            if (purchases.isNotEmpty()) {
//                for (purchase in purchases) {
//                    handlePurchaseOnCheck(purchase)
//                }
//                onResult.invoke()
//            } else {
//                Log.d("TAG_BILLING", "No Purchase Found")
//
////                setSubscribedIds(context, selectedIdList)
////                setSubscribedId(context, selectedId)
//
//
//                // No active purchases found
//            }
//        }
//    }
//
//    fun checkExistingPurchases(onResult: (Boolean) -> Unit) {
//        val bb = BillingPreferences(context)
//        queryExistingPurchases { purchases ->
//            val activeProducts = purchases.map { it.products }.flatten()
//
//            bb.sharedPreferences.all.keys.forEach { productId ->
//                if (productId !in activeProducts) {
//                    bb.savePurchaseState(productId, false)
//                }
//            }
//
//            if (purchases.isNotEmpty()) {
//                for (purchase in purchases) {
//                    handlePurchaseOnCheck(purchase)
//                }
//                onResult.invoke(true)
//            } else {
//                Log.d("Purchase", "No Purchase Found")
//                // No active purchases found
//                onResult.invoke(false)
//            }
//        }
//    }
//
//    private fun handlePurchase(purchase: Purchase) {
//        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//            // Acknowledge purchase if not already done
//            if (!purchase.isAcknowledged) {
//                acknowledgePurchase(purchase.purchaseToken)
//            }
//
//            if (purchase.products.size > 0) {
//                val ff = purchase.products.first()
//                Log.d("Purchase", "handlepurcashe:$ff")
//                val bb = BillingPreferences(context)
//                bb.savePurchaseState(ff, true)
//            }
//            billingResponses.onSuccessfullyPurchased(purchase.products.firstOrNull())
//        }
//    }
//
//    private fun handlePurchaseOnCheck(purchase: Purchase) {
//        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//            // Acknowledge purchase if not already done
//            Log.d("TAG_BILLING", "handlePurchaseOnCheck")
//            if (!purchase.isAcknowledged) {
//                acknowledgePurchase(purchase.purchaseToken)
//            }
//
//            if (purchase.products.isNotEmpty()) {
//                val ff = purchase.products.first()
//                Log.d("TAG_BILLING", "handlepurcashe:$ff")
//                val bb = BillingPreferences(context)
//                bb.savePurchaseState(ff, true)
//            }
//        }
//    }
//
//    class BillingPreferences(context: Context) {
//        val sharedPreferences: SharedPreferences by lazy {
//            context.getSharedPreferences("purchase_prefs", Context.MODE_PRIVATE)
//        }
//
//        fun savePurchaseState(productId: String, isPurchased: Boolean) {
//            sharedPreferences.edit { putBoolean(productId, isPurchased) }
//        }
//
//        fun isProductPurchased(productId: String): Boolean {
//            return sharedPreferences.getBoolean(productId, false)
//        }
//    }
//
//}


