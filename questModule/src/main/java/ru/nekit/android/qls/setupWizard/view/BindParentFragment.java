package ru.nekit.android.qls.setupWizard.view;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;
import java.util.Map;

import ru.nekit.android.qls.R;

import static ru.nekit.android.qls.setupWizard.QuestSetupWizard.Step.SETTINGS;

public class BindParentFragment extends QuestSetupWizardFragment {

    public static final int QR_SIZE = 200;

    private ImageView qrCodeView;
    private String qrCodeInput;
    private Bitmap qrCodeOutput;
    private Handler qrCodeHandler;
    private TextView generateQrCodeView;
    private Runnable generationQrCodeRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.CHARACTER_SET, "windows-1251");
                qrCodeOutput = encodeAsBitmap(qrCodeInput, QR_SIZE, hints);
                qrCodeView.setImageBitmap(qrCodeOutput);
                generateQrCodeView.setVisibility(View.GONE);
            } catch (WriterException exp) {
                exp.printStackTrace();
            }
        }
    };

    public static BindParentFragment getInstance() {
        return new BindParentFragment();
    }

    private void generateQrCode() {
        if (qrCodeHandler == null) {
            (qrCodeHandler = new Handler()).post(generationQrCodeRunnable);
        }
    }

    @Override
    public void onDestroy() {
        if (qrCodeHandler != null) {
            qrCodeHandler.removeCallbacks(generationQrCodeRunnable);
            qrCodeHandler = null;
            qrCodeOutput.recycle();
        }
        super.onDestroy();
    }

    @Override
    protected boolean addToBackStack() {
        return true;
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        qrCodeView = (ImageView) view.findViewById(R.id.image_qr_code);
        generateQrCodeView = (TextView) view.findViewById(R.id.tv_generate_qr_code);
        qrCodeInput = getSetupWizard().createBindCode();
        int qrSize = getContext().getResources().getDimensionPixelSize(R.dimen.qr_code_size);
        RelativeLayout.LayoutParams qrLayoutParams = (RelativeLayout.LayoutParams)
                qrCodeView.getLayoutParams();
        qrLayoutParams.width = qrSize;
        qrLayoutParams.height = qrSize;
        qrCodeView.requestLayout();
        generateQrCode();
        setNextButtonVisibility(false);
        setAltButtonText(R.string.cancel);
    }

    private Bitmap encodeAsBitmap(@NonNull String value, int size, @Nullable Map<EncodeHintType, ?> hints)
            throws WriterException {
        BitMatrix result;
        Bitmap bitmap;
        try {
            result = new MultiFormatWriter().encode(value,
                    BarcodeFormat.QR_CODE, size, size, hints);
            int w = result.getWidth();
            int h = result.getHeight();
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                int offset = y * w;
                for (int x = 0; x < w; x++) {
                    pixels[offset + x] = ContextCompat.getColor(getContext(), result.get(x, y) ? R.color.green : R.color.white);
                }
            }
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_SIZE, 0, 0, w, h);
        } catch (Exception iae) {
            iae.printStackTrace();
            return null;
        }
        return bitmap;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_bind_parent;
    }

    @Override
    protected void altButtonAction() {
        showSetupWizardStep(SETTINGS);
    }
}