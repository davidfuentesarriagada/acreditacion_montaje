$(document).ready(function () {
	initButtons();
});

function initButtons() {
	$("#btn_export").on('click', function() {
		window.open(`${contextpath}PersonalController/personal/asistencia/exportar`, '_self');
	});
}

