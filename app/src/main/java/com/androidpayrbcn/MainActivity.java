package com.example.pc.androidpayrbcn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 999 ;

    private PaymentsClient paymentsClient;
    private Button payButton;
    private TextView price;
    private static final ToastNotification notification = new ToastNotification(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notification.setContext(this);

        //
        paymentsClient = PaymentUtils.getGoogleClientForPayment(this);
        PaymentUtils.isReadyToPay(paymentsClient, notification);

        price = findViewById(R.id.price);
        payButton = findViewById(R.id.pay_button);
        payButton.setOnClickListener(this);
    }

    // Результат оплаты
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if(data == null) return;
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        String token = paymentData.getPaymentMethodToken().getToken();
                        notification.showMessage(token);
                        break;
                    case Activity.RESULT_CANCELED:
                        // Пользователь нажал назад,
                        // когда был показан диалог google pay
                        // если показывали загрузку или что-то еще,
                        // можете отменить здесь
                        notification.showMessage("Cancel");
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        notification.showMessage(status.getStatusMessage());
                        // Гугл сам покажет диалог ошибки.
                        // Можете вывести логи и спрятать загрузку,
                        // если показывали
                        break;
                    default:
                        // Do nothing.
                }
                break;
            default:
                // Do nothing.
        }
    }

    @Override
    public void onClick(View view) {
        // Кнопка оплаты
        String sPrice = price.getText().toString();
        // Формат цены
        // Целое положительное или
        // Дробное с одним или двумя знаками после точки
        if(!sPrice.equals("")) {
            PaymentDataRequest request = PaymentUtils.createPaymentDataRequest(sPrice);
            if (request != null) {
                AutoResolveHelper.resolveTask(
                        paymentsClient.loadPaymentData(request),
                        this,
                        LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        }
    }
}
