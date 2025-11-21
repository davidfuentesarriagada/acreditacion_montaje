function initPrintTicket() {
	showConfirm('Volver a imprimir el ticket', '¿Desea volver a imprimir el Ticket?', printTicket);
}

function printTicket() {
	printJS(`/personal/ticket/${codigo}.png`, 'image');
}
