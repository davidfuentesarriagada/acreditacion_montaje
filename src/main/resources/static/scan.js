let _changeInterval;
let codigo;
let webcamBusy = false;
let html5QrcodeScanner;
$(function() {
	// scan on enter
	$("#lbl_scan_codigo").on("keypress", function(event) {
		if(event.which === 13) {
			scan();
			return false;
		}
	});
	
	$("#lbl_scan_codigo").on('hidden.bs.modal', function (e) { 
		resetCursor();
	});
	
    setupWebcam();
});

document.addEventListener("DOMContentLoaded", function(){
	resetCursor();
});


function scan() {
	let search = $("#lbl_scan_codigo").val();
	resetCursor();
	if (search.length === 0)
		return;
	
	cargando();
	hideWebcam();
	hideOk();
	$.get(`${contextpath}PersonalController/personal/scanQr`, { texto: search })
	.done(function (data) {
		codigo = data.codigo;
		// soporte a codigos no asociados a personal
		if (data.idPersonal !== null) {
			// agregando la imagen del ticket en la vista
			$("#result_ok .result").append($("<img>", {
				"src": `/personal/ticket/${codigo}.png`,
				"width": "239",
				"height": "100"
				}));
			if (marcaAsistenciaHabilitado == "true")
				showToast(`${data.nombre} registrado como asistente`);
		}
		else {
			// agregando la imagen del ticket en la vista
			$("#result_ok .result").append(`<div class="alert alert-success" role="alert">${codigo} registrado como asistente</div>`);
			if (marcaAsistenciaHabilitado == "true")
				showToast(`${codigo} registrado como asistente`);
		}
		
		// indicador de ticket impreso (ignorar si solo esta en modo marcado de
		// asistencia)
		if (imprimeTicketHabilitado == "true" && data.ticketImpreso)
			showOkPrinted();
		showOk();
		
		// reset del buscador manual si aplica
		if (imprimeTicketHabilitado == "true")
			resetBusquedaManual();
	})
	.fail(function(error) {
		showError(error.responseText);
		showNoOk();
	})
	.always(() => {
		setTimeout(enableWebcam, 2000);// espera para volver a escanear
		cargaFinalizada();
	})
	;

}

function setupWebcam() {
	html5QrcodeScanner = new Html5QrcodeScanner(
		"reader", { 
			fps: 10,
			formatsToSupport: [Html5QrcodeSupportedFormats.QR_CODE],
			qrbox: 250 });
		
	html5QrcodeScanner.render((decodedText, decodedResult) => {
	    document.getElementById('lbl_scan_codigo').value = decodedText;
	    // html5QrcodeScanner.clear();// destruccion del feed
	    if (webcamBusy)
	    	return true;
	    	
	    webcamBusy = true;
	    hideWebcam();
	    $("#lbl_scan_codigo").trigger("keyup");
	} );
	
}

function enableWebcam() {
    webcamBusy = false;
    resetCursor();
    showWebcam();
}

function showWebcam() {
	$("#reader").removeClass("d-none");
	$("#result_no_ok").addClass("d-none");
}

function hideWebcam() {
	$("#reader").addClass("d-none");
}

function showOkPrinted() {
	$("#result_ok_printed").removeClass("d-none");
}

function showOk() {
	$("#result_ok").removeClass("d-none");
}

function hideOk() {
	$("#result_ok").addClass("d-none");
	$("#result_ok_printed").addClass("d-none");
	$("#result_ok .result").empty();
}

function showNoOk() {
	$("#result_no_ok").removeClass("d-none");
}

function resetCursor() {
	$("#lbl_scan_codigo").val("");
    $("#lbl_scan_codigo").focus();
}

