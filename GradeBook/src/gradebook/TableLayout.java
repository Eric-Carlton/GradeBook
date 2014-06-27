package gradebook;
import java.awt.*;
import java.util.*;

public class TableLayout implements LayoutManager {
	/*	Copyright(c) 2010 Dr. James Frederick Wirth
	This class TableLayout can be used and derived without charge
	PROVIDED that this copyright notice remains intact.
 */
	///////////////////
	ArrayList<ArrayList<Cell>> cells = new ArrayList<ArrayList<Cell>>();
	ArrayList<Cell> currentRow = new ArrayList<Cell>();
	int nextColumn=0;
	byte[] columnOcc = new byte[200];	// should be more than enough
	int hGap=4, vGap=4;
	int nRow, nColumn;
	int[] yRow, xColumn;	// Preferred locations.
	boolean[] fixedRow, fixedColumn;	// are heights , widths fixed during stretching?
	///////////////////
	static final int LEFT	= 1;
	static final int RIGHT	= 2;
	static final int TOP	= 4;
	static final int BOTTOM	= 8;
	static final int XFIXED	= 16;
	static final int YFIXED	= 32;
	static final int ENDROW	= 64;
	static public final int WIDE = LEFT | RIGHT;
	static public final int HIGH = TOP | BOTTOM;
	///////////////////
	public void addLayoutComponent(String nm,Component cmp) {
		Cell c = new  Cell(cmp);
		c.setAlign(nm);
		while(columnOcc[nextColumn]!=0) {
			--columnOcc[nextColumn++];
			currentRow.add(null);			
		}
//System.err.print("addLayout at row="+cells.size()+" col="+nextColumn);
//for(int j=0;j<5;++j) System.err.print("  "+columnOcc[j]);
//System.err.println(" spn="+c.rowSpan+"x"+c.columnSpan);
		// position is available for c
		int rwspm1 = c.rowSpan - 1;
		currentRow.add(c);
		columnOcc[nextColumn++] = (byte) rwspm1;
		for(int j=1;j<c.columnSpan;++j) {
			currentRow.add(null);
			columnOcc[nextColumn++] = (byte) rwspm1;
		}
		if((c.align&ENDROW)!=0) endRow();
	}
	public void endRow() {
		while(columnOcc[nextColumn]!=0) {
			--columnOcc[nextColumn++];
			currentRow.add(null);			
		}
		cells.add(currentRow);
		nColumn = Math.max(nColumn,nextColumn);
		nextColumn = 0;
		currentRow = new ArrayList<Cell>();
	}
	public void removeLayoutComponent(Component cmp) { }
	public Dimension minimumLayoutSize(Container target) {
		return preferredLayoutSize(target);
	}
	public void dump() {
		System.err.println("nRow="+nRow+" nColumn="+nColumn);
		for(int r=0;r<nRow;++r)
			System.err.println("yRow["+r+"]="+yRow[r]);
		for(int c=0;c<nColumn;++c)
			System.err.println("xColumn["+c+"]="+xColumn[c]);
	}
	public void initializeLayout(Container target) {
//System.err.println("initialize layout container@"+target.hashCode());		
		endRow();
		currentRow = null;	// but ignore currentRow		
		nRow = cells.size();
//System.err.println("nRow="+nRow+" nColumn="+nColumn);
		yRow = new int[nRow+1];
		yRow[0] = vGap;
		fixedRow = new boolean[nRow];
		xColumn = new int[nColumn+1];
		// because of spans, two passes are necessary.
		xColumn[0] = hGap;
		fixedColumn = new boolean[nColumn];
		for(int c=0;c<nColumn;++c) {
			int x = xColumn[c];
			for(int r=0;r<nRow;++r) {
				ArrayList<?> row = (ArrayList<?>)cells.get(r);
				if(c>=row.size()) continue;	// Dummy filler
				Cell cell = (Cell)((ArrayList<?>)cells.get(r)).get(c);
				if(cell==null) continue;
				Dimension sz = cell.component.getPreferredSize();
				int c2 = c + cell.columnSpan;
				xColumn[c2] = Math.max(xColumn[c2],x+sz.width);
				if((cell.align&YFIXED)!=0) fixedRow[r] = true;
				if((cell.align&XFIXED)!=0) fixedColumn[r] = true;
			}
		}
		yRow[0] = vGap;
		for(int r=0;r<nRow;++r) {
			int y = yRow[r];
			yRow[r+1] = Math.max(yRow[r+1],y);	// in case row is all nulls
			ArrayList<?> row = (ArrayList<?>)cells.get(r);
			int nCol = row.size();
			for(int c=0;c<nCol;++c) {
				Cell cell = (Cell)row.get(c);
				if(cell==null) continue;
				Dimension sz = cell.component.getPreferredSize();
				int r2 = r + cell.rowSpan;
				yRow[r2] = Math.max(yRow[r2],y+sz.height);
			}
		}
//dump();
//System.err.println("EXIT initialize layout container@"+target.hashCode());		
	}
	public void reconfigure() {
		yRow = null;
	}		
	public Dimension preferredLayoutSize(Container target) {
		if(yRow==null)
			initializeLayout(target);
		return new Dimension( xColumn[nColumn]+hGap, yRow[nRow]+vGap );
	}
	public void layoutContainer(Container target) {
		// Compute actual grid layout for target at possibly
		// other than preferred size.
//System.err.println("layout container@"+target.hashCode());
		int[] xC = new int[nColumn+1];
		int[] yR = new int[nRow+1];
		Dimension tgtSz = target.getSize();
		int tgtW = tgtSz.width-hGap, tgtH = tgtSz.height-vGap;
		if(tgtW<0) tgtW=0;
		if(tgtH<0) tgtH=0;
		int prfW = xColumn[nColumn], prfH = yRow[nRow];
		int yDelta = tgtH-prfH;	// excess(or deficient) height
		int[] yAdjust = new int[nRow+1];
		int kk = 0;
		for(int k=1;k<=nRow;++k) {
			if(!fixedRow[k-1]) kk = k;
			yAdjust[k] = kk*yDelta/nRow;
		}
		yR[0] = yRow[0];
		for(int k=1;k<=nRow;++k)	// distribute yDelta to positions
			yR[k] = yRow[k] + yAdjust[k];	
		int xDelta = tgtW-prfW;	// excess(or deficient) width
//		for(int k=0;k<=nRow;++k)
		int[] xAdjust = new int[nColumn+1];
		kk = 0;
		for(int k=1;k<=nColumn;++k) {
			if(!fixedColumn[k-1]) kk = k;
			xAdjust[k] = kk*xDelta/nColumn;
		}
		xC[0] = xColumn[0];
		for(int k=1;k<=nColumn;++k)	// distribute xDelta to positions
			xC[k] = xColumn[k] + xAdjust[k];			
//for(int r=0;r<nRow;++r)
//System.err.println("yR["+r+"]="+yR[r]);
//for(int c=0;c<nRow;++c)
//System.err.println("xC["+c+"]="+xC[c]);
		for(int r=0;r<nRow;++r) {
			ArrayList<?> row = (ArrayList<?>)cells.get(r);
			int nCol = row.size();
			int y=yR[r];
			for(int c=0;c<nCol;++c) {
				Cell cell = (Cell)row.get(c);
				if(cell==null) continue;	// dummy filler
				Component comp = cell.component;
				int hgt = yR[r+cell.rowSpan]-y;	// Heighth of space
				int x=xC[c];
				int wid = xC[c+cell.columnSpan]-x;	// Width of space
//System.err.println("x="+x+" y="+y+" wid="+wid+" hgt="+hgt+" comp="+comp);
				Dimension prefSz = comp.getPreferredSize();
				int eWid = Math.min(wid,prefSz.width);	// Effective width
				int eHgt = Math.min(hgt,prefSz.height);	// Effective height
				switch(cell.align&WIDE) {
					case 0: x += (wid-eWid)/2; break;
					case RIGHT: x += (wid-eWid); break;
					case WIDE: eWid=wid; break;
				}
				int ey = y;	// effective y - where actually put the component
				switch(cell.align&HIGH) {
					case 0: ey += (hgt-eHgt)/2; break;
					case BOTTOM: ey += (hgt-eHgt); break;
					case HIGH: eHgt=hgt;
				}
				comp.setBounds(x,ey,eWid,eHgt);
			}
		}
//System.err.println("EXIT layout container@"+target.hashCode());
	}
	static class Cell {
		/////////////////////////
		Component component;
		int align, rowSpan, columnSpan;
		/////////////////////////
		Cell(Component c) { component=c; }
		void setAlign(String s) {
			// Note "LRTB" would be WIDE and TALL, "" would be centered
			align = 0;
			rowSpan = columnSpan = 1;
			int nch = s.length();
			int k=0;
			int arg=0;
			while(k<nch) {
				char ch = s.charAt(k++);
				if(Character.isDigit(ch)) {
					arg = 10*arg + Character.digit(ch,10);
					continue;
				}
				switch(ch) {
					case 'L': align |= LEFT; break;
					case 'R': align |= RIGHT; break;
					case 'T': align |= TOP; break;
					case 'B': align |= BOTTOM; break;
					case 'W': columnSpan = arg; break;
					case 'H': rowSpan = arg; break;
					case 'X': align |= XFIXED; break;
					case 'Y': align |= YFIXED; break;
					case '.': align |= ENDROW; break;
					default: throw new IllegalArgumentException("Unknown Alignment");
				}
				arg=0;
			}
		}
	}
}
