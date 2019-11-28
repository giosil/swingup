/* Copyright (c) 2002 by ISED S.p.A. - All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of ISED S.p.A. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you
 * entered into with ISED S.p.A.
 *
 * ISED S.p.A. MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT.
 * ISED S.p.A. SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */

package org.dew.swingup.util;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;

/**
 * Utilita' per addattare la larghezza delle colonne di una JTable.
 *
 * @since jdk1.3
 * @author <a href="mailto:madonna@ised.it">Giuseppe Madonna</a>
 * @version 0.00.001, 00-Month-2002
 */
public
class TableColumnResizer
{
  /**
   * Imposta i listeners necessari per calcolare la dimensione delle colonne
   * in base alla dimensione dei dati contenuti nella colonna;
   * nel caso avanzasse spazio, l'ultima colonna a destra
   * verra' espansa fino alla limite estremo della tabella.
   * <b>ATTENZIONE:</b> dato che il metodo cerca di aggiungere un ComponentListener
   * al componente parent della JTable, si presuppone che la JTable passata come parametro sia stata gia'
   * aggiunta ad un componente (ScrollPane, JPanel o quant'altro).
   *
   * @param oTable JTable su cui aggiungere i listeners necessari per il calcolo delle dimensioni delle colonne.
   */
  public static
  void setResizeColumnsListeners(final JTable oTable)
  {
    oTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);            // disabilita la modalita' di auto resize per la tabella
    JComponent oParent =                                         // recupera il componente superiore
        (JComponent)oTable.getParent();

    oParent.addComponentListener(new ComponentAdapter(){
      /**
       * Invoked when the component's size changes.
       */
      public void componentResized(ComponentEvent e) {
        normalizeColumnsSize(oTable);
      }
    });

    oTable.addAncestorListener(new AncestorListener(){
      /**
       * Called when the source or one of its ancestors is made visible
       * either by setVisible(true) being called or by its being
       * added to the component hierarchy.  The method is only called
       * if the source has actually become visible.  For this to be true
       * all its parents must be visible and it must be in a hierarchy
       * rooted at a Window
       */
      public void ancestorAdded(AncestorEvent event) {
        normalizeColumnsSize(oTable);
      }
      /**Empty implementation.*/
      public void ancestorRemoved(AncestorEvent event) {}

      /**Empty implementation.*/
      public void ancestorMoved(AncestorEvent event) {}
    });

    oTable.getModel().addTableModelListener(new TableModelListener(){
      /**
       * This fine grain notification tells listeners the exact range
       * of cells, rows, or columns that changed.
       */
      public
      void tableChanged(TableModelEvent e)
      {
        normalizeColumnsSize(oTable);
      }
    });
    
    JTableHeader oHeader =                                       // recupera l'header della tabella
        oTable.getTableHeader();
    oHeader.addMouseListener(new MouseAdapter(){
      /**
       * Invoked when the mouse has been clicked on a component.
       */
      public void mouseClicked(MouseEvent e) {
        normalizeColumnsSize(oTable);
      }
    });
  }

  /** Calcola la dimensione delle colonne della tabella in base ai dati contenuti nella colonna stessa fino
   * ad una dimensione massima pari ad un quarto della dimensione orizzontale dello schermo.
   * @param oTable JTable su cui operare il calcolo delle dimensioni delle colonne.
   */
  private static
  void normalizeColumnsSize(final JTable oTable)
  {
    SwingUtilities.invokeLater(new Runnable(){
      public void run() {
        Dimension oScreenDim =                                                  // recupera le dimensioni dello schermo
            Toolkit.getDefaultToolkit().getScreenSize();
        double dMaxColumnSize = oScreenDim.getWidth() / 4;                      // setta il limite massimo di larghezza delle colonne
        TableColumnModel oTCM =                                                 // recupera il modello delle colonne
            oTable.getColumnModel();
        TableModel oModel =
            oTable.getModel();
        int iColumnCount =                                                      // recupera il numero di colonne della tabella
            oModel.getColumnCount();
        int iFixedSpacer = 20;                                                  // fissa lo spazio minimo per la colonna
        Font oFont = oTable.getFont();                                          // recupera il font in uso
        FontMetrics oFontMetrics =                                              // recupera la fontmetric per il font in uso
            oTable.getFontMetrics(oFont);
        double dFilledSpace = 0;                                                // presetta variabile di spazio riempito
        for(int c=0; c<iColumnCount; c++){                                      // per ogni colonna
          TableColumn oTC =                                                   // prendi la colonna iesima
              oTCM.getColumn(c);
          TableCellRenderer oHeaderRenderer = oTC.getHeaderRenderer();        // recupera l'header della colonna
          double dLabelWidth = 0;                                             // imposta a 0 la larghezza della colonna
          if(oHeaderRenderer instanceof JLabel){                              // l'header e' una JLabel
            dLabelWidth = 16;                                               // imposta la larghezza della colonna con la larghezza dell'header...
          }
          
          String sColumName =                                                 // recupera il nome della colonna
              oModel.getColumnName(c);
          Rectangle2D oLabelBounds =                                          // recupera i limiti entro cui e' scritto
              oFontMetrics.getStringBounds(sColumName,oTable.getGraphics());
          dLabelWidth +=                                                      // recupera la larghezza dell'etichetta
              oLabelBounds.getWidth();
          
          if(c == iColumnCount -1){                                           // ultima colonna?
            Dimension oDim =                                                // recupera le dimensioni di questa jtable
                oTable.getParent().getSize();
            double dSpaceToFill =                                           // calcola lo spazio da riempire
                oDim.getWidth() - dFilledSpace;
            if(dSpaceToFill >= iFixedSpacer + dLabelWidth){                 // spazio rimasto sufficiente?
              oTC.setPreferredWidth(new Double(dSpaceToFill).intValue()); // espandila fino alla fine del pannello
              return;                                                     // finisci di computare
            }
          }
          else{                                                               // colonna interna
            double dMaxLength = 0;                                          // presetta la lunghezza massima a 0
            if(dLabelWidth > dMaxLength){                                   // il limite supera quello finora raggiunto?
              dMaxLength = dLabelWidth;                                   // se si, imposta il nuovo limite
            }
            int iRows = oTable.getRowCount();                               // recupera il numero di righe
            for(int r=0; r<iRows; r++){                                     // per ogni riga
              Object oValue =                                             // recupera il valore della cella
                  oTable.getValueAt(r,c);
              if(oValue != null){                                         // valore valido?
                String sValue = oValue.toString();                      // trasformalo in stringa
                Rectangle2D oValueBound =                               // recupera i limiti entro cui e' scritto
                    oFontMetrics.getStringBounds(sValue,oTable.getGraphics());
                if(oValueBound.getWidth() > dMaxLength){                // il limite supera quello finora raggiunto?
                  dMaxLength = oValueBound.getWidth();                // se si, imposta il nuovo limite
                  if(dMaxLength > dMaxColumnSize){                    // limite raggiunto per la colonna
                    dMaxLength = dMaxColumnSize;                    // imposta al limite massimo
                    break;                                          // interrompi scansione colonna
                  }
                }
              }
            }
            int iPreferredWidth =                                           // calcola la dimensione
                new Double(dMaxLength).intValue() + iFixedSpacer;
            oTC.setPreferredWidth(iPreferredWidth);                         // setta la dimensione per la colonna corrente
            dFilledSpace += iPreferredWidth;                                // aggiorna lo spazio occupato
          }
        }
      }
    });
  }
} // end TableColumnResizer
