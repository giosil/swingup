package org.dew.swingup.demo;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import org.dew.swingup.*;
import org.dew.swingup.editors.*;
import org.dew.swingup.fm.FMUtils;
import org.dew.swingup.util.*;
import org.dew.util.WMap;
import org.dew.swingup.rpc.*;

@SuppressWarnings({"rawtypes","unchecked"})
public
class DemoEntityEditor extends AEntityEditor
{
	private static final long serialVersionUID = 3000030230930141866L;
	
	protected ATableModelForSorter oTableModel;
	protected JTable oTable;
	protected List oRecords = new ArrayList();
	protected Map oLastRecordReaded;
	
	public
	DemoEntityEditor()
	{
		super();
	}
	
	public
	Object getCurrentSelection()
		throws Exception
	{
		return oLastRecordReaded;
	}
	
	protected
	Container buildGUIFilter()
	{
		FormPanel fp = new FormPanel("Ricerca");
		fp.addTab("Filtro");
		fp.addRow();
		fp.addTextField("d", "Directory",  100);
		fp.addTextField("f", "Filter",     100);
		fp.setCase("d", 0);
		fp.setCase("f", 0);
		fp.build();
		return fp;
	}
	
	protected
	Container buildGUIDetail()
	{
		FormPanel fp = new FormPanel("Dettaglio");
		fp.addTab("Attributi");
		fp.addRow();
		fp.addTextField("p",           "Path",  255);
		fp.addTextField("n",           "Name",  100);
		fp.addRow();
		fp.addDateField("d",           "Last Mod.");
		fp.addTextNumericField("l",    "Length");
		fp.setCase("p", 0);
		fp.setCase("n", 0);
		fp.build();
		
		List<String> mandatoryFields = new ArrayList<String>();
		mandatoryFields.add("p");
		mandatoryFields.add("n");
		fp.setMandatoryFields(mandatoryFields);
		
		JLabel jlId = fp.getJLabel("d");
		if(jlId != null) jlId.setForeground(Color.red);
		
		return fp;
	}
	
	protected
	Container buildGUIBigDetail()
	{
		return null;
	}
	
	protected
	Container buildGUIOtherDetail()
	{
		return null;
	}
	
	protected
	Container buildGUIResult()
	{
		String[] asCOLUMNS   = {"Type", "Path", "Name", "Last Mod.", "Length"};
		String[] asSYMBOLICS = {"t",    "p",    "n",    "d",         "l"};
		
		oTableModel = new SimpleTableModelForSorter(oRecords, asCOLUMNS, asSYMBOLICS);
		
		oTable = new JTable(oTableModel);
		oTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		oTable.setColumnSelectionAllowed(false);
		oTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		oTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			public
			Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int iRow, int iCol)
			{
				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, iRow, iCol);
				
				Map oRecord = (Map) oRecords.get(iRow);
				
