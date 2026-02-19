const API_PAISES = "https://restcountries.com/v3.1/all?fields=name,translations";


let tabla;
$(document).ready(function () {
    initButtons();
    initModal();
    initTable();
    initSelect();

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
	// ✅ 👉 AGREGA ESTO AQUÍ
    $("#lbl_rut_nacional").on("input", function () {
        this.value = this.value
            .replace(/[^0-9]/g, "")   // solo números
            .slice(0, 8);            // máximo 8 dígitos
    });

});

function initSelect() {
    const select  = document.getElementById("lbl_empresa");
    if(select.tomselect) {
        select.tomselect.destroy();
    }
    $.get(`${contextpath}PersonalController/expositor/all`)
        .done(function (data) {
            const valoresBackend = new Set();
            const inputSecundario = document.getElementById("lbl_email");

            data.forEach(op => {
                const option = document.createElement("option");
                option.value = op.id + '_' + op.email;
                option.textContent = op.nombre;
                select.appendChild(option);
            });

            Array.from(select.options).forEach(opt => {
                if (opt.value !== "") {
                    valoresBackend.add(opt.value);
                }
            });

            new TomSelect("#lbl_empresa", {
                create: true,
                persist: false,
                allowEmptyOption: true,
                items: [],
                placeholder: "Seleccione una opción o escriba el nombre de la empresa...",

                render: {
                    no_results: function(data, escape) {
                        return '<div class="no-results">No se encontraron resultados</div>';
                    },
                    option_create: function(data, escape) {
                        if (this.lastQuery && this.currentResults && this.currentResults.items.length > 0) {
                            return false;
                        }
                        return '<div class="create">Añadir empresa nueva: "<strong>' + escape(data.input) + '</strong>"</div>';
                    }
                },
                create: function(input) {
                    if (this.lastQuery && this.currentResults && this.currentResults.items.length > 0) {
                        return false;
                    }
                    // Si no hay resultados, permitir crear
                    return {
                        value: input,
                        text: input
                    };
                },
                onChange: function(value) {

                    if (!value) {
                        // Si vuelve a "Seleccione..."
                        inputSecundario.disabled = true;
                        inputSecundario.value = "";
                        return;
                    }

                    const yaRegistrada = valoresBackend.has(value);

                    $("#lbl_email").removeClass("is-invalid");

                    if (!yaRegistrada) {
                        inputSecundario.disabled = false;
                        inputSecundario.value = "";
                    } else {
                        inputSecundario.disabled = true;
                        inputSecundario.value = value.split("_")[1];
                    }
                }
            });

        })
        .fail(error => showError(error.responseText))
}

