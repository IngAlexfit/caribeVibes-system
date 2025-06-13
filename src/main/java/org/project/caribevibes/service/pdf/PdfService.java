package org.project.caribevibes.service.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.project.caribevibes.entity.booking.Booking;
import org.project.caribevibes.entity.booking.BookingActivity;
import org.project.caribevibes.exception.BusinessException;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para generar documentos PDF de vouchers de reservas.
 * 
 * Genera documentos PDF profesionales que incluyen toda la información
 * relevante de las reservas, incluyendo datos del huésped, hotel,
 * habitaciones, actividades y totales.
 * 
 * @author Sistema Caribe Vibes
 * @version 1.0
 * @since 2025
 */
@Service
public class PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Genera un voucher PDF para una reserva específica.
     * 
     * @param booking Reserva para la cual generar el voucher
     * @return Array de bytes del documento PDF generado
     * @throws BusinessException Si ocurre un error durante la generación
     */
    public byte[] generateBookingVoucher(Booking booking) {
        logger.info("Generando voucher PDF para reserva ID: {}", booking.getId());
        
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Configurar fuentes
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont regularFont = PdfFontFactory.createFont("Helvetica");

            // Generar contenido del documento
            addHeader(document, boldFont);
            addBookingInfo(document, booking, boldFont, regularFont);
            addGuestInfo(document, booking, boldFont, regularFont);
            addHotelInfo(document, booking, boldFont, regularFont);
            addActivitiesInfo(document, booking, boldFont, regularFont);
            addTotalsInfo(document, booking, boldFont, regularFont);
            addFooter(document, regularFont);

            document.close();
            
            logger.info("Voucher PDF generado exitosamente para reserva ID: {}", booking.getId());
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            logger.error("Error generando PDF para reserva ID {}: {}", booking.getId(), e.getMessage());
            throw new BusinessException("Error generando el voucher PDF: " + e.getMessage());
        }
    }

    /**
     * Añade el encabezado del documento.
     */
    private void addHeader(Document document, PdfFont boldFont) throws IOException {
        Paragraph title = new Paragraph("Caribe Vibes")
                .setFont(boldFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLUE)
                .setMarginBottom(10);
        
        Paragraph subtitle = new Paragraph("VOUCHER DE RESERVA")
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);

        document.add(title);
        document.add(subtitle);
    }

    /**
     * Añade la información básica de la reserva.
     */
    private void addBookingInfo(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 7}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(createHeaderCell("Código de Reserva:", boldFont));
        table.addCell(createDataCell(booking.getBookingCode(), regularFont));

        table.addCell(createHeaderCell("Estado:", boldFont));
        table.addCell(createDataCell(booking.getStatus().toString(), regularFont));

        table.addCell(createHeaderCell("Fecha de Reserva:", boldFont));
        table.addCell(createDataCell(booking.getBookingDate().format(DATETIME_FORMATTER), regularFont));

        table.addCell(createHeaderCell("Check-in:", boldFont));
        table.addCell(createDataCell(booking.getCheckInDate().format(DATE_FORMATTER), regularFont));

        table.addCell(createHeaderCell("Check-out:", boldFont));
        table.addCell(createDataCell(booking.getCheckOutDate().format(DATE_FORMATTER), regularFont));

        table.addCell(createHeaderCell("Número de Huéspedes:", boldFont));
        table.addCell(createDataCell(booking.getNumGuests().toString(), regularFont));

        document.add(new Paragraph("INFORMACIÓN DE LA RESERVA")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(20)
                .setMarginBottom(10));
        document.add(table);
    }

    /**
     * Añade la información del huésped.
     */
    private void addGuestInfo(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 7}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(createHeaderCell("Nombre:", boldFont));
        table.addCell(createDataCell(booking.getUser().getFirstName() + " " + booking.getUser().getLastName(), regularFont));

        table.addCell(createHeaderCell("Email:", boldFont));
        table.addCell(createDataCell(booking.getUser().getEmail(), regularFont));

        if (booking.getSpecialRequests() != null && !booking.getSpecialRequests().trim().isEmpty()) {
            table.addCell(createHeaderCell("Solicitudes Especiales:", boldFont));
            table.addCell(createDataCell(booking.getSpecialRequests(), regularFont));
        }

        document.add(new Paragraph("INFORMACIÓN DEL HUÉSPED")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(20)
                .setMarginBottom(10));
        document.add(table);
    }

    /**
     * Añade la información del hotel y habitación.
     */
    private void addHotelInfo(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 7}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(createHeaderCell("Hotel:", boldFont));
        table.addCell(createDataCell(booking.getHotel().getName(), regularFont));

        table.addCell(createHeaderCell("Dirección:", boldFont));
        table.addCell(createDataCell(booking.getHotel().getAddress(), regularFont));

        table.addCell(createHeaderCell("Destino:", boldFont));
        table.addCell(createDataCell(booking.getHotel().getDestination().getName(), regularFont));

        table.addCell(createHeaderCell("Tipo de Habitación:", boldFont));
        table.addCell(createDataCell(booking.getRoomType().getName(), regularFont));

        table.addCell(createHeaderCell("Descripción:", boldFont));
        table.addCell(createDataCell(booking.getRoomType().getDescription(), regularFont));

        table.addCell(createHeaderCell("Capacidad:", boldFont));
        table.addCell(createDataCell(booking.getRoomType().getCapacity() + " personas", regularFont));

        table.addCell(createHeaderCell("Precio por Noche:", boldFont));
        table.addCell(createDataCell("$" + booking.getRoomType().getPricePerNight().toString(), regularFont));

        document.add(new Paragraph("INFORMACIÓN DEL ALOJAMIENTO")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(20)
                .setMarginBottom(10));
        document.add(table);
    }

    /**
     * Añade la información de actividades incluidas.
     */
    private void addActivitiesInfo(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        if (booking.getActivities() != null && !booking.getActivities().isEmpty()) {
            document.add(new Paragraph("ACTIVIDADES INCLUIDAS")
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            table.addHeaderCell(createHeaderCell("Actividad", boldFont));
            table.addHeaderCell(createHeaderCell("Precio", boldFont));
            table.addHeaderCell(createHeaderCell("Cantidad", boldFont));
            table.addHeaderCell(createHeaderCell("Subtotal", boldFont));

            // Datos de actividades
            for (BookingActivity bookingActivity : booking.getActivities()) {
                table.addCell(createDataCell(bookingActivity.getActivity().getName(), regularFont));
                table.addCell(createDataCell("$" + bookingActivity.getActivity().getPrice().toString(), regularFont));
                table.addCell(createDataCell(bookingActivity.getQuantity().toString(), regularFont));
                table.addCell(createDataCell("$" + bookingActivity.getTotalPrice().toString(), regularFont));
            }

            document.add(table);
        }
    }

    /**
     * Añade la información de totales.
     */
    private void addTotalsInfo(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{7, 3}));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addCell(createHeaderCell("TOTAL A PAGAR:", boldFont));
        table.addCell(createDataCell("$" + booking.getTotalAmount().toString(), boldFont)
                .setFontSize(16)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(new Paragraph("RESUMEN DE COSTOS")
                .setFont(boldFont)
                .setFontSize(14)
                .setMarginTop(20)
                .setMarginBottom(10));
        document.add(table);
    }

    /**
     * Añade el pie de página del documento.
     */
    private void addFooter(Document document, PdfFont regularFont) {
        Paragraph footer = new Paragraph("\n\nGracias por elegir Caribe Vibes. " +
                "Para cualquier consulta, contáctenos a info@caribedreams.com\n" +
                "Este voucher debe ser presentado al momento del check-in.")
                .setFont(regularFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30)
                .setFontColor(ColorConstants.GRAY);

        document.add(footer);
    }

    /**
     * Crea una celda de encabezado con formato.
     */
    private Cell createHeaderCell(String content, PdfFont font) {
        return new Cell()
                .add(new Paragraph(content))
                .setFont(font)
                .setFontSize(12)
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(5);
    }

    /**
     * Crea una celda de datos con formato.
     */
    private Cell createDataCell(String content, PdfFont font) {
        return new Cell()
                .add(new Paragraph(content))
                .setFont(font)
                .setFontSize(11)
                .setPadding(5);
    }
}
