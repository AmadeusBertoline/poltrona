package poltrona.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.stereotype.Component;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Component
public class QrCodeGenerator {

    public byte[] gerarImagem(String texto, int largura, int altura) throws WriterException, IOException {
        BitMatrix matrix = new QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, largura, altura);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", output);
        return output.toByteArray();
    }
}
