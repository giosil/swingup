package org.dew.swingup.editors;

/**
 * Implementazione vuota di IEntityEditorListener.
 *
 * @author <a href="mailto:giorgio.silvestris@gmail.com">Giorgio Silvestris</a>
 * @version 1.0
 */
public
class EntityEditorAdapter implements IEntityEditorListener
{
  protected AEntityEditor oEntityEditor;
  
  public
  EntityEditorAdapter()
  {
  }
  
  public
  EntityEditorAdapter(AEntityEditor oEntityEditor)
  {
    this.oEntityEditor = oEntityEditor;
  }
  
  public
  void onClearSelection()
  {
  }
  
  public
  void onFind()
  {
  }
  
  public
  void onSelection()
  {
  }
  
  public
  void onChoice()
  {
  }
  
  public
  void onExit()
  {
  }
  
  public
  void onNew()
  {
  }
  
  public
  void onModify()
  {
  }
  
  public
  void onSave(boolean boNew)
  {
  }
  
  public
  void onCancel(boolean boNew)
  {
  }
  
  public
  void onDelete()
  {
  }
}
