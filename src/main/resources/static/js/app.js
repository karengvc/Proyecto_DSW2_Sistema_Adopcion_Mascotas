
const API_URL = "/api";

async function obtenerMascotas() {
    try {
        const respuesta = await fetch(`${API_URL}/mascotas`);

        if (!respuesta.ok) {
            throw new Error("No se pudieron cargar las mascotas");
        }

        return await respuesta.json();
    } catch (error) {
        console.error("Error al consultar mascotas:", error);
        return [];
    }
}

function validarContrasena(contrasena) {
    return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z\d\s])\S{8,}$/.test(contrasena);
}

function validarDni(dni) {
    return /^\d{8}$/.test(dni);
}

function validarTelefono(telefono) {
    return /^\d{9}$/.test(telefono);
}

document.addEventListener("DOMContentLoaded", () => {
    const formularios = document.querySelectorAll("form");

    formularios.forEach(formulario => {
        formulario.addEventListener("submit", evento => {
            const contrasena = formulario.querySelector('[name="password"]');
            const dni = formulario.querySelector('[name="dni"]');
            const telefono = formulario.querySelector('[name="telefono"]');

            if (contrasena && !validarContrasena(contrasena.value)) {
                evento.preventDefault();
                alert("La contraseña debe tener al menos 8 caracteres, mayúscula, minúscula, número y símbolo.");
                contrasena.focus();
                return;
            }

            if (dni && !validarDni(dni.value.trim())) {
                evento.preventDefault();
                alert("El DNI debe contener exactamente 8 números.");
                dni.focus();
                return;
            }

            if (telefono && !validarTelefono(telefono.value.trim())) {
                evento.preventDefault();
                alert("El teléfono debe contener exactamente 9 números.");
                telefono.focus();
            }
        });
    });
});