function initButtons() {
    // Botón guardar del modal
    $("#modal_new_personal .btn-primary").click(function() {
        register();   // Solo dispara la función, sin deshabilitar aquí
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

    $("#modal_new_personal .is-invalid").removeClass("is-invalid");

    const opcion = $('input[name="options"]:checked').val();
    let rut;
    const extranjero = (opcion === "extranjero");

    // ✅ VALIDAR NOMBRE
    let nombre = $("#lbl_nombre").val()?.trim();
    if (!nombre) {
        $("#lbl_nombre").addClass("is-invalid");
        showError("El nombre es obligatorio.");
        habilitarBotonGuardar();
        return;
    }

    // ✅ VALIDAR SEGÚN TIPO DE DOCUMENTO
    if (!extranjero) {
        // NACIONAL
        rut = $("#lbl_rut_nacional").val()?.trim();

        if (!rut) {
            $("#lbl_rut_nacional").addClass("is-invalid");
            showError("Debe ingresar el RUT.");
            habilitarBotonGuardar();
            return;
        }

        if (!/^[0-9]{8}$/.test(rut)) {
            $("#lbl_rut_nacional").addClass("is-invalid");
            showError("El RUT debe tener exactamente 8 dígitos numéricos.");
            habilitarBotonGuardar();
            return;
        }

    } else {
        // EXTRANJERO
        rut = $("#lbl_pass_extranjero").val()?.trim();

        if (!rut) {
            $("#lbl_pass_extranjero").addClass("is-invalid");
            showError("Debe ingresar el pasaporte.");
            habilitarBotonGuardar();
            return;
        }

        // ✅ nacionalidad obligatoria solo extranjero
        if (!$("#lbl_nacionalidad").val()) {
            $("#lbl_nacionalidad").addClass("is-invalid");
            showError("Debe seleccionar la nacionalidad.");
            habilitarBotonGuardar();
            return;
        }

        // 🔹 Prefijo EXT automático (evita duplicar si ya viene)
        rut = rut.toUpperCase();
        if (!rut.startsWith("EXT")) {
            rut = "EXT" + rut;
        }
    }

    // ✅ VALIDAR EMPRESA
    let empresa = $("#lbl_empresa option:selected").text()?.trim();
    if (!empresa) {
        $("#lbl_empresa").addClass("is-invalid");
        showError("El campo empresa es obligatorio.");
        habilitarBotonGuardar();
        return;
    }

    let email = $("#lbl_email").val();
    if (!email) {
        $("#lbl_email").addClass("is-invalid");
        showError("El correo es obligatorio.");
        habilitarBotonGuardar();
        return;
    }

    // ✅ construir objeto (datos válidos)
    let newElem = {
        nombre: nombre,
        rut: rut,                                   // ← nacional o EXTxxxx
        empresa: empresa.toUpperCase(),
        email: email,
        extranjero: extranjero,
        nacionalidad: $("#lbl_nacionalidad").val(),
        observaciones: $("#lbl_observaciones").val()
    };

    // 🟦 1) VERIFICAR EN LA BBDD SI EL RUT/PASAPORTE YA EXISTE
    cargando();
    $.get(`${contextpath}PersonalController/personal/check/${encodeURIComponent(rut)}`)
        .done(function (existe) {
            cargaFinalizada();

            const yaExiste =
                existe === true ||
                existe === "true" ||
                existe === 1 ||
                existe === "1";

            if (yaExiste) {
                showError("El documento ingresado ya se encuentra registrado en el sistema.");
                habilitarBotonGuardar();
                return; // ❌ no se guarda
            }

            // 🟢 2) NO EXISTE → PEDIR CONFIRMACIÓN Y GUARDAR
            showConfirm(
                "¿Guardar registro?",
                "Confirma que los datos ingresados son correctos.",
                function () {
                    $("#modal_new_personal .btn-primary").attr("disabled", true);
                    cargando();

                    $.ajax({
                        url: `${contextpath}PersonalController/personal/register`,
                        method: "POST",
                        data: JSON.stringify(newElem),
                        contentType: "application/json; charset=utf-8",
                    })
                    .done(() => {
                        tabla.ajax.reload();
                        $("#modal_new_personal").modal("hide");
                        initSelect()
                        $("#lbl_rut_nacional_dv").text("N.A.");
                        showToast("Se ha registrado correctamente.");
                    })
                    .fail(error => {
                        showError(error.responseText);
                    })
                    .always(() => {
                        cargaFinalizada();
                        habilitarBotonGuardar();
                    });
                }
            );
        })
        .fail(function () {
            cargaFinalizada();
            showError("No se pudo verificar el documento. Intente nuevamente.");
            habilitarBotonGuardar();
        });
}




function habilitarBotonGuardar() {
    $("#modal_new_personal .btn-primary").attr("disabled", false);
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
        ordering: false,
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
            { title:"Nombre", data:"nombre", orderable: false },
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

            { title:"Registro", data:"fechaRegistro", orderable: false },
            {
                title:"Rut", data:"rut", orderable: false,
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
            { title:"Código", data:"codigo", orderable: false },
            {
                title:"Email enviado", data:"fechaEnvioCredenciales", orderable: false,
                render: (data, type, row) => {
                    if (data === null)
                        return `<span class="badge text-bg-secondary">NO</span>`;
                    return `<span class="badge text-bg-success">SI</span>`;

                }
            },
            {
                title:"Ticket impreso", data:"fechaImpresionTicket", orderable: false,
                render: (data, type, row) => {
                    if (data === null)
                        return `<span class="badge text-bg-secondary">NO</span>`;
                    return `<span class="badge text-bg-success">SI</span>`;
                }
            },
            {
                title:"", data:"codigo", orderable: false,
                render: (data, type, row) => {
                    let accion = `<div class="d-grid gap-2 d-md-flex justify-content-md-end">`
                    accion += `<a class="btn btn-primary btn-sm" href='${contextpath}personal/${data}/editar'>Editar</a>`
                    accion += `<a class="btn btn-primary btn-sm" href='${contextpath}personal/${data}/ver'>ver</a>`
                    if (imprimeTicketHabilitado == "true") {
                        if (row.fechaImpresionTicket === null) {
                            accion += `<a class="btn btn-info btn-sm" role="button" href="#" data-personal-codigo="${data}">`;
                            accion += `<use href="${contextpath}webjars/bootstrap-icons/1.10.5/bootstrap-icons.svg#printer"></use>`;
                            accion += `<span data-personal-codigo="${data}">Imprimir</span>`;
                            accion += `</a>`;
                        }
                        else {
                            accion += `<a class="btn btn-warning btn-sm" role="button" href="#" data-personal-codigo="${data}">`;
                            accion += `<span data-personal-codigo="${data}">Reimprimir</span>`;
                            accion += `</a>`;
                        }
                    }
					// 🔴 Nuevo botón Eliminar
					       accion += `<a class="btn btn-danger btn-sm btn-delete-personal" href="#" data-personal-codigo="${data}">Eliminar</a>`;

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
        // 👉 Si cambia a PASAPORTE, limpiar RUT y DV
        $("#lbl_rut_nacional").val("");
        $("#lbl_rut_nacional_dv").text("N.A.");

        $('#lbl_rut_nacional').prop('disabled', true);
        $('#lbl_pass_extranjero').prop('disabled', false);        

        grupo.show();

        // Cargar solo si aún no hay opciones reales
        const select = document.getElementById("lbl_nacionalidad");
        if (select && select.options.length <= 1) {
            cargarNacionalidades();
        }

    } else {

        $('#lbl_rut_nacional').prop('disabled', false);
        $('#lbl_pass_extranjero').prop('disabled', true);
        // 👉 Si cambia a RUT, limpiar PASAPORTE y nacionalidad
        $("#lbl_pass_extranjero").val("");
        $("#lbl_nacionalidad").val("");

        grupo.hide();
    }
}



// ✅ ERROR elegante
function showError(msg) {
    Swal.fire({
        icon: "error",
        title: "Error",
        text: msg,
        confirmButtonColor: "#d33"
    });
}

// ✅ ÉXITO estilo moderno
function showToast(msg) {
    Swal.fire({
        toast: true,
        position: "top-end",
        icon: "success",
        title: msg,
        showConfirmButton: false,
        timer: 2000,
        timerProgressBar: true
    });
}

// ✅ Confirmación reutilizable
function showConfirm(title, text, onConfirm) {
    Swal.fire({
        title: title,
        text: text,
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
