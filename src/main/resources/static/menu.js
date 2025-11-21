$(function() {
	$('#btnSalir').click(function() {
        const segments = window.location.pathname.split('/');
        const context = (segments.length > 1 && segments[1] !== '') ? '/' + segments[1] : '';
        window.location = context + '/logout';
	});
});
