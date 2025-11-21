package com.sicep.exponor2023.acreditacion_montaje.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contiene los filtros y ordenamientos provenientes de una tabla datatables en modalidad ajax.
 * NOTA: Desde la vista es permitido no asignar todos los parametros de busqueda en el json, pero
 * no se pueden asignar parametros que no se encuentre en el DTO.
 *
 */
public class AjaxDatatableDTO {
    protected int draw;
    protected List<Map> columns;
    protected List<Map> order;
    protected int start= 0;
    protected int length= -1;
    protected Map search;

    public AjaxDatatableDTO() {
    }
    protected AjaxDatatableDTO(AjaxDatatableDTO ajaxDatatableDTO) {
        this.draw= ajaxDatatableDTO.draw;
        this.columns= ajaxDatatableDTO.columns;
        this.order= ajaxDatatableDTO.order;
        this.start= ajaxDatatableDTO.start;
        this.length= ajaxDatatableDTO.length;
        this.search= ajaxDatatableDTO.search;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public List<Map> getColumns() {
        return columns;
    }

    public void setColumns(List<Map> columns) {
        this.columns = columns;
    }

    public List<Map> getOrder() {
        return order;
    }

    public void setOrder(List<Map> order) {
        this.order = order;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Map getSearch() {
        return search;
    }

    public void setSearch(Map search) {
        this.search = search;
    }

    public boolean hasSearchText() {
        if (search== null)
            return false;
        return !(search.get("value").equals(""));
    }
    public String getSearchText() {
        if (search== null)
            return null;
        return (String)search.get("value");
    }

    public void setSearchText(String searchText) {
        if (searchText== null)
            return;
        searchText= searchText.trim();
        if (searchText.equals(""))
            return;

        if (this.search== null)
            this.search= new HashMap();
        this.search.put("value", searchText);
    }

    public boolean hasOrderParam() {
        if (this.order== null || this.order.isEmpty())
            return false;

        int indiceColumna= (Integer)this.order.get(0).get("column");
        if (indiceColumna== -1)
            return false;


        return (this.columns.get(indiceColumna).get("data")!= null);
    }

    public String getOrderParam() {
        if (order== null || order.isEmpty())
            return null;
        // obtencion del primer campo de ordenamiento (soporta multiples pero se requiere mayor estudio)
        int indiceColumna= (Integer)order.get(0).get("column");

        String columna= (String)columns.get(indiceColumna).get("data");
        return columna;
    }

    public String getOrderDir() {
        if (order== null || order.isEmpty())
            return null;
        // obtencion del primer campo de ordenamiento (soporta multiples pero se requiere mayor estudio)
        String dir=  (String)order.get(0).get("dir");
        return dir;
    }

    public boolean isDesc() {
        if (order== null || order.isEmpty())
        	return true;
        String dir=  (String)order.get(0).get("dir");
        return dir.equals("desc");
    }
    
    public int getPageNumber() {
    	if (length == 0 || length == -1)
    		return 0;
    	return start / length;
    }
    
}
