const API_PAISES = "https://restcountries.com/v3.1/all?fields=name,translations";


let tabla;
$(document).ready(function () {
    initButtons();
    initModal();
    initTable();

    $("#lbl_rut_nacional").on("keyup", function(e) {
        calculoDv();
    });

    $("#lbl_rut_nacional").on("change", function(e) {
        $(this).trigger("keyup");
    });
	
	// 🔹 NUEVO: cada vez que cambie entre Rut / Pasaporte
    $('input[name="options"]').on('change', function () {
        actualizarEstadoNacionalidad();
    });

});

function initButtons() {
    $("#modal_new_personal .btn-primary").click(function() {
        $(this).attr("disabled", true);
        register();
    });
    $("#btn_export_list").on('click', function() {
        window.open(`${contextpath}PersonalController/personal/lista/exportar`, '_self');
    });

    $("#btn_plantilla_qr").on('click', function() {
        cargando();
        $.post(`${contextpath}PersonalController/personal/generatePlantilla/all`)
            .done(function (data) {
                showToast("Se han generado las plantillas QR");
            })
            .fail(error => showError(error.responseText))
            .always(() => cargaFinalizada());
    });

}

function initModal() {
    // vaciado del formulario y habilitacion del boton de registro
    $('#modal_new_personal').on('show.bs.modal', function (e) {
        $(this).find("form input[type!='radio']").val("");// ignora radio para que no le quite el value
        $(this).find(".btn-primary").attr("disabled", false);
        $(this).find("form input[name='options']:first").prop('checked', true);

        // Reset nacionalidad
        $("#lbl_nacionalidad").val("");

        // 🔹 Muy importante: dejar visible/oculto según la opción actual (por defecto "nacional")
        actualizarEstadoNacionalidad();
    });

    // focus en el primer input
    $('#modal_new_personal').on('shown.bs.modal', function() {
        $(this).find("form input")[0].focus();
    });
}



