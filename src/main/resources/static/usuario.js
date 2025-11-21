$(function () {
	$("#btn_guardar").on('click', function() {
		showConfirm("Guardar cambios", "¿Desea guardar los cambios?", update);
	});
});

function update() {
	// validacion de campos
	$("#btn_guardar").prop('disabled', true);
	let user = {
		email : $("#lbl_email").val(),
		password : $("#lbl_password").val(),
		nombre : $("#lbl_nombre").val(),
		role : $("#sel_role").val(),
		enabled : $("#chk_enabled").prop("checked")
	};
	cargando();
	$.post(`${contextpath}UsuarioController/usuario/${usuario.id}/edit`, user)
		.done(function (data) {
			showMensaje("Cambios guardados", location.href);
		})
		.fail(function(error) {
			showError(error.responseText)
			cargaFinalizada();
			$("#btn_guardar").prop('disabled', false);
		})
	;
}
