package org.project.caribevibes.service.pdf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import org.project.caribevibes.entity.booking.Booking;
import org.project.caribevibes.entity.booking.BookingActivity;
import org.project.caribevibes.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio para generar documentos PDF de vouchers de reservas con diseño atractivo.
 * 
 * Genera documentos PDF profesionales estilo caribeño que incluyen toda la información
 * relevante de las reservas con un diseño moderno y colores vibrantes.
 * 
 * @author Sistema Caribe Vibes
 * @version 2.0
 * @since 2025
 */
@Service
public class PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    // Colores temáticos del Caribe
    private static final Color CARIBBEAN_BLUE = new DeviceRgb(0, 123, 255);
    private static final Color TROPICAL_GREEN = new DeviceRgb(40, 167, 69);
    private static final Color SUNSET_ORANGE = new DeviceRgb(255, 193, 7);
    private static final Color CORAL_RED = new DeviceRgb(220, 53, 69);
    private static final Color BEACH_SAND = new DeviceRgb(248, 249, 250);
    private static final Color OCEAN_DEEP = new DeviceRgb(23, 162, 184);

    /**
     * Genera un voucher PDF premium para una reserva específica.
     * 
     * @param booking Reserva para la cual generar el voucher
     * @return Array de bytes del documento PDF generado
     * @throws BusinessException Si ocurre un error durante la generación
     */
    public byte[] generateBookingVoucher(Booking booking) {
        logger.info("Generando voucher PDF premium para reserva ID: {}", booking.getId());
        
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            
            // Configurar márgenes para un diseño más elegante
            document.setMargins(40, 40, 40, 40);

            // Configurar fuentes
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont regularFont = PdfFontFactory.createFont("Helvetica");
            PdfFont lightFont = PdfFontFactory.createFont("Helvetica-Oblique");

            // Generar contenido del documento con diseño premium
            addPremiumHeader(document, booking, boldFont, regularFont);
            addBookingBanner(document, booking, boldFont, regularFont);
            addPremiumBookingInfo(document, booking, boldFont, regularFont);
            addPremiumHotelInfo(document, booking, boldFont, regularFont);
            addPremiumGuestInfo(document, booking, boldFont, regularFont);
            
            if (booking.getActivities() != null && !booking.getActivities().isEmpty()) {
                addPremiumActivitiesInfo(document, booking, boldFont, regularFont);
            }
            
            addPremiumTotalsInfo(document, booking, boldFont, regularFont);
            addTermsAndConditions(document, lightFont);
            addPremiumFooter(document, regularFont, lightFont);

            document.close();
            
            logger.info("Voucher PDF premium generado exitosamente para reserva ID: {}", booking.getId());
            return outputStream.toByteArray();
            
        } catch (IOException e) {
            logger.error("Error generando PDF premium para reserva ID {}: {}", booking.getId(), e.getMessage());
            throw new BusinessException("Error generando el voucher PDF: " + e.getMessage());
        }
    }

    /**
     * Añade un encabezado premium con gradiente y logo.
     */
    private void addPremiumHeader(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        // Crear tabla principal del header
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{2, 6, 2}));
        headerTable.setWidth(UnitValue.createPercentValue(100));
        headerTable.setMarginBottom(30);

        // Logo o icono (simulado con texto estilizado)
        Cell logoCell = new Cell()
                .add(new Paragraph("🏝️")
                        .setFontSize(40)
                        .setTextAlignment(TextAlignment.CENTER))
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(CARIBBEAN_BLUE, 2))
                .setBackgroundColor(BEACH_SAND)
                .setPadding(10);

        // Título principal
        Cell titleCell = new Cell()
                .add(new Paragraph("CARIBE VIBES")
                        .setFont(boldFont)
                        .setFontSize(28)
                        .setFontColor(CARIBBEAN_BLUE)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(5))
                .add(new Paragraph("Voucher de Reserva")
                        .setFont(regularFont)
                        .setFontSize(16)
                        .setFontColor(OCEAN_DEEP)
                        .setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph("🌺 Tu aventura caribeña te espera 🌺")
                        .setFont(regularFont)
                        .setFontSize(12)
                        .setFontColor(TROPICAL_GREEN)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(5))
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(CARIBBEAN_BLUE, 2))
                .setBackgroundColor(new DeviceRgb(240, 248, 255))
                .setPadding(15);

        // Código QR simulado
        Cell qrCell = new Cell()
                .add(new Paragraph("📱")
                        .setFontSize(30)
                        .setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph("QR CODE")
                        .setFont(regularFont)
                        .setFontSize(8)
                        .setTextAlignment(TextAlignment.CENTER))
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(OCEAN_DEEP, 2))
                .setBackgroundColor(BEACH_SAND)
                .setPadding(10);

        headerTable.addCell(logoCell);
        headerTable.addCell(titleCell);
        headerTable.addCell(qrCell);

        document.add(headerTable);
    }

    /**
     * Añade un banner colorido con el estado de la reserva.
     */
    private void addBookingBanner(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        Color statusColor = getStatusColor(booking.getStatus().toString());
        String statusEmoji = getStatusEmoji(booking.getStatus().toString());
        
        Table bannerTable = new Table(UnitValue.createPercentArray(new float[]{1, 4, 1}));
        bannerTable.setWidth(UnitValue.createPercentValue(100));
        bannerTable.setMarginBottom(25);

        Cell bannerCell = new Cell(1, 3)
                .add(new Paragraph(statusEmoji + " " + getStatusText(booking.getStatus().toString()) + " " + statusEmoji)
                        .setFont(boldFont)
                        .setFontSize(20)
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph("Reserva #" + booking.getId())
                        .setFont(regularFont)
                        .setFontSize(14)
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(5))
                .setBackgroundColor(statusColor)
                .setPadding(15)
                .setBorder(new SolidBorder(statusColor, 3));

        bannerTable.addCell(bannerCell);
        document.add(bannerTable);
    }

    /**
     * Añade información de la reserva con diseño moderno.
     */
    private void addPremiumBookingInfo(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        // Título de sección con icono
        Paragraph sectionTitle = new Paragraph("📅 DETALLES DE LA RESERVA")
                .setFont(boldFont)
                .setFontSize(16)
                .setFontColor(CARIBBEAN_BLUE)
                .setMarginBottom(15)
                .setMarginTop(10);
        document.add(sectionTitle);

        // Crear grid de información
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}));
        infoTable.setWidth(UnitValue.createPercentValue(100));

        // Fechas
        addInfoCard(infoTable, "🗓️ Check-in", 
                booking.getCheckInDate().format(DATE_FORMATTER),
                TROPICAL_GREEN, boldFont, regularFont);
        
        addInfoCard(infoTable, "🗓️ Check-out", 
                booking.getCheckOutDate().format(DATE_FORMATTER),
                CORAL_RED, boldFont, regularFont);
        
        addInfoCard(infoTable, "🌙 Noches", 
                String.valueOf(booking.getCheckInDate().until(booking.getCheckOutDate()).getDays()),
                SUNSET_ORANGE, boldFont, regularFont);

        // Segunda fila
        addInfoCard(infoTable, "👥 Huéspedes", 
                booking.getNumberOfGuests().toString(),
                OCEAN_DEEP, boldFont, regularFont);
        
        addInfoCard(infoTable, "🛏️ Habitaciones", 
                booking.getNumberOfRooms().toString(),
                CARIBBEAN_BLUE, boldFont, regularFont);
        
        addInfoCard(infoTable, "📋 Estado", 
                getStatusText(booking.getStatus().toString()),
                getStatusColor(booking.getStatus().toString()), boldFont, regularFont);

        document.add(infoTable);
    }

    /**
     * Añade información del hotel con diseño atractivo.
     */
    private void addPremiumHotelInfo(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        Paragraph sectionTitle = new Paragraph("🏨 INFORMACIÓN DEL ALOJAMIENTO")
                .setFont(boldFont)
                .setFontSize(16)
                .setFontColor(CARIBBEAN_BLUE)
                .setMarginBottom(15)
                .setMarginTop(25);
        document.add(sectionTitle);

        // Crear tabla del hotel
        Table hotelTable = new Table(UnitValue.createPercentArray(new float[]{1}));
        hotelTable.setWidth(UnitValue.createPercentValue(100));

        StringBuilder hotelInfo = new StringBuilder();
        hotelInfo.append("🏖️ ").append(booking.getHotel().getName()).append("\n");
        hotelInfo.append("📍 ").append(booking.getHotel().getAddress()).append("\n");
        
        if (booking.getHotel().getRating() != null) {
            hotelInfo.append("⭐ ").append(getStarRating(booking.getHotel().getRating())).append("\n");
        }
        
        hotelInfo.append("🏠 ").append(booking.getRoomType().getName()).append("\n");
        
        if (booking.getRoomType().getDescription() != null) {
            hotelInfo.append("📝 ").append(booking.getRoomType().getDescription());
        }

        Cell hotelCell = new Cell()
                .add(new Paragraph(hotelInfo.toString())
                        .setFont(regularFont)
                        .setFontSize(12)
                        .setFixedLeading(16))
                .setBackgroundColor(new DeviceRgb(245, 252, 255))
                .setBorder(new SolidBorder(OCEAN_DEEP, 2))
                .setPadding(20);

        hotelTable.addCell(hotelCell);
        document.add(hotelTable);
    }

    /**
     * Añade información del huésped con diseño elegante.
     */
    private void addPremiumGuestInfo(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        Paragraph sectionTitle = new Paragraph("👤 INFORMACIÓN DEL HUÉSPED")
                .setFont(boldFont)
                .setFontSize(16)
                .setFontColor(CARIBBEAN_BLUE)
                .setMarginBottom(15)
                .setMarginTop(25);
        document.add(sectionTitle);

        Table guestTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        guestTable.setWidth(UnitValue.createPercentValue(100));

        // Información personal
        StringBuilder guestInfo = new StringBuilder();
        guestInfo.append("👨‍💼 ").append(booking.getUser().getFirstName())
                .append(" ").append(booking.getUser().getLastName()).append("\n");
        guestInfo.append("📧 ").append(booking.getUser().getEmail()).append("\n");
        
        // El teléfono no existe en la entidad User actual
        guestInfo.append("� Usuario: ").append(booking.getUser().getUsername()).append("\n");

        Cell guestInfoCell = new Cell()
                .add(new Paragraph(guestInfo.toString())
                        .setFont(regularFont)
                        .setFontSize(12)
                        .setFixedLeading(16))
                .setBackgroundColor(new DeviceRgb(248, 255, 248))
                .setBorder(new SolidBorder(TROPICAL_GREEN, 2))
                .setPadding(15);

        // Información de la reserva
        StringBuilder bookingInfo = new StringBuilder();
        bookingInfo.append("📅 Fecha de Reserva:\n")
                .append(booking.getBookingDate().format(DATETIME_FORMATTER)).append("\n\n");
        
        if (booking.getSpecialRequests() != null && !booking.getSpecialRequests().trim().isEmpty()) {
            bookingInfo.append("💭 Solicitudes Especiales:\n")
                    .append(booking.getSpecialRequests());
        } else {
            bookingInfo.append("💭 Sin solicitudes especiales");
        }

        Cell bookingInfoCell = new Cell()
                .add(new Paragraph(bookingInfo.toString())
                        .setFont(regularFont)
                        .setFontSize(12)
                        .setFixedLeading(16))
                .setBackgroundColor(new DeviceRgb(255, 248, 245))
                .setBorder(new SolidBorder(SUNSET_ORANGE, 2))
                .setPadding(15);

        guestTable.addCell(guestInfoCell);
        guestTable.addCell(bookingInfoCell);
        document.add(guestTable);
    }

    /**
     * Añade información de actividades con diseño premium.
     */
    private void addPremiumActivitiesInfo(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        Paragraph sectionTitle = new Paragraph("🎯 ACTIVIDADES INCLUIDAS")
                .setFont(boldFont)
                .setFontSize(16)
                .setFontColor(CARIBBEAN_BLUE)
                .setMarginBottom(15)
                .setMarginTop(25);
        document.add(sectionTitle);

        Table activitiesTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1, 1}));
        activitiesTable.setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        addHeaderCell(activitiesTable, "🎪 Actividad", boldFont);
        addHeaderCell(activitiesTable, "👥 Cantidad", boldFont);
        addHeaderCell(activitiesTable, "💰 Precio", boldFont);
        addHeaderCell(activitiesTable, "💵 Total", boldFont);

        for (BookingActivity bookingActivity : booking.getActivities()) {
            activitiesTable.addCell(createActivityCell(
                    bookingActivity.getActivity().getName() + "\n" + 
                    (bookingActivity.getActivity().getDescription() != null ? 
                     bookingActivity.getActivity().getDescription() : ""),
                    regularFont));
            
            activitiesTable.addCell(createActivityCell(
                    bookingActivity.getQuantity().toString(), 
                    regularFont));
            
            activitiesTable.addCell(createActivityCell(
                    "$" + bookingActivity.getPricePerPerson().toString(), 
                    regularFont));
            
            activitiesTable.addCell(createActivityCell(
                    "$" + bookingActivity.getTotalPrice().toString(), 
                    regularFont));
        }

        document.add(activitiesTable);
    }

    /**
     * Añade información de totales con diseño destacado.
     */
    private void addPremiumTotalsInfo(Document document, Booking booking, PdfFont boldFont, PdfFont regularFont) {
        // Título de sección
        Paragraph sectionTitle = new Paragraph("💰 RESUMEN DE COSTOS")
                .setFont(boldFont)
                .setFontSize(16)
                .setFontColor(CARIBBEAN_BLUE)
                .setMarginBottom(15)
                .setMarginTop(25);
        document.add(sectionTitle);

        // Calcular desglose
        BigDecimal roomTotal = booking.getRoomType().getPricePerNight()
                .multiply(BigDecimal.valueOf(booking.getCheckInDate().until(booking.getCheckOutDate()).getDays()));
        
        BigDecimal activitiesTotal = booking.getActivities().stream()
                .map(BookingActivity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Table totalsTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
        totalsTable.setWidth(UnitValue.createPercentValue(100));

        // Desglose
        addTotalRow(totalsTable, "🏨 Alojamiento (" + 
                booking.getCheckInDate().until(booking.getCheckOutDate()).getDays() + " noches)",
                "$" + roomTotal.toString(), regularFont);
        
        if (activitiesTotal.compareTo(BigDecimal.ZERO) > 0) {
            addTotalRow(totalsTable, "🎯 Actividades", 
                    "$" + activitiesTotal.toString(), regularFont);
        }

        // Separador
        Cell separatorCell = new Cell(1, 2)
                .setBorderTop(new SolidBorder(CARIBBEAN_BLUE, 2))
                .setBorderBottom(new SolidBorder(CARIBBEAN_BLUE, 2))
                .setPadding(5);
        totalsTable.addCell(separatorCell);

        // Total final
        Cell totalLabelCell = new Cell()
                .add(new Paragraph("💎 TOTAL A PAGAR")
                        .setFont(boldFont)
                        .setFontSize(16)
                        .setFontColor(TROPICAL_GREEN))
                .setBackgroundColor(new DeviceRgb(240, 255, 240))
                .setPadding(15)
                .setTextAlignment(TextAlignment.RIGHT);

        Cell totalAmountCell = new Cell()
                .add(new Paragraph("$" + booking.getTotalPrice().toString())
                        .setFont(boldFont)
                        .setFontSize(20)
                        .setFontColor(TROPICAL_GREEN))
                .setBackgroundColor(new DeviceRgb(240, 255, 240))
                .setPadding(15)
                .setTextAlignment(TextAlignment.CENTER);

        totalsTable.addCell(totalLabelCell);
        totalsTable.addCell(totalAmountCell);

        document.add(totalsTable);
    }

    /**
     * Añade términos y condiciones.
     */
    private void addTermsAndConditions(Document document, PdfFont lightFont) {
        Paragraph terms = new Paragraph("📋 TÉRMINOS Y CONDICIONES")
                .setFont(lightFont)
                .setFontSize(12)
                .setFontColor(OCEAN_DEEP)
                .setMarginTop(30)
                .setMarginBottom(10);
        
        Paragraph termsText = new Paragraph(
                "• Este voucher es válido únicamente para las fechas especificadas.\n" +
                "• Presentar este documento al momento del check-in junto con identificación oficial.\n" +
                "• Cancelaciones deben realizarse con al menos 48 horas de anticipación.\n" +
                "• Las actividades están sujetas a disponibilidad y condiciones climáticas.\n" +
                "• Para cambios o consultas, contactar a nuestro equipo de atención al cliente.")
                .setFont(lightFont)
                .setFontSize(10)
                .setFontColor(new DeviceRgb(100, 100, 100))
                .setFixedLeading(12);

        document.add(terms);
        document.add(termsText);
    }

    /**
     * Añade un pie de página premium.
     */
    private void addPremiumFooter(Document document, PdfFont regularFont, PdfFont lightFont) {
        // Información de contacto
        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}));
        footerTable.setWidth(UnitValue.createPercentValue(100));
        footerTable.setMarginTop(30);

        Cell contactCell = new Cell()
                .add(new Paragraph("📞 CONTACTO")
                        .setFont(regularFont)
                        .setFontSize(12)
                        .setFontColor(CARIBBEAN_BLUE))
                .add(new Paragraph("📧 reservas@caribevibes.com\n📱 +57 1 234 5678\n🌐 www.caribevibes.com")
                        .setFont(lightFont)
                        .setFontSize(10)
                        .setFixedLeading(12))
                .setBackgroundColor(BEACH_SAND)
                .setPadding(15)
                .setBorder(new SolidBorder(CARIBBEAN_BLUE, 1));

        Cell emergencyCell = new Cell()
                .add(new Paragraph("🚨 EMERGENCIAS")
                        .setFont(regularFont)
                        .setFontSize(12)
                        .setFontColor(CORAL_RED))
                .add(new Paragraph("24/7 Asistencia\n📱 +57 300 123 4567\n🏥 Servicios médicos")
                        .setFont(lightFont)
                        .setFontSize(10)
                        .setFixedLeading(12))
                .setBackgroundColor(new DeviceRgb(255, 245, 245))
                .setPadding(15)
                .setBorder(new SolidBorder(CORAL_RED, 1));

        Cell socialCell = new Cell()
                .add(new Paragraph("🌐 SÍGUENOS")
                        .setFont(regularFont)
                        .setFontSize(12)
                        .setFontColor(TROPICAL_GREEN))
                .add(new Paragraph("📘 Facebook: /CaribeVibes\n📷 Instagram: @caribevibes\n🐦 Twitter: @caribevibes")
                        .setFont(lightFont)
                        .setFontSize(10)
                        .setFixedLeading(12))
                .setBackgroundColor(new DeviceRgb(245, 255, 245))
                .setPadding(15)
                .setBorder(new SolidBorder(TROPICAL_GREEN, 1));

        footerTable.addCell(contactCell);
        footerTable.addCell(emergencyCell);
        footerTable.addCell(socialCell);

        document.add(footerTable);

        // Mensaje final
        Paragraph farewell = new Paragraph("🌴 ¡Gracias por elegir Caribe Vibes para tu aventura! 🌴")
                .setFont(regularFont)
                .setFontSize(14)
                .setFontColor(CARIBBEAN_BLUE)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);

        Paragraph generated = new Paragraph("Voucher generado el " + 
                LocalDateTime.now().format(DATETIME_FORMATTER) + " | ID: " + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .setFont(lightFont)
                .setFontSize(8)
                .setFontColor(new DeviceRgb(150, 150, 150))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);

        document.add(farewell);
        document.add(generated);
    }

    // =================== MÉTODOS AUXILIARES ===================

    /**
     * Crea una tarjeta de información con colores temáticos.
     */
    private void addInfoCard(Table table, String label, String value, Color color, PdfFont boldFont, PdfFont regularFont) {
        Cell infoCard = new Cell()
                .add(new Paragraph(label)
                        .setFont(boldFont)
                        .setFontSize(10)
                        .setFontColor(color)
                        .setMarginBottom(5))
                .add(new Paragraph(value)
                        .setFont(regularFont)
                        .setFontSize(14)
                        .setFontColor(ColorConstants.BLACK))
                .setBackgroundColor(new DeviceRgb(250, 250, 250))
                .setBorder(new SolidBorder(color, 2))
                .setPadding(12)
                .setTextAlignment(TextAlignment.CENTER);
        
        table.addCell(infoCard);
    }

    /**
     * Añade una fila de total.
     */
    private void addTotalRow(Table table, String label, String amount, PdfFont font) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setFont(font).setFontSize(12))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setPadding(8));
        
        table.addCell(new Cell()
                .add(new Paragraph(amount).setFont(font).setFontSize(12))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setPadding(8)
                .setTextAlignment(TextAlignment.RIGHT));
    }

    /**
     * Crea una celda de encabezado para tablas.
     */
    private void addHeaderCell(Table table, String text, PdfFont boldFont) {
        Cell headerCell = new Cell()
                .add(new Paragraph(text)
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(CARIBBEAN_BLUE)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);
        
        table.addCell(headerCell);
    }

    /**
     * Crea una celda de actividad.
     */
    private Cell createActivityCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text)
                        .setFont(font)
                        .setFontSize(10))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setPadding(8);
    }

    /**
     * Obtiene el color según el estado de la reserva.
     */
    private Color getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "CONFIRMED":
                return TROPICAL_GREEN;
            case "PENDING":
                return SUNSET_ORANGE;
            case "CANCELLED":
                return CORAL_RED;
            case "COMPLETED":
                return OCEAN_DEEP;
            default:
                return ColorConstants.GRAY;
        }
    }

    /**
     * Obtiene el emoji según el estado de la reserva.
     */
    private String getStatusEmoji(String status) {
        switch (status.toUpperCase()) {
            case "CONFIRMED":
                return "✅";
            case "PENDING":
                return "⏳";
            case "CANCELLED":
                return "❌";
            case "COMPLETED":
                return "🎉";
            default:
                return "📋";
        }
    }

    /**
     * Obtiene el texto amigable del estado.
     */
    private String getStatusText(String status) {
        switch (status.toUpperCase()) {
            case "CONFIRMED":
                return "CONFIRMADA";
            case "PENDING":
                return "PENDIENTE";
            case "CANCELLED":
                return "CANCELADA";
            case "COMPLETED":
                return "COMPLETADA";
            default:
                return status;
        }
    }

    /**
     * Genera una representación visual de estrellas.
     */
    private String getStarRating(BigDecimal rating) {
        if (rating == null || rating.compareTo(BigDecimal.ONE) < 0 || rating.compareTo(BigDecimal.valueOf(5)) > 0) {
            return "Sin calificación";
        }
        
        StringBuilder stars = new StringBuilder();
        int fullStars = rating.intValue();
        for (int i = 0; i < fullStars; i++) {
            stars.append("⭐");
        }
        return stars.toString() + " (" + rating + "/5)";
    }
}