function register() {
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

    let newElem = {
        nombre : $("#lbl_nombre").val(),
        rut : rut,
        empresa : $("#lbl_empresa").val(),
        email : $("#lbl_email").val(),
        extranjero: extranjero,
        // 🔹 NUEVO:
        nacionalidad: $("#lbl_nacionalidad").val(),
        observaciones: $("#lbl_observaciones").val()
    };

    // realizando el request
    cargando();
    $.ajax({
        url: `${contextpath}PersonalController/personal/register`,
        method: "POST",
        data: JSON.stringify(newElem),
        contentType: "application/json; charset=utf-8",
    })
        .done(function() {
            tabla.ajax.reload();// recarga la tabla
            $("#modal_new_personal").modal("hide");
            showToast("Se ha realizado el registro");
        })
        .fail(function(error) {
            showError(error.responseText);
            $("#modal_new_personal .btn-primary").attr("disabled", false);
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

function initTable() {
    var cfgTabla= {
        dom: '<"top"i>frt<"bottom"lp><"clear">',
        ordering: true,
        paging: true,
        searching: true,// necesario para filtrado
        processing: true,// ajax
        serverSide: true,// ajax
        ajax: {
            url: `${contextpath}PersonalController/personal/filter`,
            type: "POST",
            contentType : 'application/json; charset=utf-8',
            data : function(data) {
                return JSON.stringify(data);
            },
        },
        columns: [
            { title:"Nombre", data:"nombre" },
            {
                title:"Empresa", data:"listaExpositor", orderable: false,
                render: function(data, type, row, meta) {
                    if (data.length === 1)
                        return data[0].nombre;

                    let listaNombreExpositor = data.map(e => e.nombre);// lista de nombre de expositores del personal
                    let textoDespl = listaNombreExpositor.join("\n- ");

                    return `${data[0].nombre} 
					<span title="Expositores:\n- ${textoDespl}"><i class="bi bi-exclamation-circle"></i></span>`;
                }
            },

            { title:"Registro", data:"fechaRegistro" },
            {
                title:"Rut", data:"rut",
                render: (data, type, row) => {
                    if (!row.extranjero)
                        return row.rutCompleto;
                    if (data === null)
                        return `<span class="badge text-bg-danger">NO REGISTRADO</span>`;
                    return `${row.rutCompleto} <span class="badge text-bg-danger">EXT</span>`;
                }
            },
            {
                title:"Email", data:"listaExpositor", orderable: false,
                render: function(data, type, row, meta) {
                    if (data.length === 1)
                        return data[0].email;

                    let listaEmailExpositor = data.map(e => `${e.nombre} - ${e.email}`);// lista de email y nombre de expositores del personal
                    let textoDespl = listaEmailExpositor.join("\n- ");

                    return `${data[0].email} 
					<span title="Emails:\n- ${textoDespl}"><i class="bi bi-exclamation-circle"></i></span>`;
                }
            },
            //{ title:"Email", data:"email" },
            { title:"Código", data:"codigo" },
            {
                title:"Email enviado", data:"fechaEnvioCredenciales",
                render: (data, type, row) => {
                    if (data === null)
                        return `<span class="badge text-bg-secondary">NO</span>`;
                    return `<span class="badge text-bg-success">SI</span>`;

                }
            },
            {
                title:"Ticket impreso", data:"fechaImpresionTicket",
                render: (data, type, row) => {
                    if (data === null)
                        return `<span class="badge text-bg-secondary">NO</span>`;
                    return `<span class="badge text-bg-success">SI</span>`;
                }
            },
            {
                title:"", data:"codigo",
                render: (data, type, row) => {
                    let accion = `<div class="d-grid gap-2 d-md-flex justify-content-md-end">`
                    accion += `<a class="btn btn-primary btn-sm" href='${contextpath}personal/${data}/editar'>Editar</a>`
                    accion += `<a class="btn btn-primary btn-sm" href='${contextpath}personal/${data}/ver'>ver</a>`
                    if (imprimeTicketHabilitado == "true") {
                        if (row.fechaImpresionTicket === null) {
                            accion += `<a class="btn btn-info btn-sm" role="button" href="#" data-personal-codigo="${data}">`;
                            accion += `<svg class="bi" width="12" height="12" fill="currentColor"><use href="${contextpath}webjars/bootstrap-icons/1.10.5/bootstrap-icons.svg#printer"></use></svg>`;
                            accion += `<span data-personal-codigo="${data}">Imprimir</span>`;
                            accion += `</a>`;
                        }
                        else {
                            accion += `<a class="btn btn-warning btn-sm" role="button" href="#" data-personal-codigo="${data}">`;
                            accion += `<span data-personal-codigo="${data}">Reimprimir</span>`;
                            accion += `</a>`;
                        }
                    }
                    accion += `</div>`;
                    return accion;
                },
                createdCell: function(td, cellData, rowData, row, col) {
                    $(td).find(".btn-info").on("click", initPrint);
                    $(td).find(".btn-warning").on("click", initReprint);
                }
            }
        ],
    };
    cfgTabla["language"]= languageDatatable;
    tabla= $('#table_personal').DataTable(cfgTabla);
}


function initPrint(event) {
    let td = event.target;
    let codigo = td.getAttribute("data-personal-codigo");
    showConfirm("Imprimir ticket", `¿Desea imprimir el ticket para ${codigo}?`,
        function() {
            doPrint(codigo);
        });
}

function initReprint(event) {
    let td = event.target;
    let codigo = td.getAttribute("data-personal-codigo");
    showConfirm("Reimprimir ticket", `El ticket para el código ${codigo} ha sido impreso previamente.¿Está seguro de imprimir nuevamente el ticket?`,
        function() {
            doPrint(codigo);
        });
}

function doPrint(codigo) {
    cargando();
    $.get(`${contextpath}PersonalController/personal/${codigo}/printTicket`)
        .done(function (data) {
            showToast(`Se ha impreso el ticket para ${data.nombre}`);
            tabla.ajax.reload();// recarga la tabla
        })
        .fail(error => showError(error.responseText))
        .always(() => cargaFinalizada());
}


// 🔹 Carga de nacionalidades desde API
function cargarNacionalidades() {
    const selectNacionalidad = document.getElementById("lbl_nacionalidad");
    if (!selectNacionalidad) return;

    // Mientras carga
    selectNacionalidad.innerHTML = '<option value="" selected disabled>Cargando nacionalidades...</option>';

    fetch(API_PAISES)
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al obtener nacionalidades");
            }
            return response.json();
        })
        .then(data => {
            // Ordenar por nombre en español si existe, si no, nombre común
            data.sort((a, b) => {
                const nombreA = (a.translations?.spa?.common || a.name.common || "").toUpperCase();
                const nombreB = (b.translations?.spa?.common || b.name.common || "").toUpperCase();
                return nombreA.localeCompare(nombreB);
            });

            // Limpiar y agregar opciones
            selectNacionalidad.innerHTML = '<option value="" selected disabled>Seleccione nacionalidad</option>';

            data.forEach(pais => {
                const nombreES = (pais.translations && pais.translations.spa && pais.translations.spa.common)
                    ? pais.translations.spa.common
                    : pais.name.common;

                const option = document.createElement("option");
                option.value = nombreES;
                option.textContent = nombreES;
                selectNacionalidad.appendChild(option);
            });
        })
        .catch(error => {
            console.error(error);
            // Fallback básico si falla la API
            selectNacionalidad.innerHTML = `
				<option value="" selected disabled>No se pudieron cargar, seleccione manualmente</option>
				<option value="Argentina">Argentina</option>
				<option value="Peruana">Peruana</option>
				<option value="Boliviana">Boliviana</option>
				<option value="Colombiana">Colombiana</option>
				<option value="Venezolana">Venezolana</option>
				<option value="Otra">Otra</option>
			`;
        });
}

// 🔹 Mostrar / ocultar el bloque de nacionalidad según radio
function actualizarEstadoNacionalidad() {
    const opcion = $('input[name="options"]:checked').val();
    const grupo = $("#grupo_nacionalidad");
    if (!grupo.length) return;

    if (opcion === "extranjero") {
        grupo.show();

        // Cargar solo si aún no hay opciones reales
        const select = document.getElementById("lbl_nacionalidad");
        if (select && select.options.length <= 1) {
            cargarNacionalidades();
        }
    } else {
        grupo.hide();
        $("#lbl_nacionalidad").val("");
    }
}