package poltrona.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import com.google.zxing.WriterException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import poltrona.dto.ingresso.IngressoResponseDTO;
import poltrona.dto.venda.VendaResponseDTO;

@Service
public class PdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final QrCodeGenerator qrCodeGenerator;

    public PdfService(QrCodeGenerator qrCodeGenerator) {
        this.qrCodeGenerator = qrCodeGenerator;
    }

    public byte[] gerarComprovanteVenda(VendaResponseDTO dto) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A6, 15, 15, 15, 15);
        try {
            PdfWriter.getInstance(document, output);
            document.open();

            Font tituloFonte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font subtituloFonte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font textoFonte = FontFactory.getFont(FontFactory.HELVETICA, 8);
            Font destaqueFonte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);

            Paragraph titulo = new Paragraph("CINE POLTRONA", tituloFonte);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            Paragraph subtitulo = new Paragraph("COMPROVANTE DE VENDA", subtituloFonte);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitulo);

            Paragraph divisor = new Paragraph("--------------------------------------------------", textoFonte);
            divisor.setAlignment(Element.ALIGN_CENTER);
            document.add(divisor);

            String dataFormatada = dto.dataVenda() != null ? dto.dataVenda().format(DATE_FORMATTER) : "N/A";
            document.add(new Paragraph("Código Venda: " + dto.codigoComprovante(), subtituloFonte));
            document.add(new Paragraph("Cinema: " + dto.cinema(), textoFonte));
            document.add(new Paragraph("Cliente: " + dto.cliente(), textoFonte));
            document.add(new Paragraph("Data: " + dataFormatada, textoFonte));
            document.add(new Paragraph("Forma Pagto: " + dto.formaPagamento(), textoFonte));
            document.add(new Paragraph("Status: " + dto.status(), textoFonte));
            document.add(divisor);
            document.add(new Paragraph("ITENS DA COMPRA:", subtituloFonte));

            if (dto.itens() == null || dto.itens().isEmpty()) {
                document.add(new Paragraph("Nenhum item vinculado.", textoFonte));
            } else {
                for (var item : dto.itens()) {
                    document.add(new Paragraph(String.format("- %dx %s (R$ %.2f)",
                            item.quantidade(), item.descricao(), item.precoUnitario()), textoFonte));
                }
            }

            document.add(divisor);
            Paragraph total = new Paragraph(String.format("VALOR TOTAL: R$ %.2f", dto.valorTotal()), destaqueFonte);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            byte[] qrCode = qrCodeGenerator.gerarImagem(
                    "POLTRONA-COMPROVANTE-CODIGO:" + dto.codigoComprovante() + "-VENDA-ID:" + dto.id(),
                    100, 100);
            Image image = Image.getInstance(qrCode);
            image.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph(" ", textoFonte));
            document.add(image);
            document.close();
            return output.toByteArray();
        } catch (DocumentException | IOException | WriterException ex) {
            throw new IllegalStateException("Erro ao gerar PDF do comprovante de venda", ex);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    public byte[] gerarIngresso(IngressoResponseDTO dto) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A6, 20, 20, 20, 20);
        try {
            PdfWriter.getInstance(document, output);
            document.open();

            Font tituloFonte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font subtituloFonte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font textoFonte = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph titulo = new Paragraph("CINE POLTRONA", tituloFonte);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            Paragraph divisor = new Paragraph("--------------------------------------------------", textoFonte);
            divisor.setAlignment(Element.ALIGN_CENTER);
            document.add(divisor);
            document.add(new Paragraph("Cinema: " + dto.cinema(), subtituloFonte));
            document.add(new Paragraph("Filme: " + dto.tituloFilme(), subtituloFonte));
            document.add(new Paragraph("Sala: " + dto.sala(), textoFonte));
            document.add(new Paragraph("Sessão: " + dto.inicioSessao().format(DATE_FORMATTER), textoFonte));
            document.add(new Paragraph("Tipo da sessão: " + dto.tipo(), textoFonte));
            document.add(new Paragraph("Poltrona: " + dto.fileira() + dto.coluna(), subtituloFonte));
            document.add(new Paragraph("Tipo Poltrona: " + dto.tipoPoltrona(), subtituloFonte));
            document.add(new Paragraph("Preço: R$ " + dto.preco(), textoFonte));
            document.add(new Paragraph("Cliente: " + dto.cliente(), textoFonte));
            document.add(new Paragraph("Código do Ingresso: " + dto.id(), textoFonte));
            document.add(new Paragraph("Endereço: " + dto.endereco(), textoFonte));

            byte[] qrCode = qrCodeGenerator.gerarImagem(
                    "POLTRONA-INGRESSO-ID:" + dto.id() + "-CLIENTE:" + dto.cliente(), 120, 120);
            Image image = Image.getInstance(qrCode);
            image.setAlignment(Element.ALIGN_CENTER);
            document.add(new Paragraph(" ", textoFonte));
            document.add(image);
            document.close();
            return output.toByteArray();
        } catch (DocumentException | IOException | WriterException ex) {
            throw new IllegalStateException("Erro ao gerar PDF do ingresso", ex);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}
