$(function() {
    initTable();
    initSearchFilter();
});

function initTable() {
	var cfgTabla= {
		dom: 'rt<"clear">',
	    ordering: true,
		paging: true,
		searching: true,// necesario para filtrado
		processing: true,// ajax
		serverSide: true,// ajax
		pageLength: 4,
		ajax: {
			url: `${contextpath}PersonalController/personal/filter`,
			type: "POST",
			contentType : 'application/json; charset=utf-8',
			data : function(data) {
				data.search = { value : $("#lbl_searchtext").val() };
				return JSON.stringify(data);
			},
		},
		columns: [
			{ title:"Código", data:"codigo", 
				render: (data, type, row) => {
					let accion = `
						<div class="d-flex gap-2 mb-3">
					 		<button class="btn btn-primary btn-sm" type="button" data-personal-codigo="${data}">
					 			<svg class="bi" width="12" height="12" fill="currentColor">
									<use href="${contextpath}webjars/bootstrap-icons/1.10.5/bootstrap-icons.svg#check-circle"></use>
								</svg>
					 			${data}
					 		</button>
					 	</div>
					`;
					return accion;
				},
				createdCell: function(td, cellData, rowData, row, col) {
					$(td).find("button").on("click", seleccionPersonal);
				}
			},
			{ title:"Nombre", data:"nombre" },
			{ 
				title:"Empresa", data:"listaExpositor", orderable: false,
				render: function(data, type, row, meta) {
					if (data.length === 1)
						return data[0].nombre;
						
					// lista de nombre de expositores del personal
					let listaNombreExpositor = data.map(e => e.nombre);
					let textoDespl = listaNombreExpositor.join("\n- ");

					return `${data[0].nombre} 
					<span title="Expositores:\n- ${textoDespl}"><i class="bi bi-exclamation-circle"></i></span>`;
				}
			},
			
			{ title:"Rut", data:"rut", render: (data, type, row) => row.rutCompleto },
			{ 
				title:"Ticket impreso", data:"fechaImpresionTicket",
				render: (data, type, row) => {
					if (data === null)
						return `<span class="badge text-bg-secondary">NO</span>`;
					return `<span class="badge text-bg-success">SI</span>`;
				}
			},
			{ title:"Observaciones", data:"observaciones" },
		],
	};
	cfgTabla["language"]= languageDatatable;
	tabla= $('#table_personal').DataTable(cfgTabla);
}


function initSearchFilter() {
	$("#lbl_searchtext").on("keyup", function(e) {
		let input = e.target;
		let value = input.value;
		if (value.length > 0 && value.length < 3)
			return false;
		tabla.ajax.reload();// recarga la tabla
	});
	
	$("#lbl_searchtext").on("change", function(e) {
		let input = e.target;
		$(input).trigger('keyup');
	});
}


function seleccionPersonal(event) {
	let td = event.target;
	let codigo = td.getAttribute("data-personal-codigo");
	
	$("#lbl_scan_codigo").val(codigo);
	$("#lbl_scan_codigo").trigger("keyup");
}

function resetBusquedaManual() {
	$("#lbl_searchtext").val("");
	tabla.ajax.reload();// recarga la tabla
}
