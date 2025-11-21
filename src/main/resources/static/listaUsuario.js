let tabla;
$(document).ready(function () {
	initTable();
	initSearchFilter();
	initSelect();
	initButton();
	initModal();
});

function initButtons() {
	$("#modal_new_usuario .btn-primary").click(function() {
		$(this).attr("disabled", true);
		register();
	});
}

function initModal() {
	// vaciado del formulario y habilitacion del boton de registro
	$('#modal_new_usuario').on('show.bs.modal', function (e) {
		$(this).find("form input").val("");
		$(this).find(".btn-primary").attr("disabled", false);
	});
	// focus en el primer input
	$('#modal_new_usuario').on('shown.bs.modal', function() {
		$(this).find("form input")[0].focus();
	});
	
}

function register() {
	let newUser = {
		email : $("#lbl_email").val(),
		password : $("#lbl_password").val(),
		nombre : $("#lbl_nombre").val(),
		role : $("#sel_role").val()
	};
	cargando();
	$.ajax({
		url: `${contextpath}UsuarioController/usuario/register`,
		method: "POST",
		data: JSON.stringify(newUser),
		contentType: "application/json; charset=utf-8",
	})
	.done(function() {
		$("#modal_new_usuario").modal("hide");
		fillTable();
	})
	.fail(function(error) {
		showError(error.responseText);
		$("#modal_new_usuario .btn-primary").attr("disabled", false);
	})
	.always(() => cargaFinalizada())
	;

}

function initTable() {
	var cfgTabla= {
		dom: '<"top"i>rt<"bottom"lp><"clear">',
	    ordering: true,
		paging: true,
		searching: true,// necesario para filtrado
		data: listaUsuario,
		columns: [
			{ title:"Nombre", data:"nombre" },
			{ title:"E-mail", data:"email" },
			{ 
				title:"Perfil", data:"role", 
				render: function(data, type, row, meta) {
					if (data === "ROLE_ADMIN")
						return "Administrador";
					return "Usuario";
				}
			},
			{ 
				title:"Habilitado", data:"enabled",
				render: function(data, type, row, meta) {
					let checked = data ? " checked" : "";
					return `<div class="form-check form-switch"><input class="form-check-input clickleable" data-usuario-id="${row.id}" type="checkbox"${checked}></div>`
				},
				createdCell: function(td, cellData, rowData, row, col) {
					var btn = $(td).find("input");
                    btn.change(changeEnabled);
				}
			},
			{ 
				title:"Editar", data:"id", 
				render: function(data, type, row, meta) {
					return `<a class="btn btn-primary btn-sm" href='${contextpath}usuario/${data}'>ver</a>`;
				},
			},
		],
	};
	cfgTabla["language"]= languageDatatable;
	tabla= $('#table_usuario').DataTable(cfgTabla);
}

function initSearchFilter() {
	$("#lbl_searchtext").on("keyup", function() {
		tabla.draw();
	});
	$("#chk_filtro_enabled,#lbl_searchtext").on("change", function() {
		tabla.draw();
	});
	
	// permite agregar a la busqueda el seleccionador
	$.fn.dataTable.ext.search.push(
		function(settings, searchData, index, rowData, counter) {
			// filtro de habilitado
			let checked = $("#chk_filtro_enabled").prop('checked');// switch filtro
			let usuarioHabilitado = rowData.enabled;// valor del usuario
			if (!checked && !usuarioHabilitado)
				return false;
			
			// filtro de busqueda de texto
			let searchtext = $('#lbl_searchtext').val();
			if (searchtext.length !== 0 &&
					!rowData.nombre.toUpperCase().includes(searchtext.toUpperCase()) &&
					!rowData.email.toUpperCase().includes(searchtext.toUpperCase()))
				return false;
			
			return true;
		});
		
	// filtrado inicial
	tabla.draw();
}

function fillTable() {
	tabla.clear();
	$.get(`${contextpath}UsuarioController/usuario/list`)
	.done(function (data) {
		tabla.rows.add(data).draw();
	});
}

function initSelect() {
	$("#sel_role").select2({
		dropdownParent: $('#modal_new_usuario')
	});
}

function initModal() {
	// vaciado del formulario y habilitacion del boton de registro
	$('#modal_new_usuario').on('show.bs.modal', function (e) {
		$('#modal_new_usuario form input').val("");
		$('#modal_new_usuario form select').val(-1);
		$('#modal_new_usuario form select').change();
		$("#modal_new_usuario .btn-primary").prop("disabled", false);
	});
	// focus en el primer input
	$('#modal_new_usuario').on('shown.bs.modal', function() {
		$('#modal_new_usuario form input')[0].focus();
	});
	
}

function initButton() {
	$("#modal_new_usuario .btn-primary").click(function() {
		$(this).attr("disabled", true);
		register();
	});
}

function changeEnabled(event) {
	let td = event.target;
	let idUsuario = td.getAttribute("data-usuario-id");
	let checked = td.checked;
	cargando();
	$.post(`${contextpath}UsuarioController/usuario/${idUsuario}/changeEnabled/${checked}`)
		.done(data => showToast(`Se ha ${checked ? "habilitado" : "deshabilitado"} el usuario`))
		.fail(error => showError(error.responseText))
		.always(() => cargaFinalizada());
}