				String sType = (String) oRecord.get("t");
				if(sType != null && sType.equals("f")) {
					this.setForeground(table.getForeground());
					this.setFont(new Font(getFont().getName(), Font.PLAIN, getFont().getSize()));
				}
				else {
					this.setForeground(Color.gray);
					this.setFont(new Font(getFont().getName(), Font.ITALIC, getFont().getSize()));
				}
				return this;
			}
		});
		TableUtils.setMonospacedFont(oTable);
		
		oTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() >= 2 && !e.isControlDown()) {
					try {
						fireSelect();
					}
					catch(Exception ex) {
						GUIMessage.showException(ex);
					}
				}
			}
		});
		
		JScrollPane oScrollPane = new JScrollPane(oTable);
		
		TableColumnResizer.setResizeColumnsListeners(oTable);
		TableSorter.setSorterListener(oTable);
		
		oTable.getSelectionModel().addListSelectionListener(this);
		
		return oScrollPane;
	}
	
	protected
	void onChoiceMade()
	{
		setChoice(oLastRecordReaded);
	}
	
	protected
	void setFilterValues(Object oValues)
		throws Exception
	{
		if(oValues instanceof Map) {
			Map mapValues = (Map) oValues;
			FormPanel fpFilter = (FormPanel) getFilterContainer();
			fpFilter.setValues(mapValues);
		}
	}
	
	protected
	void doFind()
		throws Exception
	{
		FormPanel fpFilter = (FormPanel) getFilterContainer();
		
		WMap wmFilter = new WMap(fpFilter.getValues());
		
		String sDirectory = wmFilter.getString("d", null);
		if(sDirectory == null || sDirectory.length() < 3) {
			sDirectory = "user.home";
		}
		String sFilter = wmFilter.getString("f", "");
		
		IRPCClient oRPCClient = ResourcesMgr.getDefaultRPCClient();
		Vector vParameters = new Vector();
		vParameters.add(FMUtils.encrypt(sDirectory));
		vParameters.add(FMUtils.encrypt(sFilter));
		
		oRecords = (List) oRPCClient.execute("FM.ls", vParameters, true);
		
		oTableModel.setData(oRecords);
		TableSorter.resetHeader(oTable);
		
		FormPanel fpDetail = (FormPanel) getDetailContainer();
		fpDetail.reset();
		
		if(oRecords.size() == 1) {
			ResourcesMgr.getStatusBar().setText("1 item found.");
		}
		else {
			ResourcesMgr.getStatusBar().setText(oRecords.size() + " items found.");
		}
	}
	
	protected
	void doReset()
		throws Exception
	{
		FormPanel fpFilter = (FormPanel) getFilterContainer();
		fpFilter.reset();
		
		FormPanel fpDetail = (FormPanel) getDetailContainer();
		fpDetail.reset();
		
		oRecords = new ArrayList();
		oTableModel.setData(oRecords);
		TableSorter.resetHeader(oTable);
	}
	
	protected
	boolean onSelection()
		throws Exception
	{
		int iRow = oTable.getSelectedRow();
		if(iRow < 0 || iRow >= oRecords.size()) {
			return false;
		}
		
		Map mapRecord = (Map) oRecords.get(iRow);
		
		FormPanel fpDetail = (FormPanel) getDetailContainer();
		fpDetail.reset();
		fpDetail.setValues(mapRecord);
		fpDetail.selectFirstTab();
		
		return true;
	}
	
	protected
	void doNew()
		throws Exception
	{
		FormPanel fpDetail = (FormPanel) getDetailContainer();
		fpDetail.reset();
		fpDetail.requestFocus();
		
		oTable.clearSelection();
		oTable.setEnabled(false);
	}
	
	protected
	void doOpen()
		throws Exception
	{
		oTable.setEnabled(false);
		
		FormPanel fpDetail = (FormPanel) getDetailContainer();
		fpDetail.requestFocus();
	}
	
	protected
	boolean doSave(boolean boNew)
		throws Exception
	{
		FormPanel fpDetail = (FormPanel) getDetailContainer();
		
		String sCheckMandatory = fpDetail.getStringCheckMandatories();
		if(sCheckMandatory.length() > 0) {
			GUIMessage.showWarning("Occorre valorizzare i seguenti campi:\n" + sCheckMandatory);
			return false;
		}
		
		int iRowToSelect = 0;
		
		if(boNew) {
			GUIMessage.showInformation("Funzione simulata");
			
			oRecords.add(fpDetail.getValues());
			iRowToSelect = oRecords.size() - 1;
		}
		else {
			GUIMessage.showInformation("Funzione simulata");
			
			int iRow = oTable.getSelectedRow();
			oRecords.set(iRow, fpDetail.getValues());
			iRowToSelect = iRow;
		}
		
		oTable.setEnabled(true);
		TableSorter.resetHeader(oTable);
		oTableModel.notifyUpdates();
		oTable.setRowSelectionInterval(iRowToSelect, iRowToSelect);
		
		return true;
	}
	
	protected
	void doCancel()
		throws Exception
	{
		FormPanel fpDetail = (FormPanel) getDetailContainer();
		int iRow = oTable.getSelectedRow();
		fpDetail.reset();
		if(iRow >= 0) fpDetail.setValues(oLastRecordReaded);
		oTable.setEnabled(true);
	}
	
	protected
	void doDelete()
		throws Exception
	{
		GUIMessage.showWarning("Funzione non disponibile");
	}
	
	protected
	void checkActions(List listDefActions, boolean boAllowEditing)
	{
	}
	
	protected
	void onChangeEditorStatus(int iStatus)
	{
		System.out.println("DemoEntityEditor.onChangeEditorStatus(" + iStatus + ")");
	}
	
	protected
	void doPrint()
		throws Exception
	{
		GUIMessage.showWarning("Funzione non disponibile");
	}
	
	protected
	boolean isElementEnabled()
	{
		return true;
	}
	
	protected
	void doToggle()
		throws Exception
	{
	}
}
