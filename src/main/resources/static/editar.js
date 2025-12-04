$(function() {
	$("#btn_guardar").on("click", guardar);
	
	$("#lbl_rut_nacional").on("keyup", function(e) {
		calculoDv();
	});
	
	$("#lbl_rut_nacional").on("change", function(e) {
		$(this).trigger("keyup");
	});
	// 🔹 NUEVO: cuando cambie entre Nacional / Extranjero en EDITAR
	 $('input[name="options"]').on("change", function () {
	     actualizarEstadoDocumentoEditar();
	 });

	 // 🔹 Ejecutar una vez al cargar, para dejar todo coherente
	 actualizarEstadoDocumentoEditar();
});

$(function() {
    $("#btn_guardar").on("click", guardar);
    
    $("#lbl_rut_nacional").on("keyup", function(e) {
        calculoDv();
    });
    
    $("#lbl_rut_nacional").on("change", function(e) {
        $(this).trigger("keyup");
    });

    // 🔹 NUEVO: cuando cambie entre Nacional / Extranjero en EDITAR
    $('input[name="options"]').on("change", function () {
        actualizarEstadoDocumentoEditar();
    });

    // 🔹 Ejecutar una vez al cargar, para dejar todo coherente
    actualizarEstadoDocumentoEditar();
});

function guardar() {
    let rut;
    let extranjero = true;
    const opcion = $('input[name="options"]:checked').val();

    if (opcion === "nacional") {
        extranjero = false;
        rut = $("#lbl_rut_nacional").val()?.trim();

        // (Opcional) pequeña validación de RUT
        if (!rut) {
            showError("Debe ingresar el RUT.");
            return;
        }

    } else {
        // 🔹 Pasaporte
        rut = $("#lbl_pass_extranjero").val()?.trim();

        if (!rut) {
            showError("Debe ingresar el pasaporte.");
            return;
        }

        // 🔹 MISMA LÓGICA QUE EN REGISTRO:
        //     - normalizamos
        //     - agregamos EXT solo si no está
        rut = rut.toUpperCase();
        if (!rut.startsWith("EXT")) {
            rut = "EXT" + rut;     // quedará EXT1234567
        }
    }

    let elem = {
        nombre : $("#lbl_nombre").val(),
        rut : rut,                       // ← nacional o EXTxxxx
        extranjero: extranjero,
        observaciones: $("#lbl_observaciones").val()
    };

    cargando();
    $.ajax({
        url: `${contextpath}PersonalController/personal/${codigo}/edit`,
        method: "POST",
        data: JSON.stringify(elem),
        contentType: "application/json; charset=utf-8",
    })
    .done(function() {
        showToast("Se ha realizado la edición");
        location.href = `${contextpath}listaPersonal`;
    })
    .fail(function(error) {
        showError(error.responseText);
    })
    .always(() => cargaFinalizada());
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

// 🔹 NUEVO: misma lógica que en registro, pero para EDITAR
function actualizarEstadoDocumentoEditar() {
    const opcion = $('input[name="options"]:checked').val();

    if (opcion === "extranjero") {
        // Si selecciona Extranjero → limpiar RUT y DV
        $("#lbl_rut_nacional").val("");
        $("#lbl_rut_nacional_dv").text("N.A.");
    } else {
        // Si selecciona Nacional → limpiar Pasaporte
        $("#lbl_pass_extranjero").val("");
    }
}




