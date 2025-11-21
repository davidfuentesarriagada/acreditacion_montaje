$(function() {
	$("#btn_guardar").on("click", guardar);
	
	$("#lbl_rut_nacional").on("keyup", function(e) {
		calculoDv();
	});
	
	$("#lbl_rut_nacional").on("change", function(e) {
		$(this).trigger("keyup");
	});
});

function guardar() {
	// generando el json request
	let rut;
	let extranjero = true;
	let opcion = $('input[name="options"]:checked').val();
	if (opcion === "nacional") {
		extranjero = false;
		rut = $("#lbl_rut_nacional").val();
	}
	else
		rut = $("#lbl_pass_extranjero").val();
	
	let elem = {
		nombre : $("#lbl_nombre").val(),
		rut : rut,
		extranjero: extranjero,
		observaciones: $("#lbl_observaciones").val()
	};
	
	// realizando el request
	cargando();
	$.ajax({
		url: `${contextpath}PersonalController/personal/${codigo}/edit`,
		method: "POST",
		data: JSON.stringify(elem),
		contentType: "application/json; charset=utf-8",
	})
	.done(function() {
		showToast("Se ha realizado la edición");
		location.href = `${contextpath}listaPersonal`
	})
	.fail(function(error) {
		showError(error.responseText);
	})
	.always(() => cargaFinalizada())
	;

}

function calculoDv() {
	let numero = $("#lbl_rut_nacional").val();
	$("#lbl_rut_nacional_dv").text("N.A.");
	if (numero.length < 7 || numero.length > 8)
		return false;
	
	let dv = calcularVerificador(numero);
	$("#lbl_rut_nacional_dv").text(`- ${dv}`);

	function calcularVerificador(numero) {
		let sum = 0;
		let mul = 2;
		
		let i = numero.length;
		while (i--) {
			sum = sum + parseInt(numero.charAt(i)) * mul;
			if (mul % 7 === 0)
				mul = 2;
			else
				mul++;
		}
		
		const res = sum % 11;
		
		if (res === 0)
			return '0';
		else if (res === 1)
			return 'K';
		
		return `${11 - res}`;
	}

}

