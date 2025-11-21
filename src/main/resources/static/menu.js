$(function() {
	$('#btnSalir').click(function() {
        // usar host para que segments[0] pueda contener 'localhost'
        const segments = window.location.host.split(':');

        // comprobar si segments[0] contiene 'localhost'
        const isLocalhost = segments[0].indexOf('localhost') !== -1;

        console.log(segments)
        console.log(isLocalhost)

        if (isLocalhost) {
            // redirigir a la ruta de logout (comportamiento en la línea 6)
            window.location = "/logout";
        } else {
            // redirigir a otra URL (ajusta según necesites)
            window.location = 'https://www.aiaeventos.cl/acreditacion_montaje/logout';
        }
	});
});
