package com.utils;

/**
 * @Author RenBo
 * @Date 2024-05-07 16:36
 * @PackageName:com.utils
 * @ClassName: QRCodeGenerator
 * @Description: TODO
 * @Version 1.0
 */
import java.io.File;
import java.io.IOException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.util.HashMap;
import java.util.Map;

public class QRCodeGenerator {
    private static final String QR_CODE_IMAGE_PATH = "./QRCode.png";

    public static void main(String[] args) {
        generateQRCodeImage("https://www.baidu.com", 350, 350, QR_CODE_IMAGE_PATH);
    }

    private static void generateQRCodeImage(String text, int width, int height, String filePath) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

            File qrCodeFile = new File(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", qrCodeFile.toPath());
            System.out.println("QR Code generated successfully at path: " + qrCodeFile.getAbsolutePath());
        } catch (WriterException | IOException e) {
            System.out.println("Could not generate QR Code: " + e.getMessage());
        }
    }
}

