"use strict";

function cargando() {
	$('#modalCargando').off('shown.bs.modal');// elimina evento asignado en carga finalizada
	$("#modalCargando").modal("show");
}

function cargaFinalizada() {
	if ($('#modalCargando').hasClass('show'))
		$("#modalCargando").modal("hide");
	// permite que se oculte la barra de carga si se encuentra en periodo de transicion
	else {
		$("#modalCargando").modal("hide");
		$('#modalCargando').on('shown.bs.modal', e => $('#modalCargando').modal("hide"));
	}
}

function formatNumber(numero) {
	return new Intl.NumberFormat('es-CL').format(numero);
}

function formatCurrency(numero) {
	return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP' }).format(numero);
}

function showConfirm(titulo, consulta, funcionCallback) {
	$("#modal-confirm .modal-title").text(titulo);
	$("#modal-confirm .modal-body").text(consulta);
	$("#modal-confirm").modal("show");
	
	$("#modal-confirm .btn-primary").off('click');
	$("#modal-confirm .btn-primary").on("click", function() {
		funcionCallback();
		$("#modal-confirm").modal("hide");
	});
}

function intVal(i) {
	if (typeof i === 'string')
		return i.replace(/[\$,]/g, '') * 1;
	else if (typeof i === 'number')
		return i;
	return 0;
}

// error de datatables
$.fn.dataTable.ext.errMode = function(settings, techNote, message) {
	let error = message.split(" - ");// DataTables warning: table id=table_orden_trabajo - Sucursal no existe
	showError(error[1]);
};

/**
 * jqueryIdTabla ejemplo "#table_mantenciones_ot"
 */
function scrollToTable(jqueryIdTabla) {
	$("html").animate({
		scrollTop: $(jqueryIdTabla).offset().top
		},800
	);
}

function getValorInput(valor) {
	valor = $.trim(valor);
	if (valor === "")
		return null;
	return valor;
}

function getIntValorInput(valor) {
	valor = $.trim(valor);
	if (valor === "")
		return null;
	return parseInt(valor);
}

function showToast(mensaje) {
	var myToastEl = document.getElementById('toast-info');
	var myToast = bootstrap.Toast.getOrCreateInstance(myToastEl);
	let areaMensaje = $(myToastEl).find(".toast-body");
	areaMensaje.text(mensaje);
	myToast.show();
}

function showMensaje(mensaje, href) {
	let modalAlerta = $("#modal-alerta");
	
	// transicion en caso de requerirse 
	modalAlerta.off('hidden.bs.modal');
	if (typeof href !== "undefined") {
		modalAlerta.on('hidden.bs.modal', function () {
			location.href= href;
		});
	}
	
	// cierre al click en boton enter
	modalAlerta.off('keypress');
	modalAlerta.on('keypress', function (event) {
		var keycode = (event.keyCode ? event.keyCode : event.which);
		if(keycode == '13')
			modalAlerta.modal("hide");
	});
	
	// cuerpo del modal
	modalAlerta.find(".modal-header").attr("class","modal-header alert alert-primary d-flex align-items-center");
	modalAlerta.find(".modal-title").html(`<i class="bi bi-info-circle-fill"></i>INFORMACIÓN`);
	modalAlerta.find(".modal-body").text(mensaje);
	modalAlerta.modal("show");
	
}

function showError(error) {
	let modalAlerta = $("#modal-alerta");
	
	// cierre al click en boton enter
	modalAlerta.off('keypress');
	modalAlerta.on('keypress', function (event) {
		var keycode = (event.keyCode ? event.keyCode : event.which);
		if(keycode == '13')
			modalAlerta.modal("hide");
	});
	
	// cuerpo del modal
	modalAlerta.find(".modal-header").attr("class","modal-header alert alert-danger d-flex align-items-center");
	modalAlerta.find(".modal-title").html(`<i class="bi bi-exclamation-triangle-fill"></i>ERROR`);
	modalAlerta.find(".modal-body").text(error);
	modalAlerta.modal("show");
}

