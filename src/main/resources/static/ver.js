$(function() {
	$("#btn_generate_images").on("click", generateImages);
	$("#btn_email_empresa").on("click", sendEmail);
	$("#btn_delete").on("click", initDelete);
});

function initPrintTicket() {
	if (!imprimeTicketHabilitado) {
		printTicketLocal();
		return;
	}
	
	if (fechaImpresion !== null)
		showConfirm('Volver a imprimir el ticket', '¿Desea volver a imprimir el Ticket?', printTicket);
	else
		printTicket();
}

function printTicket() {
	$.post(`${contextpath}PersonalController/personal/${codigo}/printTicket`)
		.done(function () {
			showToast("Imprimiendo Ticket");
		})
		.fail(function(error) {
			showError(error.responseText);
		});
}

function printTicketLocal() {
	// Habilitar si se quiere imprimir y dejar constancia
	$.post(`${contextpath}PersonalController/personal/${codigo}/setPrintedTicket`)
		.done(function () {
			showToast("Impresión de Ticket");
		})
		.fail(function(error) {
			showError(error.responseText);
		});
	printJS(`/personal/ticket/${codigo}.png`, 'image');
}

function generateImages() {
	$.post(`${contextpath}PersonalController/personal/${codigo}/generateImages`)
	.done(function () {
		showMensaje("Código QR, Ticket y plantilla han sido recreados");
	})
	.fail(function(error) {
		showError(error.responseText);
	});
}

function sendEmail(idExpositor) {
	cargando();
	$.post(`${contextpath}PersonalController/expositor/${idExpositor}/sendEmail`)
	.done(function () {
		showMensaje("Se ha realizado el envío de email");
	})
	.fail(function(error) {
		showError(error.responseText);
	})
	.always(cargaFinalizada())
	;
}

function initDelete() {
	showConfirm("Eliminar personal", "¿Desea eliminar este registro?", doDelete);
}

function doDelete() {
	cargando();
	$.post(`${contextpath}PersonalController/personal/${codigo}/delete`)
		.done(function (data) {
			showMensaje("Se ha eliminado el registro", `${contextpath}listaPersonal`);
		})
		.fail(error => showError(error.responseText))
		.always(() => cargaFinalizada());
}

// ======================
//  Helpers SweetAlert2
// ======================

// ❌ Error
function showError(msg) {
    Swal.fire({
        icon: "error",
        title: "Error",
        text: msg || "Ha ocurrido un error.",
        confirmButtonColor: "#d33"
    });
}

// ✅ Toast de éxito / info
function showToast(msg) {
    Swal.fire({
        toast: true,
        position: "top-end",
        icon: "success",
        title: msg || "Operación realizada correctamente.",
        showConfirmButton: false,
        timer: 2000,
        timerProgressBar: true
    });
}

// ✅ Confirmación genérica con callback (ya usada en initPrintTicket y initDelete)
function showConfirm(title, text, onConfirm) {
    Swal.fire({
        title: title || "¿Está seguro?",
        text: text || "",
        icon: "question",
        showCancelButton: true,
        confirmButtonText: "Sí, continuar",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#0d6efd",
        cancelButtonColor: "#6c757d"
    }).then(result => {
        if (result.isConfirmed && typeof onConfirm === "function") {
            onConfirm();
        }
    });
}

// ✅ Mensaje de éxito, opcionalmente con redirección
//   - showMensaje("Texto")
//   - showMensaje("Texto", "/listaPersonal")
function showMensaje(msg, redirectUrl) {
    Swal.fire({
        icon: "success",
        title: "Operación exitosa",
        text: msg || "La operación se ha realizado correctamente.",
        confirmButtonText: "Aceptar",
        confirmButtonColor: "#0d6efd"
    }).then(result => {
        if (result.isConfirmed && redirectUrl) {
            window.location.href = redirectUrl;
        }
    });
}

