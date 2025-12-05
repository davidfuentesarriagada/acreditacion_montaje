let tabla;

$(document).ready(function () {
    initTable();
    initSearchFilter();
    initSelect();
    initModal();
    initButton();
    initTogglePassword();
});

/* =========================
 *   TABLA
 * ========================= */

function initTable() {
    var cfgTabla = {
        dom: '<"top"i>rt<"bottom"lp><"clear">',
        ordering: true,
        paging: true,
        searching: true, // necesario para filtrado
        data: listaUsuario,
        columns: [
            { title: "Nombre", data: "nombre" },
            { title: "E-mail", data: "email" },
            {
                title: "Perfil", data: "role",
                render: function (data) {
                    if (data === "ROLE_ADMIN")
                        return "Administrador";
                    return "Usuario";
                }
            },
            {
                title: "Habilitado", data: "enabled",
                render: function (data, type, row) {
                    let checked = data ? " checked" : "";
                    return `<div class="form-check form-switch">
                                <input class="form-check-input clickleable"
                                       data-usuario-id="${row.id}"
                                       type="checkbox"${checked}>
                            </div>`;
                },
                createdCell: function (td, cellData, rowData) {
                    var btn = $(td).find("input");
                    btn.change(changeEnabled);
                }
            },
            {
                title: "Editar", data: "id",
                render: function (data) {
                    return `<a class="btn btn-primary btn-sm" href='${contextpath}usuario/${data}'>ver</a>`;
                },
            },
        ],
    };
    cfgTabla["language"] = languageDatatable;
    tabla = $('#table_usuario').DataTable(cfgTabla);
}

/* =========================
 *   FILTROS / BUSCADOR
 * ========================= */

function initSearchFilter() {
    $("#lbl_searchtext").on("keyup", function () {
        tabla.draw();
    });
    $("#chk_filtro_enabled,#lbl_searchtext").on("change", function () {
        tabla.draw();
    });

    // permite agregar a la búsqueda el switch de habilitado
    $.fn.dataTable.ext.search.push(
        function (settings, searchData, index, rowData) {
            // filtro de habilitado
            let checked = $("#chk_filtro_enabled").prop('checked'); // switch filtro
            let usuarioHabilitado = rowData.enabled;                // valor del usuario
            if (!checked && !usuarioHabilitado)
                return false;

            // filtro de búsqueda de texto
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

/* =========================
 *   LLENAR TABLA DESDE BACK
 * ========================= */

function fillTable() {
    tabla.clear();
    $.get(`${contextpath}UsuarioController/usuario/list`)
        .done(function (data) {
            tabla.rows.add(data).draw();
        });
}

/* =========================
 *   SELECT2 PERFIL
 * ========================= */

function initSelect() {
    $("#sel_role").select2({
        dropdownParent: $('#modal_new_usuario')
    });
}

/* =========================
 *   MODAL NUEVO USUARIO
 * ========================= */

function clearUserForm() {
    const $form = $('#form_new_usuario');
    $form[0].reset();
    $form.find(".is-invalid").removeClass("is-invalid");

    // reset select2
    $("#sel_role").val("-1").trigger("change");
}

function initModal() {
    // al abrir: limpiar formulario y habilitar botón
    $('#modal_new_usuario').on('show.bs.modal', function () {
        clearUserForm();
        $("#btn_save_usuario").prop("disabled", false);
    });

    // focus en el primer input
    $('#modal_new_usuario').on('shown.bs.modal', function () {
        $('#lbl_nombre').trigger("focus");
    });
}

/* =========================
 *   BOTÓN GUARDAR + VALIDACIÓN
 * ========================= */

function initButton() {
    $("#btn_save_usuario").click(function () {
        validarYRegistrar();
    });
}

function validarYRegistrar() {
    // limpiar estilos invalid
    $("#form_new_usuario .is-invalid").removeClass("is-invalid");

    const nombre = $("#lbl_nombre").val().trim();
    const email = $("#lbl_email").val().trim();
    const password = $("#lbl_password").val();
    const passwordRe = $("#lbl_password_re").val();
    const role = $("#sel_role").val();

    let errores = [];

    // Nombre
    if (!nombre) {
        $("#lbl_nombre").addClass("is-invalid");
        errores.push("El nombre es obligatorio.");
    }

    // Email
    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email || !regexEmail.test(email)) {
        $("#lbl_email").addClass("is-invalid");
        errores.push("Debe ingresar un email válido.");
    }

    // Password
    if (!password || password.length < 6) {
        $("#lbl_password").addClass("is-invalid");
        errores.push("La contraseña debe tener al menos 6 caracteres.");
    }

    // Repetir password
    if (password !== passwordRe) {
        $("#lbl_password_re").addClass("is-invalid");
        errores.push("Las contraseñas no coinciden.");
    }

    // Perfil
    if (!role || role === "-1") {
        $("#sel_role").addClass("is-invalid");
        errores.push("Debe seleccionar un perfil.");
    }

    if (errores.length > 0) {
        // SweetAlert2 con lista de errores
        Swal.fire({
            icon: "error",
            title: "Datos incompletos",
            html: `<ul style="text-align:left;margin:0;padding-left:1.2rem;">
                       ${errores.map(e => `<li>${e}</li>`).join("")}
                   </ul>`,
            confirmButtonColor: "#d33"
        });
        return;
    }

    // Si todo ok, registrar
    register();
}

/* =========================
 *   REGISTRO AJAX
 * ========================= */

function register() {
    $("#btn_save_usuario").prop("disabled", true);

    let newUser = {
        email: $("#lbl_email").val().trim(),
        password: $("#lbl_password").val(),
        nombre: $("#lbl_nombre").val().trim(),
        role: $("#sel_role").val()
    };

    cargando();
    $.ajax({
        url: `${contextpath}UsuarioController/usuario/register`,
        method: "POST",
        data: JSON.stringify(newUser),
        contentType: "application/json; charset=utf-8",
    })
        .done(function () {
            $("#modal_new_usuario").modal("hide");
            showToast("Usuario registrado correctamente");
            fillTable();
        })
        .fail(function (error) {
            showError(error.responseText || "No se pudo registrar el usuario.");
            $("#btn_save_usuario").prop("disabled", false);
        })
        .always(() => cargaFinalizada());
}

/* =========================
 *   HABILITAR / DESHABILITAR
 * ========================= */

function changeEnabled(event) {
    let td = event.target;
    let idUsuario = td.getAttribute("data-usuario-id");
    let checked = td.checked;
    cargando();
    $.post(`${contextpath}UsuarioController/usuario/${idUsuario}/changeEnabled/${checked}`)
        .done(() => showToast(`Se ha ${checked ? "habilitado" : "deshabilitado"} el usuario`))
        .fail(error => showError(error.responseText || "Error al cambiar el estado del usuario."))
        .always(() => cargaFinalizada());
}

/* =========================
 *   TOGGLE VER CONTRASEÑA
 * ========================= */

function initTogglePassword() {
    const $pass = $("#lbl_password");
    const $btnToggle = $("#btn_toggle_password");

    if ($pass.length === 0 || $btnToggle.length === 0) {
        return;
    }

    $btnToggle.on("click", function (e) {
        e.preventDefault();

        const tipoActual = $pass.attr("type");
        const nuevoTipo = (tipoActual === "password") ? "text" : "password";
        $pass.attr("type", nuevoTipo);

        // Cambiar ícono ojo / ojo tachado
        const $icon = $(this).find("i");
        if ($icon.length) {
            $icon.toggleClass("bi-eye bi-eye-slash");
        }
    });
}


