
package pe.edu.cibertec.sistemaadopcionmascotas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CorreoService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String correoRemitente;

    public void enviarCodigo(String destinatario, String codigo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(correoRemitente);
        mensaje.setTo(destinatario);
        mensaje.setSubject("Verificación de cuenta - AdoptaPet");
        mensaje.setText(
                "¡Bienvenido a AdoptaPet!\n\n" +
                        "Tu código de verificación es: " + codigo + "\n\n" +
                        "Este código vence en 10 minutos. " +
                        "Si no solicitaste esta cuenta, ignora este mensaje."
        );

        mailSender.send(mensaje);
    }
}
