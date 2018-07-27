package com.example.pc.androidpayrbcn;

import android.content.Context;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import java.util.Arrays;

public class PaymentUtils {

    private static final String CURRENCY_CODE = "RUB";
    private static final String TOKENIZATION_PUBLIC_KEY = "1234567";

    //Создание клиента, относительно которого выполняются операции
    public static PaymentsClient getGoogleClientForPayment(Context context) {
        return  Wallet.getPaymentsClient(
                context,
                new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .setTheme( WalletConstants.THEME_LIGHT)
                        .build());
    }


    // Проверка, что подключен Google Pay
    public static void isReadyToPay(PaymentsClient paymentsClient, final ToastNotification notification) {
        notification.showMessage("isReadyToPay");
        IsReadyToPayRequest request = IsReadyToPayRequest.newBuilder()
                        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                        .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                        .build();
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    public void onComplete(Task<Boolean> task) {
                        try {
                            boolean result = task.getResult(ApiException.class);
                            if (result == true) {
                                notification.showMessage("isReadyToPay -> OK");
                                // Show Google as payment option.
                            } else {
                                notification.showMessage("isReadyToPay -> Error");
                                // Hide Google as payment option.
                            }
                        } catch (ApiException exception) {
                            notification.showMessage("isReadyToPay -> Exception");
                        }
                    }
                });
    }


    // Создание запроса
    public static PaymentDataRequest createPaymentDataRequest(String price){
        TransactionInfo transaction = createTransaction(price);
        PaymentDataRequest request = generatePaymentRequest(transaction);
        return request;
    }

    private static TransactionInfo createTransaction(String price) {
        return TransactionInfo.newBuilder()
                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                .setTotalPrice(price)
                .setCurrencyCode(CURRENCY_CODE)
                .build();
    }

    private static PaymentDataRequest generatePaymentRequest(TransactionInfo transaction) {
        PaymentMethodTokenizationParameters tokenParams = PaymentMethodTokenizationParameters
                .newBuilder()
                .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_DIRECT)
                .addParameter("publicKey", TOKENIZATION_PUBLIC_KEY)
                .build();

        return PaymentDataRequest.newBuilder()
                .setPhoneNumberRequired(false)
                .setEmailRequired(true)
                .setShippingAddressRequired(true)
                .setTransactionInfo(transaction)
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .setCardRequirements(
                        CardRequirements.newBuilder()
                                .addAllowedCardNetworks(Arrays.asList(
                                WalletConstants.CARD_NETWORK_VISA,
                                WalletConstants.CARD_NETWORK_MASTERCARD,
                                WalletConstants.CARD_NETWORK_OTHER))
                                .build())
                .setPaymentMethodTokenizationParameters(tokenParams)
                .setUiRequired(true)
                .build();
    }
}