let _changeInterval;
let codigo;
let webcamBusy = false;
let html5QrcodeScanner;
$(function() {
	$("#lbl_scan_codigo").on("keyup", function() {
		clearInterval(_changeInterval);
		_changeInterval = setInterval(function() {
			scan();
			clearInterval(_changeInterval);
		}, 500);
	});
	
    setupWebcam();

});

document.addEventListener("DOMContentLoaded", function(){
	resetCursor();
});


function scan() {
	let search = $("#lbl_scan_codigo").val();
	$("#lbl_scan_codigo").val("");
	$("#img_ticket").empty();
	if (search.length === 0)
		return;
	
	cargando();
	$.get(`${contextpath}PersonalController/personal/scanQr`, { texto: search })
	.done(function (data) {
		codigo = data.codigo;
		// agregando la imagen del ticket en la vista
		$("#img_ticket").append($("<img>", {
			"src": `/personal/ticket/${codigo}.png`,
			"width": "239",
			"height": "100"
			}));
		if (data.fechaImpresionTicket !== null)
			showConfirm('Volver a imprimir el ticket', '¿Desea volver a imprimir el Ticket?', printTicket);
		else
			printTicket();
	})
	.fail(function(error) {
		showError(error.responseText);
	})
	.always(() => {
		setTimeout(enableWebcam, 2000);// espera para volver a escanear
		cargaFinalizada();
	})
	;

}

function printTicket() {
	$.post(`${contextpath}PersonalController/personal/${codigo}/setPrintedTicket`)
		.done(function () {
			showToast("Impresión de Ticket");
		})
		.fail(function(error) {
			showError(error.responseText);
		});
	
	printJS(`/personal/ticket/${codigo}.png`, 'image');
}

function setupWebcam() {
	html5QrcodeScanner = new Html5QrcodeScanner(
		"reader", { 
			fps: 10,
			formatsToSupport: [Html5QrcodeSupportedFormats.QR_CODE],
			qrbox: 250 });
		
	html5QrcodeScanner.render((decodedText, decodedResult) => {
	    document.getElementById('lbl_scan_codigo').value = decodedText;
	    //html5QrcodeScanner.clear();// destruccion del feed
	    if (webcamBusy)
	    	return true;
	    	
	    // TODO ocultar div render
	    webcamBusy = true;
	    $("#lbl_scan_codigo").trigger("keyup");
	} );
}

function enableWebcam() {
    webcamBusy = false;
	// TODO mostrar div render
}

function resetCursor() {
	$("#lbl_scan_codigo").val("");
    $("#lbl_scan_codigo").focus();
}
